package ntou.soselab.chatops4msa.Entity.Capability.MicroserviceSystem;

import com.fasterxml.jackson.annotation.JsonProperty;
import ntou.soselab.chatops4msa.Entity.Capability.Configs;
import ntou.soselab.chatops4msa.Entity.Capability.Info;

import java.util.ArrayList;
import java.util.List;

public class MicroserviceSystem implements Configs {

    @JsonProperty("info")
    private Info info;
    @JsonProperty("service")
    private List<Service> serviceList;
    @JsonProperty("capability")
    private List<String> capabilityList;

    public List<String> getProperty(String serviceName, String info) {
        List<String> list = new ArrayList<>();
        if ("all_service".equals(serviceName)) {
            for (Service service : serviceList) {
                list.add(service.getProperty(info));
            }
        }
        // TODO: all configs list to map
//        if (serviceList.contains(serviceName)) list.add(serviceList.get(serviceName))
        return list;
    }

    public List<String> getCapabilityList() {
        return this.capabilityList;
    }

    public String verify() {
        StringBuilder systemSb = new StringBuilder();

        // info verify
        String infoErrorMessage = info.verify();
        if (!"".equals(infoErrorMessage)) {
            systemSb.append("  info error:").append("\n");
            systemSb.append(infoErrorMessage).append("\n");
        }

        // service verify
        if (serviceList == null || serviceList.size() == 0) {
            systemSb.append("  service error:").append("\n");
            systemSb.append("    the service has no content").append("\n");
        }
        StringBuilder serviceSb = new StringBuilder();
        if (serviceList != null) {
            for (int i = 0; i < serviceList.size(); i++) {
                String serviceErrorMessage = serviceList.get(i).verify();
                if (!"".equals(serviceErrorMessage)) {
                    serviceSb.append("    service[").append(i).append("] error:").append("\n");
                    serviceSb.append(serviceErrorMessage).append("\n");
                }
            }
        }
        String allServiceErrorMessage = serviceSb.toString();
        if (!"".equals(allServiceErrorMessage)) {
            systemSb.append("  service error:").append("\n");
            systemSb.append(allServiceErrorMessage).append("\n");
        }

        // capability verify
        if (capabilityList == null || capabilityList.size() == 0) {
            systemSb.append("  capability error:").append("\n");
            systemSb.append("    the capability has no content").append("\n");
        }

        return systemSb.toString();
    }
}
