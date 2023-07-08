package ntou.soselab.chatops4msa.Entity.CapabilityConfig.MicroserviceSystem;

import com.fasterxml.jackson.annotation.JsonProperty;
import ntou.soselab.chatops4msa.Entity.CapabilityConfig.Configs;
import ntou.soselab.chatops4msa.Entity.CapabilityConfig.Info;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MicroserviceSystem implements Configs {

    @JsonProperty("info")
    private Info info;
    private Map<String, Service> serviceMap;
    @JsonProperty("capability")
    private List<String> capabilityList;

    private final transient StringBuilder errorMessageSb = new StringBuilder();

    /**
     * service verify
     */
    @JsonProperty("service")
    private void setServiceMapAndVerify(List<Service> serviceList) {
        if (serviceList == null || serviceList.isEmpty()) {
            errorMessageSb.append("  service error:").append("\n");
            errorMessageSb.append("    the service has NO content").append("\n");
            return;
        }

        Map<String, Service> map = new HashMap<>();
        for (int i = 0; i < serviceList.size(); i++) {
            Service currentService = serviceList.get(i);
            String serviceErrorMessage = currentService.verify();
            if (!serviceErrorMessage.isEmpty()) {
                errorMessageSb.append("  service[").append(i).append("] error:").append("\n");
                errorMessageSb.append(serviceErrorMessage).append("\n");
            }
            map.put(currentService.getProperty("name"), currentService);
        }

        this.serviceMap = map;
    }

    public List<String> getProperty(String serviceName, String info) {
        List<String> list = new ArrayList<>();
        if ("all_service".equals(serviceName)) {
            for (Service service : serviceMap.values()) {
                list.add(service.getProperty(info));
            }
        } else if (serviceMap.containsKey(serviceName)) {
            list.add(serviceMap.get(serviceName).getProperty(info));
        }
        return list;
    }

    public List<String> getAllServiceNameList() {
        return new ArrayList<>(serviceMap.keySet());
    }

    public List<String> getCapabilityList() {
        return this.capabilityList;
    }

    public String verify() {

        // info verify
        StringBuilder infoSb = new StringBuilder();
        String infoErrorMessage = info.verify();
        if (!infoErrorMessage.isEmpty()) {
            infoSb.append("  info error:").append("\n");
            infoSb.append(infoErrorMessage).append("\n");
        }
        errorMessageSb.insert(0, infoSb);

        // capability verify
        if (capabilityList == null || capabilityList.isEmpty()) {
            errorMessageSb.append("  capability error:").append("\n");
            errorMessageSb.append("    the capability has NO content").append("\n");
        }

        return errorMessageSb.toString();
    }
}
