package ntou.soselab.chatops4msa.Entity.Capability.DevOpsTool.LowCode;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.*;

public class InvokedFunction {
    private String functionName;
    private Map<String, String> parameterMap;
    private String assign;
    private List<InvokedFunction> todoList;
    private List<InvokedFunction> trueList;
    private List<InvokedFunction> falseList;

    private transient StringBuilder errorMessageSb = new StringBuilder();
    private transient final Map<String, List<String>> toolkitVerifyConfigMap;

    // within the DeclaredFunction, it is shared with all InvokedFunction (object reference)
    private transient Map<String, String> currentVariableMap;

    public InvokedFunction() {
        this.toolkitVerifyConfigMap = ToolkitVerifyConfigLoader.CONFIG_MAP;
    }

    @JsonAnySetter
    public void setFunctionContent(String functionName, Map<String, Object> parameterMap) {
        this.functionName = functionName;
        this.parameterMap = new HashMap<>();

        if (parameterMap == null) return;
        for (Map.Entry<String, Object> entry : parameterMap.entrySet()) {
            String parameterName = entry.getKey();
            Object parameterValue = entry.getValue();
            if (parameterValue instanceof String) {
                if ("assign".equals(parameterName)) this.assign = (String) parameterValue;
                else this.parameterMap.put(parameterName, (String) parameterValue);

            } else if (parameterValue instanceof Integer) {
                this.parameterMap.put(parameterName, parameterValue.toString());

            } else if (parameterValue instanceof List<?> specialParameter) {
                if (specialParameter.isEmpty()) appendParameterFormatErrorMessage(parameterName);
                List<InvokedFunction> specialParameterFunctionList = new ArrayList<>();
                ObjectMapper mapper = new ObjectMapper();
                for (Object obj : specialParameter) {
                    InvokedFunction invokedFunction = mapper.convertValue(obj, InvokedFunction.class);
                    specialParameterFunctionList.add(invokedFunction);
                }
                if ("todo".equals(parameterName)) this.todoList = specialParameterFunctionList;
                else if ("true".equals(parameterName)) this.trueList = specialParameterFunctionList;
                else if ("false".equals(parameterName)) this.falseList = specialParameterFunctionList;
                else appendParameterFormatErrorMessage(parameterName);

            } else {
                appendParameterFormatErrorMessage(parameterName);
            }
        }
    }

    private void appendParameterFormatErrorMessage(String parameterName) {
        errorMessageSb
                .append("          the parameter format [")
                .append(parameterName)
                .append("] is incorrect")
                .append("\n");
    }

    public String getName() {
        return this.functionName;
    }

    public String getAssign() {
        return this.assign;
    }

    public String verify(String indent) {
        // name verify
        if (functionName == null) errorMessageSb.append("          name is null").append("\n");

        // parameter verify
        if (!toolkitVerifyConfigMap.containsKey(functionName)) return errorMessageSb.toString();
        for (String requiredParameter : toolkitVerifyConfigMap.get(functionName)) {
            if (!parameterMap.containsKey(requiredParameter)) {
                if ("todo".equals(requiredParameter) && todoList != null) continue;
                if ("true".equals(requiredParameter) && trueList != null) continue;
                if ("false".equals(requiredParameter) && falseList != null) continue;
                errorMessageSb
                        .append(indent)
                        .append("        the parameter [")
                        .append(requiredParameter)
                        .append("] is missing in ")
                        .append(functionName)
                        .append("\n");
            }
        }

        // special parameter verify
        verifySpecialParameter("todo", todoList, indent);
        verifySpecialParameter("true", trueList, indent);
        verifySpecialParameter("false", falseList, indent);

        return errorMessageSb.toString();
    }

    private void verifySpecialParameter(String parameterName, List<InvokedFunction> specialParameter, String indent) {
        if (specialParameter == null) return;
        for (InvokedFunction function : specialParameter) {
            String errorMessage = function.verify(indent + "  ");
            if (!"".equals(errorMessage)) {
                errorMessageSb.append(indent).append("        ").append(parameterName).append(" function error:").append("\n");
                errorMessageSb.append(indent).append(errorMessage).append("\n");
            }
        }
    }

    public String variableRetrievalVerify(Map<String, String> localVariableMap, String indent) {
        StringBuilder sb = new StringBuilder();

        // update the current variable map
        this.currentVariableMap = localVariableMap;

        // verify the parameter
        for (String parameterValue : parameterMap.values()) {
            List<String> extractedVariableList = LowCodeVariableParser.extractVariableList(parameterValue);
            for (String extractedVariable : extractedVariableList) {
                if (currentVariableMap.containsKey(extractedVariable)) continue;
                sb.append(indent).append("        the variable[").append(extractedVariable).append("] has not been assigned").append("\n");
            }
        }

        // special parameter verify
        variableRetrievalVerifyOfSpecialParameter("todo", todoList, localVariableMap, sb, indent);
        variableRetrievalVerifyOfSpecialParameter("true", trueList, localVariableMap, sb, indent);
        variableRetrievalVerifyOfSpecialParameter("false", falseList, localVariableMap, sb, indent);

        return sb.toString();
    }

    private void variableRetrievalVerifyOfSpecialParameter(String parameterName,
                                                           List<InvokedFunction> specialParameter,
                                                           Map<String, String> currentVariableMap,
                                                           StringBuilder sb,
                                                           String indent) {
        if (specialParameter == null) return;
        if ("todo".equals(parameterName)) currentVariableMap.put(parameterMap.get("element_name"), null);
        for (InvokedFunction function : specialParameter) {
            String errorMessage = function.variableRetrievalVerify(currentVariableMap, indent + "  ");
            if (!"".equals(errorMessage)) {
                sb.append(indent).append("        ").append(parameterName).append(" function error:").append("\n");
                sb.append(indent).append(errorMessage).append("\n");
            }
        }
    }
}
