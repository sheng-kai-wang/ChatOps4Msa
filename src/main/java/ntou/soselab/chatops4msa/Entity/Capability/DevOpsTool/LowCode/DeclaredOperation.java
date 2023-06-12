package ntou.soselab.chatops4msa.Entity.Capability.DevOpsTool.LowCode;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

public class DeclaredOperation {

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
    private List<InvokedOperation> invokedOperationList;

    public String verify() {
        StringBuilder sb = new StringBuilder();

        if (name == null) sb.append("      name is null").append("\n");
        if (description == null) sb.append("      description is null").append("\n");

        // access verify
        String errorMessage = access.verify();
        if (!"".equals(errorMessage)) sb.append(errorMessage).append("\n");

        //TODO: invoked operation verify

        return sb.toString();
    }
}
