package ntou.soselab.chatops4msa.Entity.Capability.DevOpsTool.LowCode;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

public class DeclaredFunction {

    private Map<String, String> localVariableMap;
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

    public String verify() {
        StringBuilder declaredFunctionSb = new StringBuilder();

        if (name == null) declaredFunctionSb.append("      name is null").append("\n");
        if (description == null) declaredFunctionSb.append("      description is null").append("\n");

        // access verify
        String errorMessage = access.verify();
        if (!"".equals(errorMessage)) declaredFunctionSb.append(errorMessage).append("\n");

        // body verify
        if (invokedFunctionList == null) {
            declaredFunctionSb.append("      body error:").append("\n")
                    .append("        there is no body").append("\n");

        } else {
            if (invokedFunctionList.size() == 0) {
                declaredFunctionSb.append("      body error:").append("\n")
                        .append("        the body has no content").append("\n");
            }
            StringBuilder invokedFunctionSb = new StringBuilder();
            for (int i = 0; i < invokedFunctionList.size(); i++) {
                String invokedFunctionErrorMessage = invokedFunctionList.get(i).verify();
                if (!"".equals(invokedFunctionErrorMessage)) {
                    invokedFunctionSb.append("        function[").append(i).append("] error:").append("\n")
                            .append(invokedFunctionErrorMessage).append("\n");
                }
            }
            String allInvokedFunctionErrorMessage = invokedFunctionSb.toString();
            if (!"".equals(allInvokedFunctionErrorMessage)) {
                declaredFunctionSb.append("      body error:").append("\n")
                        .append(allInvokedFunctionErrorMessage).append("\n");
            }
        }

        return declaredFunctionSb.toString();
    }
}
