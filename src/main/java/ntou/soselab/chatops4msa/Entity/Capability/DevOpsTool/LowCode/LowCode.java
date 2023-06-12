package ntou.soselab.chatops4msa.Entity.Capability.DevOpsTool.LowCode;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

public class LowCode {

    @JsonProperty("property")
    private Map<String, String> propertyMap;
    @JsonProperty("constructor")
    private List<DeclaredOperation> constructorList;
    @JsonProperty("operation")
    private List<DeclaredOperation> operationList;
    @JsonProperty("on_message")
    private List<DeclaredOperation> onMessageList;

    public String verify() {
        StringBuilder sb = new StringBuilder();

        // constructor verify
        String errorMessage = operationVerify("constructor", constructorList);
        if (!"".equals(errorMessage)) sb.append(errorMessage);

        // operation verify
        errorMessage = operationVerify("operation", operationList);
        if (!"".equals(errorMessage)) sb.append(errorMessage);

        // constructor verify
        errorMessage = operationVerify("on_message", onMessageList);
        if (!"".equals(errorMessage)) sb.append(errorMessage);

        return sb.toString();
    }

    private String operationVerify(String type, List<DeclaredOperation> operationList) {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < operationList.size(); i++) {
            String operationErrorMessage = operationList.get(i).verify();
            if (!"".equals(operationErrorMessage)) {
                sb.append("    ").append(type).append("[").append(i).append("] error:").append("\n")
                        .append(operationErrorMessage).append("\n");
            }
        }

        String allOperationErrorMessage = sb.toString();
        if (!"".equals(allOperationErrorMessage)) {
            sb.append("  ").append(type).append(" error:").append("\n")
                    .append(allOperationErrorMessage).append("\n");
        }
        return sb.toString();
    }
}
