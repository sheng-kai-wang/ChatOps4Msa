package ntou.soselab.chatops4msa.Entity.Capability.DevOpsTool.LowCode;

import com.fasterxml.jackson.annotation.JsonProperty;
import ntou.soselab.chatops4msa.Entity.Capability.Secret;
import org.springframework.stereotype.Component;

import java.util.HashMap;
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

        // on message operation verify
        errorMessage = functionVerify("on_message", onMessageList);
        if (!"".equals(errorMessage)) sb.append(errorMessage);

        return sb.toString();
    }

    private String functionVerify(String functionType, List<DeclaredFunction> functionList) {
        if (functionList == null) return "";

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < functionList.size(); i++) {
            String errorMessage = functionList.get(i).verify();
            if (!"".equals(errorMessage)) {
                sb.append("    ").append(functionType).append("[").append(i).append("] error:").append("\n");
                sb.append(errorMessage).append("\n");
            }
        }

        return sb.toString();
    }

    public String variableRetrieveAndVerify(Secret secret) {
        if (propertyMap == null) return "";

        // verify and update the property
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> entry : propertyMap.entrySet()) {
            List<String> extractedVariableList = LowCodeVariableParser.extractVariableList(entry.getValue());
            for (String variable : extractedVariableList) {
                if (!variable.contains("secret.")) {
                    sb.append("    The variable value of the property can only be obtained from \"secret.yml\"").append("\n");
                } else {
                    variable = variable.replace("secret.", "");
                    String variableValue = secret.getSecretProperty(variable);
                    if (variableValue == null) {
                        sb.append("    the variable[").append(variable).append("] is not set in \"secret.yml\"").append("\n");
                    } else {
                        propertyMap.put(entry.getKey(), variableValue);
                    }
                }
            }
        }

        // put the secret properties into the devops tool properties
        for (Map.Entry<String, String> entry : secret.getSecretPropertyMap().entrySet()) {
            String newPropertyName = "secret." + entry.getKey();
            if (propertyMap.containsKey(newPropertyName)) continue;
            propertyMap.put(newPropertyName, entry.getValue());
        }

        // verify the local variable of constructor
        String errorMessage = functionVariableVerify("constructor", constructorList);
        if (!"".equals(errorMessage)) sb.append(errorMessage);

        // verify the local variable of operation
        errorMessage = functionVariableVerify("operation", operationList);
        if (!"".equals(errorMessage)) sb.append(errorMessage);

        // verify the local variable of on message operation
        errorMessage = functionVariableVerify("on_message", onMessageList);
        if (!"".equals(errorMessage)) sb.append(errorMessage);

        return sb.toString();
    }

    private String functionVariableVerify(String functionType, List<DeclaredFunction> functionList) {
        if (functionList == null) return "";

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < functionList.size(); i++) {
            String errorMessage = functionList.get(i).localVariableVerify(new HashMap<>(propertyMap));
            if (!"".equals(errorMessage)) {
                sb.append("    ").append(functionType).append("[").append(i).append("] error:").append("\n");
                sb.append(errorMessage).append("\n");
            }
        }

        return sb.toString();
    }
}
