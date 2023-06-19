package ntou.soselab.chatops4msa.Entity.Capability.DevOpsTool.LowCode;

import com.fasterxml.jackson.annotation.JsonProperty;

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

    private Map<String, String> localVariableMap;

    public String getFunctionName() {
        return this.name;
    }

    public boolean isPrivate() {
        return "private".equals(access.getAccess());
    }

    public String verify() {
        StringBuilder declaredFunctionSb = new StringBuilder();

        if (name == null) declaredFunctionSb.append("      name is null").append("\n");
        if (description == null) declaredFunctionSb.append("      description is null").append("\n");

        // access verify
        declaredFunctionSb.append(access.verify());

        // body verify
        if (invokedFunctionList == null) {
            declaredFunctionSb.append("      body error:").append("\n");
            declaredFunctionSb.append("        there is no body").append("\n");

        } else {
            if (invokedFunctionList.size() == 0) {
                declaredFunctionSb.append("      body error:").append("\n");
                declaredFunctionSb.append("        the body has no content").append("\n");
            }
            StringBuilder invokedFunctionSb = new StringBuilder();
            for (int i = 0; i < invokedFunctionList.size(); i++) {
                String invokedFunctionErrorMessage = invokedFunctionList.get(i).verify("");
                if (!"".equals(invokedFunctionErrorMessage)) {
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
            if (!"".equals(errorMessage)) {
                sb.append("      body[").append(i).append("] error:").append("\n");
                sb.append(errorMessage).append("\n");
            }
            // initial the intermediate variables of invoked function
            String functionName = function.getName();
            if (functionName != null && !"".equals(functionName)) localVariableMap.put(functionName, null);
            String functionAssign = function.getAssign();
            if (functionAssign != null && !"".equals(functionAssign)) localVariableMap.put(functionAssign, null);
        }

        return sb.toString();
    }
}
