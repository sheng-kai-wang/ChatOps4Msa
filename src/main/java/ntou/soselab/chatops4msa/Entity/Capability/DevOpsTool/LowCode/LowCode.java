package ntou.soselab.chatops4msa.Entity.Capability.DevOpsTool.LowCode;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

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
}
