package ntou.soselab.chatops4msa.Entity.Capability.MicroserviceSystem;

import com.fasterxml.jackson.annotation.JsonProperty;
import ntou.soselab.chatops4msa.Entity.Capability.Configs;
import ntou.soselab.chatops4msa.Entity.Capability.Info;

import java.util.List;

public class MicroserviceSystem implements Configs {

    @JsonProperty("info")
    private Info info;
    @JsonProperty("service")
    private List<Service> serviceList;
    @JsonProperty("capability")
    private List<String> capabilityList;

    public String verify() {
        StringBuilder systemSb = new StringBuilder();

        // info verify
        String infoErrorMessage = info.verify();
        if (!"".equals(infoErrorMessage)) {
            systemSb.append("  info error:").append("\n")
                    .append(infoErrorMessage).append("\n");
        }

        // service verify
        if (serviceList.size() == 0) {
            systemSb.append("  service error:").append("\n")
                    .append("    the service has no content").append("\n");
        }
        StringBuilder serviceSb = new StringBuilder();
        for (int i = 0; i < serviceList.size(); i++) {
            String serviceErrorMessage = serviceList.get(i).verify();
            if (!"".equals(serviceErrorMessage)) {
             serviceSb.append("    service[").append(i).append("] error:").append("\n")
                     .append(serviceErrorMessage).append("\n");
            }
        }
        String allServiceErrorMessage = serviceSb.toString();
        if (!"".equals(allServiceErrorMessage)) {
            systemSb.append("  service error:").append("\n")
                    .append(allServiceErrorMessage).append("\n");
        }

        // capability verify
        if (capabilityList.size() == 0) {
            systemSb.append("  capability error:").append("\n")
                    .append("    the capability has no content").append("\n");
        }

        return systemSb.toString();
    }
}
