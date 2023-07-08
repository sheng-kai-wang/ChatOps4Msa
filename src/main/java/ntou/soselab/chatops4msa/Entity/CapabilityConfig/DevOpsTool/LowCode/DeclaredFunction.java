package ntou.soselab.chatops4msa.Entity.CapabilityConfig.DevOpsTool.LowCode;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DeclaredFunction {
    @JsonProperty("name")
    private String name;
    @JsonProperty("parameter")
    private Map<String, String> parameterDescriptionMap;
    @JsonProperty("description")
    private String description;
    @JsonProperty("access")
    private AccessPermission access;
    @JsonProperty("body")
    private List<InvokedFunction> invokedFunctionList;

    private Map<String, String> localVariableMap = new HashMap<>();

    public String getName() {
        return this.name;
    }

    public Map<String, String> getParameterDescriptionMap() {
        if (parameterDescriptionMap == null) return new HashMap<>();
        return this.parameterDescriptionMap;
    }

    public String getDescription() {
        return this.description;
    }

    public AccessPermission getAccess() {
        return this.access;
    }

    public boolean isPrivate() {
        return "private".equals(this.access.getAccess());
    }

    public List<InvokedFunction> getAllInvokedFunctionList() {
        return this.invokedFunctionList;
    }

    public Map<String, String> getLocalVariableMap() {
        return this.localVariableMap;
    }

    public String verify() {
        StringBuilder declaredFunctionSb = new StringBuilder();

        if (name == null) declaredFunctionSb.append("      there is NO name").append("\n");
        if (description == null) declaredFunctionSb.append("      there is NO description").append("\n");

        // access verify
        declaredFunctionSb.append(access.verify());

        // body verify
        if (invokedFunctionList == null) {
            declaredFunctionSb.append("      body error:").append("\n");
            declaredFunctionSb.append("        there is NO body").append("\n");

        } else {
            if (invokedFunctionList.size() == 0) {
                declaredFunctionSb.append("      body error:").append("\n");
                declaredFunctionSb.append("        the body has NO content").append("\n");
            }
            StringBuilder invokedFunctionSb = new StringBuilder();
            for (int i = 0; i < invokedFunctionList.size(); i++) {
                String invokedFunctionErrorMessage = invokedFunctionList.get(i).verify("");
                if (!invokedFunctionErrorMessage.isEmpty()) {
                    invokedFunctionSb.append("      body[").append(i).append("] error:").append("\n");
                    invokedFunctionSb.append(invokedFunctionErrorMessage).append("\n");
                }
            }
            declaredFunctionSb.append(invokedFunctionSb);
        }

        return declaredFunctionSb.toString();
    }

    public String localVariableVerify(Map<String, String> copiedPropertyMap) {
        StringBuilder sb = new StringBuilder();

        // initial the properties of the devops tool
        this.localVariableMap = copiedPropertyMap;

        // initial the parameters
        if (parameterDescriptionMap != null) {
            for (String parameterName : parameterDescriptionMap.keySet()) {
                localVariableMap.put(parameterName, null);
            }
        }

        for (int i = 0; i < invokedFunctionList.size(); i++) {
            InvokedFunction function = invokedFunctionList.get(i);
            // verify the variable retrieval of invoked function
            String errorMessage = function.variableRetrievalVerify(localVariableMap, "");
            if (!errorMessage.isEmpty()) {
                sb.append("      body[").append(i).append("] error:").append("\n");
                sb.append(errorMessage).append("\n");
            }
            // initial the intermediate variables of invoked function
            String functionName = function.getName();
            if (functionName != null && !functionName.isEmpty()) localVariableMap.put(functionName, null);
            String functionAssign = function.getAssign();
            if (functionAssign != null && !functionAssign.isEmpty()) localVariableMap.put(functionAssign, null);
        }

        return sb.toString();
    }
}
