package ntou.soselab.chatops4msa.Service.ToolkitFunctionService;

import ntou.soselab.chatops4msa.Entity.Capability.MicroserviceSystem.MicroserviceSystem;
import ntou.soselab.chatops4msa.Service.LowCodeService.CapabilityConfigLoader;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.List;

public class InfoToolkit extends ToolkitFunction implements ApplicationContextAware {
    private CapabilityConfigLoader configLoader;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.configLoader = applicationContext.getBean(CapabilityConfigLoader.class);
    }

    public String[] toolkitInfoGet(String system, String service, String info) {
        MicroserviceSystem microserviceSystem = configLoader.microserviceSystemMap.get(system);
        List<String> list = microserviceSystem.getProperty(service, info);
        return list.toArray(new String[0]);
    }
}
