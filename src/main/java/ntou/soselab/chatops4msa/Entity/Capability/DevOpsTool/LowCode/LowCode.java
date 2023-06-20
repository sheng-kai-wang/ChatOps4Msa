package ntou.soselab.chatops4msa.Entity.Capability.DevOpsTool.LowCode;

import com.fasterxml.jackson.annotation.JsonProperty;
import ntou.soselab.chatops4msa.Entity.Capability.Secret;
import ntou.soselab.chatops4msa.Service.LowCodeService.LowCodeVariableExtractor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
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

    public List<String> getAllDeclaredFunctionNameList() {
        List<String> list = new ArrayList<>();
        if (operationList == null || operationList.isEmpty()) return list;
        for (DeclaredFunction operation : operationList) {
            if (operation.isPrivate()) continue;
            list.add(operation.getFunctionName());
        }
        return list;
    }

    public String verify() {
        StringBuilder sb = new StringBuilder();

        // constructor verify
        sb.append(functionVerify("constructor", constructorList));

        // operation verify
        sb.append(functionVerify("operation", operationList));

        // on message operation verify
        sb.append(functionVerify("on_message", onMessageList));

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
            List<String> extractedVariableList = LowCodeVariableExtractor.extractVariableList(entry.getValue());
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
        sb.append(functionVariableVerify("constructor", constructorList));

        // verify the local variable of operation
        sb.append(functionVariableVerify("operation", operationList));

        // verify the local variable of on message operation
        sb.append(functionVariableVerify("on_message", onMessageList));

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
