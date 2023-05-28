package ntou.soselab.chatops4msa.Service.LowCodeService;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import ntou.soselab.chatops4msa.Entity.Capability.MicroserviceSystem.MicroserviceSystem;
import ntou.soselab.chatops4msa.Exception.IllegalCapabilityConfigException;
import ntou.soselab.chatops4msa.Service.DiscordService.JDAService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Service
public class CapabilityConfigLoader {

    private final ObjectMapper objectMapper;
    private final Gson gson;
    private final StringBuilder errorMessageSb;

    private final String capabilityMicroserviceSystemClasspath;
    private final String capabilityToolClasspath;
    private final String capabilityRabbitmqFile;
    private final String capabilitySecretFile;

    private Map<String, MicroserviceSystem> microserviceSystemMap;

    private final JDAService jdaService;

    @Autowired
    public CapabilityConfigLoader(Environment env, JDAService jdaService) {
        this.objectMapper = new ObjectMapper(new YAMLFactory());
        this.gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
        this.errorMessageSb = new StringBuilder();

        this.capabilityMicroserviceSystemClasspath = env.getProperty("capability.microservice-system.classpath");
        this.capabilityToolClasspath = env.getProperty("capability.tool.classpath");
        this.capabilityRabbitmqFile = env.getProperty("capability.rabbitmq.file");
        this.capabilitySecretFile = env.getProperty("capability.secret.file");

        this.jdaService = jdaService;

        try {
            this.microserviceSystemMap = loadMicroserviceSystemConfig();
            checkVerifyMessage();

        } catch (IllegalCapabilityConfigException e) {
            String failedMessage = "[ERROR] Capability Configs Verification Failed:";
            System.out.println(failedMessage);
            System.out.println(e.getMessage());
            jdaService.sendChatOpsChannelPropertiesMessage(failedMessage);
            jdaService.sendChatOpsChannelPropertiesMessage(e.getMessage());
            e.printStackTrace();
        }
    }

    private Map<String, MicroserviceSystem> loadMicroserviceSystemConfig() {
        System.out.println("[DEBUG] start to load microservice system configs");
        HashMap<String, MicroserviceSystem> microserviceSystemObjMap = new HashMap<>();
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        Resource[] resources;
        try {
            resources = new PathMatchingResourcePatternResolver(classLoader).getResources(capabilityMicroserviceSystemClasspath);
            if (resources.length == 0) {
                System.out.println("[ERROR] there is NO microservice system configs");
                errorMessageSb.append("there is NO microservice system configs").append("\n");
            }

            for (Resource resource : resources) {
                String fileName = resource.getFilename();
                System.out.println("[DEBUG] try to load " + fileName);
                String nameWithoutExtension = getFileNameWithoutExtension(fileName);
                MicroserviceSystem microserviceSystemObj = objectMapper.readValue(resource.getInputStream(), MicroserviceSystem.class);

                System.out.println("[DEBUG] the content of " + nameWithoutExtension + ": ");
                System.out.println(gson.toJson(microserviceSystemObj));

                String systemErrorMessage = microserviceSystemObj.verify();
                if (!"".equals(systemErrorMessage)) errorMessageSb.append(systemErrorMessage).append("\n");

                microserviceSystemObjMap.put(nameWithoutExtension, microserviceSystemObj);
            }

        } catch (IOException e) {
            e.printStackTrace();

        }
        return microserviceSystemObjMap;
    }

    private String getFileNameWithoutExtension(String fileName) {
        if (fileName == null) return null;
        int lastDotIndex;
        lastDotIndex = fileName.lastIndexOf('.');
        return (lastDotIndex != -1) ? fileName.substring(0, lastDotIndex) : fileName;
    }

    private void checkVerifyMessage() throws IllegalCapabilityConfigException {
        String errorMessage = errorMessageSb.toString();
        if (!"".equals(errorMessage)) throw new IllegalCapabilityConfigException(errorMessage);
        else {
            String passedMessage = "[INFO] Capability Configs Verification Passed";
            System.out.println(passedMessage);
            jdaService.sendChatOpsChannelPropertiesMessage(passedMessage);
        }
    }
}
