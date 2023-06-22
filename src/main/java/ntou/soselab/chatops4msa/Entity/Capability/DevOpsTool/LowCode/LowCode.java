package ntou.soselab.chatops4msa.Entity.Capability.DevOpsTool.LowCode;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
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
    private Map<String, DeclaredFunction> constructorMap;
    private Map<String, DeclaredFunction> operationMap;
    private Map<String, DeclaredFunction> onMessageMap;

    private final transient StringBuilder errorMessageSb = new StringBuilder();

    @JsonSetter("constructor")
    private void setConstructorMapAndVerify(List<DeclaredFunction> constructorList) {
        this.constructorMap = functionListToMapAndVerify("constructor", constructorList);
    }

    @JsonSetter("operation")
    private void setOperationMapAndVerify(List<DeclaredFunction> operationList) {
        this.operationMap = functionListToMapAndVerify("operation", operationList);
    }

    @JsonSetter("on_message")
    private void setOnMessageMapAndVerify(List<DeclaredFunction> onMessageList) {
        this.onMessageMap = functionListToMapAndVerify("constructor", onMessageList);
    }

    public List<String> getAllDeclaredFunctionNameList() {
        List<String> list = new ArrayList<>();
        list.addAll(getAllDeclaredFunctionNameList(constructorMap));
        list.addAll(getAllDeclaredFunctionNameList(operationMap));
        list.addAll(getAllDeclaredFunctionNameList(onMessageMap));
        return list;
    }

    private List<String> getAllDeclaredFunctionNameList(Map<String, DeclaredFunction> functionMap) {
        List<String> list = new ArrayList<>();
        if (functionMap == null || functionMap.isEmpty()) return list;
        for (DeclaredFunction function : functionMap.values()) {
            list.add(function.getName());
        }
        return list;
    }

    public List<DeclaredFunction> getAllDeclaredFunctionObjList() {
        List<DeclaredFunction> list = new ArrayList<>();
        list.addAll(getAllDeclaredFunctionObjList(constructorMap));
        list.addAll(getAllDeclaredFunctionObjList(operationMap));
        list.addAll(getAllDeclaredFunctionObjList(onMessageMap));
        return list;
    }

    private List<DeclaredFunction> getAllDeclaredFunctionObjList(Map<String, DeclaredFunction> functionMap) {
        List<DeclaredFunction> list = new ArrayList<>();
        if (functionMap == null || functionMap.isEmpty()) return list;
        list.addAll(functionMap.values());
        return list;
    }

    private Map<String, DeclaredFunction> functionListToMapAndVerify(String functionType, List<DeclaredFunction> functionList) {
        if (functionList == null || functionList.isEmpty()) {
            errorMessageSb.append("    ").append(functionType).append(" error:").append("\n");
            errorMessageSb.append("      there is NO content").append("\n");
            return null;
        }

        Map<String, DeclaredFunction> map = new HashMap<>();
        for (int i = 0; i < functionList.size(); i++) {
            DeclaredFunction currentFunction = functionList.get(i);
            String functionErrorMessage = currentFunction.verify();
            if (!functionErrorMessage.isEmpty()) {
                errorMessageSb.append("    ").append(functionType).append("[").append(i).append("] error:").append("\n");
                errorMessageSb.append(functionErrorMessage).append("\n");
            }
            map.put(currentFunction.getName(), currentFunction);
        }
        return map;
    }

    public String verify() {
        return errorMessageSb.toString();
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
        sb.append(functionVariableVerify("constructor", constructorMap));

        // verify the local variable of operation
        sb.append(functionVariableVerify("operation", operationMap));

        // verify the local variable of on message operation
        sb.append(functionVariableVerify("on_message", onMessageMap));

        return sb.toString();
    }

    private String functionVariableVerify(String functionType, Map<String, DeclaredFunction> functionMap) {
        if (functionMap == null) return "";

        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, DeclaredFunction> entry : functionMap.entrySet()) {
            String errorMessage = entry.getValue().localVariableVerify(new HashMap<>(propertyMap));
            if (!errorMessage.isEmpty()) {
                sb.append("    ").append(functionType).append("[").append(entry.getKey()).append("] error:").append("\n");
                sb.append(errorMessage).append("\n");
            }
        }

        return sb.toString();
    }
}
