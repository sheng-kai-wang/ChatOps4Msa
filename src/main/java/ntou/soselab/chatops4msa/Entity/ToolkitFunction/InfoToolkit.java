package ntou.soselab.chatops4msa.Entity.ToolkitFunction;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import ntou.soselab.chatops4msa.Entity.CapabilityConfig.MicroserviceSystem.MicroserviceSystem;
import ntou.soselab.chatops4msa.Exception.ToolkitFunctionException;
import ntou.soselab.chatops4msa.Service.LowCodeService.CapabilityConfigLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class InfoToolkit extends ToolkitFunction {
    private final CapabilityConfigLoader configLoader;

    @Autowired
    public InfoToolkit(CapabilityConfigLoader configLoader) {
        this.configLoader = configLoader;
    }

    public String toolkitInfoGet(String system, String service, String info) throws ToolkitFunctionException {
        MicroserviceSystem microserviceSystem = configLoader.microserviceSystemMap.get(system);
        List<String> list = microserviceSystem.getProperty(service, info);
        String json;
        try {
            json = new ObjectMapper().writeValueAsString(list);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            throw new ToolkitFunctionException("toolkit-info-get error");
        }
        return json;
    }
}
