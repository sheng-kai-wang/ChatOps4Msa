package ntou.soselab.chatops4msa.Service.LowCodeService;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import ntou.soselab.chatops4msa.Entity.Capability.Configs;
import ntou.soselab.chatops4msa.Entity.Capability.DevOpsTool.DevOpsTool;
import ntou.soselab.chatops4msa.Entity.Capability.MessageDelivery;
import ntou.soselab.chatops4msa.Entity.Capability.MicroserviceSystem.MicroserviceSystem;
import ntou.soselab.chatops4msa.Entity.Capability.Secret;
import ntou.soselab.chatops4msa.Exception.IllegalCapabilityConfigException;
import ntou.soselab.chatops4msa.Service.DiscordService.JDAService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Service
public class CapabilityConfigLoader {
    public Map<String, MicroserviceSystem> microserviceSystemMap;
    public Map<String, DevOpsTool> devOpsToolMap;
    public Map<String, MessageDelivery> messageDeliveryMap;
    public Map<String, Secret> secretMap;

    private final JDAService jdaService;
    private final StringBuilder errorMessageSb;

    @Autowired
    public CapabilityConfigLoader(Environment env, JDAService jdaService) {
        this.jdaService = jdaService;
        this.errorMessageSb = new StringBuilder();

//        String microserviceSystemClasspath = env.getProperty("capability.microservice-system.classpath");
        String devOpsToolClasspath = env.getProperty("capability.devops-tool.classpath");
//        String messageDeliveryClasspath = env.getProperty("capability.message-delivery.classpath");
//        String secretClasspath = env.getProperty("capability.secret.classpath");

//        this.microserviceSystemMap = loadConfig("microservice-system", MicroserviceSystem.class, microserviceSystemClasspath);
        this.devOpsToolMap = loadConfig("devops-tool", DevOpsTool.class, devOpsToolClasspath);
//        this.messageDeliveryMap = loadConfig("message-delivery", MessageDelivery.class, messageDeliveryClasspath);
//        this.secretMap = loadConfig("secret", Secret.class, secretClasspath);

        try {
            checkVerifyMessage();
        } catch (IllegalCapabilityConfigException e) {
            String failedMessage = "[ERROR] Capability Configs Verification Failed";
            System.out.println(failedMessage);
            System.out.println(e.getMessage());
            jdaService.sendChatOpsChannelPropertiesMessage(failedMessage);
            jdaService.sendChatOpsChannelPropertiesMessage(e.getMessage());
            e.printStackTrace();
        }
    }

    private <T extends Configs> Map<String, T> loadConfig(String configType, Class<T> configClass, String classpath) {
        System.out.println("[DEBUG] start to load " + configType + " configs");
        HashMap<String, T> configObjMap = new HashMap<>();
        ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory());
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .disableHtmlEscaping()
                .excludeFieldsWithModifiers(Modifier.TRANSIENT)
                .create();
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

        try {
            Resource[] resources = new PathMatchingResourcePatternResolver(classLoader).getResources(classpath);
            if (resources.length == 0) {
                System.out.println("[ERROR] there is NO " + configType + " configs");
                errorMessageSb.append("there is NO ").append(configType).append(" configs").append("\n");
            }

            for (Resource resource : resources) {
                String fileName = resource.getFilename();
                System.out.println("[DEBUG] try to load " + fileName);
                T configObj = objectMapper.readValue(resource.getInputStream(), configClass);

                System.out.println("[DEBUG] the content of " + fileName + ": ");
                System.out.println(gson.toJson(configObj));

                String systemErrorMessage = configObj.verify();
                if (!"".equals(systemErrorMessage)) {
                    errorMessageSb.append(fileName).append(" error:").append("\n");
                    errorMessageSb.append(systemErrorMessage).append("\n");
                }

                String nameWithoutExtension = getFileNameWithoutExtension(fileName);
                configObjMap.put(nameWithoutExtension, configObj);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return Collections.unmodifiableMap(configObjMap);
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
            System.out.println();
            jdaService.sendChatOpsChannelPropertiesMessage(passedMessage);
        }
    }
}
