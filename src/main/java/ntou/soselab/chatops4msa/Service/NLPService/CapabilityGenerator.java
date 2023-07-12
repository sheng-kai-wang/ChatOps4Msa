package ntou.soselab.chatops4msa.Service.NLPService;

import jakarta.annotation.PostConstruct;
import ntou.soselab.chatops4msa.Entity.CapabilityConfig.DevOpsTool.LowCode.DeclaredFunction;
import ntou.soselab.chatops4msa.Entity.NLP.Capability;
import ntou.soselab.chatops4msa.Service.LowCodeService.CapabilityConfigLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CapabilityGenerator {
    private final CapabilityConfigLoader configLoader;
    private final Map<String, Capability> capabilityMap = new HashMap<>();
    @Autowired
    public CapabilityGenerator(CapabilityConfigLoader configLoader) {
        this.configLoader = configLoader;
    }

    @PostConstruct
    private void generateIntentAndEntity() {
        Map<String, DeclaredFunction> availableFunctionMap = configLoader.getAllNonPrivateDeclaredFunctionMap();
        for (DeclaredFunction function : availableFunctionMap.values()) {
            String description = function.getDescription();
            String capabilityName = function.getName();
            List<String> parameterList = new ArrayList<>(function.getParameterDescriptionMap().keySet());
            Capability capability = new Capability(description, capabilityName, parameterList);
            capabilityMap.put(capabilityName, capability);
        }
    }

    public String getCapabilityListString() {
        StringBuilder sb = new StringBuilder();
        for (Capability capability : capabilityMap.values()) {
            sb.append(capability.toString());
        }
        return sb.toString();
    }

    public List<String> getParameterList(String capabilityName) {
        return capabilityMap.get(capabilityName).getParameterList();
    }

    public List<String> getServiceNameList() {
        return this.configLoader.getAllServiceNameList();
    }
}
