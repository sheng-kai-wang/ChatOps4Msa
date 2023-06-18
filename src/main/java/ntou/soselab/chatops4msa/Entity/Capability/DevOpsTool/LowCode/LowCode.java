package ntou.soselab.chatops4msa.Entity.Capability.DevOpsTool.LowCode;

import com.fasterxml.jackson.annotation.JsonProperty;
import ntou.soselab.chatops4msa.Entity.Capability.Secret;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class LowCode {
    @JsonProperty("property")
    private Map<String, String> propertyMap;
    @JsonProperty("constructor")
    private List<DeclaredFunction> constructorList;
    @JsonProperty("operation")
    private List<DeclaredFunction> operationList;
    @JsonProperty("on_message")
    private List<DeclaredFunction> onMessageList;

    public String verify() {
        StringBuilder sb = new StringBuilder();

        // constructor verify
        String errorMessage = functionVerify("constructor", constructorList);
        if (!"".equals(errorMessage)) sb.append(errorMessage);

        // operation verify
        errorMessage = functionVerify("operation", operationList);
        if (!"".equals(errorMessage)) sb.append(errorMessage);

        // constructor verify
        errorMessage = functionVerify("on_message", onMessageList);
        if (!"".equals(errorMessage)) sb.append(errorMessage);

        return sb.toString();
    }

    private String functionVerify(String type, List<DeclaredFunction> functionList) {
        if (functionList == null) return "";

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < functionList.size(); i++) {
            String operationErrorMessage = functionList.get(i).verify();
            if (!"".equals(operationErrorMessage)) {
                sb.append("    ").append(type).append("[").append(i).append("] error:").append("\n")
                        .append(operationErrorMessage).append("\n");
            }
        }

        return sb.toString();
    }

    /**
     * @return error message
     */
    public String variableRetrieve(Secret secret) {
        if (propertyMap == null) return "";

        // TODO: set the variable into propertyMap
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> entry : propertyMap.entrySet()) {
            List<String> extractedVariableList = LowCodeVariableParser.extractVariableList(entry.getValue());
            for (String variable : extractedVariableList) {
                if (!variable.contains("secret.")) {
                    sb.append("      The variable value of the property can only be obtained from \"secret.yml\"").append("\n");
                } else {
                    variable = variable.replace("secret.", "");
                    String variableValue = secret.getSecretProperty(variable);
                    if (variableValue == null) {
                        sb.append("      the variable[").append(variable).append("] is not set in \"secret.yml\"").append("\n");
                    }
                }
            }
        }
        return sb.toString();
    }
}
