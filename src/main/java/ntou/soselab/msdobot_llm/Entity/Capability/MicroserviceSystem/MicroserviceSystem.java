package ntou.soselab.msdobot_llm.Entity.Capability.MicroserviceSystem;

import com.fasterxml.jackson.annotation.JsonProperty;
import ntou.soselab.msdobot_llm.Entity.Capability.Info;

import java.util.List;

public class MicroserviceSystem {

    @JsonProperty("info")
    private Info info;
    @JsonProperty("service")
    private List<Service> serviceList;
    @JsonProperty("chatops_query_language")
    private List<String> chatopsQueryLanguageList;

    public String verify() {
        StringBuilder systemSb = new StringBuilder();

        // info error
        String infoErrorMessage = info.verify();
        if (!"".equals(infoErrorMessage)) {
            systemSb.append("info error:").append("\n").append(infoErrorMessage).append("\n");
        }

        // service error
        if (serviceList.size() == 0) {
            systemSb.append("service error:").append("\n")
                    .append("  the service has no content").append("\n");
        }
        StringBuilder serviceSb = new StringBuilder();
        for (int i = 0; i < serviceList.size(); i++) {
            String serviceErrorMessage = serviceList.get(i).verify();
            if (!"".equals(serviceErrorMessage)) {
             serviceSb.append("  service[").append(i).append("] error:").append("\n");
             serviceSb.append(serviceErrorMessage).append("\n");
            }
        }
        String allServiceErrorMessage = serviceSb.toString();
        if (!"".equals(allServiceErrorMessage)) {
            systemSb.append("service error:").append("\n").append(allServiceErrorMessage).append("\n");
        }

        // chatops_query_language error
        if (chatopsQueryLanguageList.size() == 0) {
            systemSb.append("chatops_query_language error:").append("\n")
                    .append("  the chatops_query_language has no content").append("\n");
        }

        return systemSb.toString();
    }
}
