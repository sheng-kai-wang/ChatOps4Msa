package ntou.soselab.chatops4msa.Entity.Capability.DevOpsTool.LowCode;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import ntou.soselab.chatops4msa.Service.LowCodeService.LowCodeVariableExtractor;
import ntou.soselab.chatops4msa.Service.LowCodeService.ToolkitVerifyConfigLoader;

import java.util.*;

public class InvokedFunction {
    private String functionName;
    private Map<String, String> argumentMap;
    private String assign;
    private List<InvokedFunction> todoList = new ArrayList<>();
    private List<InvokedFunction> trueList = new ArrayList<>();
    private List<InvokedFunction> falseList = new ArrayList<>();

    private transient final StringBuilder errorMessageSb = new StringBuilder();
    private transient final Map<String, List<String>> toolkitVerifyConfigMap;

    // within the DeclaredFunction, it is shared with all InvokedFunction (object reference)
    private transient Map<String, String> currentVariableMap;

    public InvokedFunction() {
        this.toolkitVerifyConfigMap = ToolkitVerifyConfigLoader.CONFIG_MAP;
    }

    @JsonAnySetter
    public void setFunctionContentAndVerify(String functionName, Map<String, Object> argumentMap) {
        this.functionName = functionName;
        this.argumentMap = new HashMap<>();

        if (argumentMap == null) return;
        for (Map.Entry<String, Object> entry : argumentMap.entrySet()) {
            String argumentName = entry.getKey();
            Object argumentValue = entry.getValue();
            if (argumentValue instanceof String) {
                if ("assign".equals(argumentName)) this.assign = (String) argumentValue;
                else this.argumentMap.put(argumentName, (String) argumentValue);

            } else if (argumentValue instanceof Integer) {
                this.argumentMap.put(argumentName, argumentValue.toString());

            } else if (argumentValue instanceof List<?> specialArgument) {
                if (specialArgument.isEmpty()) appendArgumentFormatErrorMessage(argumentName);
                List<InvokedFunction> specialargumentFunctionList = new ArrayList<>();
                ObjectMapper mapper = new ObjectMapper();
                for (Object obj : specialArgument) {
                    InvokedFunction invokedFunction = mapper.convertValue(obj, InvokedFunction.class);
                    specialargumentFunctionList.add(invokedFunction);
                }
                if ("todo".equals(argumentName)) this.todoList = specialargumentFunctionList;
                else if ("true".equals(argumentName)) this.trueList = specialargumentFunctionList;
                else if ("false".equals(argumentName)) this.falseList = specialargumentFunctionList;
                else appendArgumentFormatErrorMessage(argumentName);

            } else {
                appendArgumentFormatErrorMessage(argumentName);
            }
        }
    }

    private void appendArgumentFormatErrorMessage(String argumentName) {
        errorMessageSb
                .append("          the argument format [")
                .append(argumentName)
                .append("] is incorrect")
                .append("\n");
    }

    public String getName() {
        return this.functionName;
    }

    public Map<String, String> copyArgumentMap() {
        return new HashMap<>(argumentMap);
    }

    public String getAssign() {
        return this.assign;
    }

    public List<InvokedFunction> getTodoList() {
        return this.todoList;
    }

    public List<InvokedFunction> getTrueList() {
        return this.trueList;
    }

    public boolean hasTrueList() {
        return !trueList.isEmpty();
    }

    public boolean hasFalseList() {
        return !falseList.isEmpty();
    }

    public List<InvokedFunction> getFalseList() {
        return this.falseList;
    }

    public String verify(String indent) {
        // name verify
        if (functionName == null) errorMessageSb.append("          there is NO name").append("\n");

        // argument verify
        if (!toolkitVerifyConfigMap.containsKey(functionName)) return errorMessageSb.toString();
        for (String requiredArgument : toolkitVerifyConfigMap.get(functionName)) {
            if (!argumentMap.containsKey(requiredArgument)) {
                if ("todo".equals(requiredArgument) && todoList != null) continue;
                if ("true".equals(requiredArgument) && trueList != null) continue;
                if ("false".equals(requiredArgument) && falseList != null) continue;
                errorMessageSb
                        .append(indent)
                        .append("        the argument [")
                        .append(requiredArgument)
                        .append("] is missing in ")
                        .append(functionName)
                        .append("\n");
            }
        }

        // special argument verify
        verifySpecialArgument("todo", todoList, indent);
        verifySpecialArgument("true", trueList, indent);
        verifySpecialArgument("false", falseList, indent);

        return errorMessageSb.toString();
    }

    private void verifySpecialArgument(String argumentName, List<InvokedFunction> specialArgument, String indent) {
        if (specialArgument == null) return;
        for (InvokedFunction function : specialArgument) {
            String errorMessage = function.verify(indent + "  ");
            if (!errorMessage.isEmpty()) {
                errorMessageSb.append(indent).append("        ").append(argumentName).append(" function error:").append("\n");
                errorMessageSb.append(indent).append(errorMessage).append("\n");
            }
        }
    }

    public String variableRetrievalVerify(Map<String, String> localVariableMap, String indent) {
        StringBuilder sb = new StringBuilder();

        // update the current variable map
        this.currentVariableMap = localVariableMap;

        // verify the argument
        for (String argumentValue : argumentMap.values()) {
            List<String> extractedVariableList = LowCodeVariableExtractor.extractVariableList(argumentValue);
            for (String extractedVariable : extractedVariableList) {
                if (currentVariableMap.containsKey(extractedVariable)) continue;
                sb.append(indent).append("        the variable[").append(extractedVariable).append("] has not been assigned").append("\n");
            }
        }

        // special argument verify
        variableRetrievalVerifyOfSpecialArgument("todo", todoList, localVariableMap, sb, indent);
        variableRetrievalVerifyOfSpecialArgument("true", trueList, localVariableMap, sb, indent);
        variableRetrievalVerifyOfSpecialArgument("false", falseList, localVariableMap, sb, indent);

        return sb.toString();
    }

    private void variableRetrievalVerifyOfSpecialArgument(String argumentName,
                                                          List<InvokedFunction> specialArgument,
                                                          Map<String, String> currentVariableMap,
                                                          StringBuilder sb,
                                                          String indent) {
        if (specialArgument == null) return;
        if ("todo".equals(argumentName)) currentVariableMap.put(argumentMap.get("element_name"), null);
        for (InvokedFunction function : specialArgument) {

            // initial the intermediate variables of invoked function
            String functionName = function.getName();
            if (functionName != null && !functionName.isEmpty()) currentVariableMap.put(functionName, null);
            String functionAssign = function.getAssign();
            if (functionAssign != null && !functionAssign.isEmpty()) currentVariableMap.put(functionAssign, null);

            // verify
            String errorMessage = function.variableRetrievalVerify(currentVariableMap, indent + "  ");
            if (!errorMessage.isEmpty()) {
                sb.append(indent).append("        ").append(argumentName).append(" function error:").append("\n");
                sb.append(indent).append(errorMessage).append("\n");
            }
        }
    }
}
