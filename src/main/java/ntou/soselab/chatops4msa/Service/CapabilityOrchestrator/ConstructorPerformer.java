package ntou.soselab.chatops4msa.Service.CapabilityOrchestrator;

import jakarta.annotation.PostConstruct;
import ntou.soselab.chatops4msa.Exception.ToolkitFunctionException;
import ntou.soselab.chatops4msa.Service.DiscordService.JDAService;
import ntou.soselab.chatops4msa.Service.LowCodeService.CapabilityConfigLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
public class ConstructorPerformer {
    private final CapabilityConfigLoader configLoader;
    private final CapabilityOrchestrator orchestrator;
    private final JDAService jdaService;

    @Autowired
    public ConstructorPerformer(CapabilityConfigLoader configLoader,
                                CapabilityOrchestrator orchestrator,
                                JDAService jdaService) {

        this.configLoader = configLoader;
        this.orchestrator = orchestrator;
        this.jdaService = jdaService;
    }

    @PostConstruct
    private void performTheConstructor() {

        System.out.println(">>> perform all the constructors");

        for (String functionName : configLoader.getAllConstructorNameList()) {
            try {
                orchestrator.performTheCapability(functionName, new HashMap<>());
            } catch (ToolkitFunctionException e) {
                e.printStackTrace();
                String errorMessage = "[ERROR] " + e.getLocalizedMessage();
                System.out.println(errorMessage);
                jdaService.sendChatOpsChannelErrorMessage(errorMessage);
            }
        }

        System.out.println("<<< end of the constructors");
        System.out.println();
    }
}