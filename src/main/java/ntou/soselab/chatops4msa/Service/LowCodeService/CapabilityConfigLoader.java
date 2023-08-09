package ntou.soselab.chatops4msa.Service.LowCodeService;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import jakarta.annotation.PostConstruct;
import ntou.soselab.chatops4msa.Entity.CapabilityConfig.Configs;
import ntou.soselab.chatops4msa.Entity.CapabilityConfig.DevOpsTool.DevOpsTool;
import ntou.soselab.chatops4msa.Entity.CapabilityConfig.DevOpsTool.LowCode.DeclaredFunction;
import ntou.soselab.chatops4msa.Entity.CapabilityConfig.DevOpsTool.LowCode.LowCode;
import ntou.soselab.chatops4msa.Entity.CapabilityConfig.MessageDelivery.MessageDelivery;
import ntou.soselab.chatops4msa.Entity.CapabilityConfig.MicroserviceSystem.MicroserviceSystem;
import ntou.soselab.chatops4msa.Entity.CapabilityConfig.Secret.Secret;
import ntou.soselab.chatops4msa.Exception.IllegalCapabilityConfigException;
import ntou.soselab.chatops4msa.Service.DiscordService.JDAService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.lang.reflect.Modifier;
import java.util.*;

@Service
public class CapabilityConfigLoader {
    private final Map<String, MicroserviceSystem> microserviceSystemMap;
    private final Map<String, Secret> secretMap;
    private final Map<String, DevOpsTool> devOpsToolMap;
    public final Map<String, MessageDelivery> messageDeliveryMap;

    private final JDAService jdaService;
    private final StringBuilder errorMessageSb;

    @Autowired
    public CapabilityConfigLoader(Environment env, JDAService jdaService) {
        this.jdaService = jdaService;
        this.errorMessageSb = new StringBuilder();

        String microserviceSystemClasspath = env.getProperty("capability.microservice-system.classpath");
        String secretClasspath = env.getProperty("capability.secret.classpath");
        String devOpsToolClasspath = env.getProperty("capability.devops-tool.classpath");
        String messageDeliveryClasspath = env.getProperty("capability.message-delivery.classpath");

        this.microserviceSystemMap = loadConfig("microservice-system", MicroserviceSystem.class, microserviceSystemClasspath);
        this.secretMap = loadConfig("secret", Secret.class, secretClasspath);
        this.devOpsToolMap = loadConfig("devops-tool", DevOpsTool.class, devOpsToolClasspath);
        this.messageDeliveryMap = loadConfig("message-delivery", MessageDelivery.class, messageDeliveryClasspath);
    }

    @PostConstruct
    public void variableRetrieveAndVerify() {
        System.out.println("[DEBUG] start to verify the variable retrieval and retrieve the property variable of low code");
        System.out.println();

        StringBuilder variableRetrieveSb = new StringBuilder();
        variableRetrieveAndVerify("devops-tool", devOpsToolMap, variableRetrieveSb);
        variableRetrieveAndVerify("message-delivery", messageDeliveryMap, variableRetrieveSb);

        String allVariableRetrieveErrorMessage = variableRetrieveSb.toString();
        if (!allVariableRetrieveErrorMessage.isEmpty()) {
            errorMessageSb.append("variable retrieve error:").append("\n");
            errorMessageSb.append(allVariableRetrieveErrorMessage).append("\n");
        }
    }

    /**
     * verify the capability list in microservice-system
     */
    @PostConstruct
    public void microserviceSystemCapabilityListVerify() {
        System.out.println();
        System.out.println("[DEBUG] start to verify the capability list in microservice-system");
        System.out.println();

        // duplicated function name verify (declared function in low-code)
        List<String> allDeclaredFunctionNameList = getAllDeclaredFunctionNameList();
        String errorMessage = duplicatedFunctionNameVerify("declared function in low-code", allDeclaredFunctionNameList);
        if (!errorMessage.isEmpty()) errorMessageSb.append(errorMessage).append("\n");

        for (Map.Entry<String, MicroserviceSystem> entry : microserviceSystemMap.entrySet()) {
            String microserviceSystemName = entry.getKey();

            // duplicated function name verify (capability list)
            List<String> capabilityList = entry.getValue().getCapabilityList();
            errorMessage = duplicatedFunctionNameVerify("capability list in " + microserviceSystemName, capabilityList);
            if (!errorMessage.isEmpty()) errorMessageSb.append(errorMessage).append("\n");

            // undefined function name
            ArrayList<String> undefinedFunctionNameList = new ArrayList<>();
            for (String functionName : capabilityList) {
                if (allDeclaredFunctionNameList.contains(functionName)) continue;
                undefinedFunctionNameList.add(functionName);
            }
            if (undefinedFunctionNameList.isEmpty()) continue;
            errorMessageSb.append("the ").append(microserviceSystemName).append(" contains undefined function name or private function:").append("\n");
            errorMessageSb.append("  ").append(undefinedFunctionNameList).append("\n");
        }
    }

    @PostConstruct
    public void sendVerifyMessage() {
        try {
            checkVerifyMessage();
        } catch (IllegalCapabilityConfigException e) {
            String failedMessage = "[ERROR] Capability Configs Verification Failed";
            System.out.println(failedMessage);
            System.out.println();
            jdaService.sendChatOpsChannelErrorMessage(failedMessage);
            jdaService.sendChatOpsChannelBlocksMessage(e.getMessage());
            e.printStackTrace();
        }
    }

    private <T extends Configs> Map<String, T> loadConfig(String configType, Class<T> configClass, String classpath) {
        System.out.println();
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

//                System.out.println("[DEBUG] the content of " + fileName + ": ");
//                System.out.println(gson.toJson(configObj));
//                System.out.println();

                String errorMessage = configObj.verify();
                if (!errorMessage.isEmpty()) {
                    errorMessageSb.append(fileName).append(" error:").append("\n");
                    errorMessageSb.append(errorMessage).append("\n");
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

    private <T extends DevOpsTool> void variableRetrieveAndVerify(String devOpsToolType,
                                                                  Map<String, T> devOpsToolMap,
                                                                  StringBuilder sb) {

        for (Map.Entry<String, T> entry : devOpsToolMap.entrySet()) {
            LowCode lowCode = entry.getValue().getLowCode();
            String variableRetrieveErrorMessage = lowCode.variableRetrieveAndVerify(secretMap.get("secret"));
            if (!variableRetrieveErrorMessage.isEmpty()) {
                sb.append("  ").append(devOpsToolType).append("[").append(entry.getKey()).append("] error:").append("\n");
                sb.append(variableRetrieveErrorMessage).append("\n");
            }
        }
    }

    private List<String> getAllDeclaredFunctionNameList() {
        List<String> allDeclaredFunctionNameList = new ArrayList<>();
        for (DevOpsTool devOpsTool : devOpsToolMap.values()) {
            allDeclaredFunctionNameList.addAll(devOpsTool.getLowCode().getAllNonPrivateDeclaredFunctionNameList());
        }
        for (MessageDelivery messageDelivery : messageDeliveryMap.values()) {
            allDeclaredFunctionNameList.addAll(messageDelivery.getLowCode().getAllNonPrivateDeclaredFunctionNameList());
        }
        return allDeclaredFunctionNameList;
    }

    private String duplicatedFunctionNameVerify(String verifyType, List<String> functionNameList) {
        StringBuilder sb = new StringBuilder();

        Set<String> uniqueFunctionNames = new HashSet<>();
        Set<String> duplicates = new HashSet<>();
        for (String functionName : functionNameList) {
            if (uniqueFunctionNames.add(functionName)) continue;
            duplicates.add(functionName);
        }

        if (!duplicates.isEmpty()) {
            sb.append(verifyType).append(" error:").append("\n");
            sb.append("  duplicate function name found:").append("\n");
            sb.append("    ").append(duplicates).append("\n");
        }

        return sb.toString();
    }

    private void checkVerifyMessage() throws IllegalCapabilityConfigException {
        String errorMessage = compactLineBreaks(errorMessageSb.toString());
        if (!errorMessage.isEmpty()) throw new IllegalCapabilityConfigException(errorMessage);
        else {
            String passedMessage = "[INFO] Capability Configs Verification Passed";
            System.out.println(passedMessage);
            System.out.println();
            jdaService.sendChatOpsChannelInfoMessage(passedMessage);
        }
    }

    private String compactLineBreaks(String errorMessage) {
        while (errorMessage.contains("\n\n\n")) {
            errorMessage = errorMessage.replaceAll("\n\n\n", "\n\n");
        }
        return errorMessage;
    }

    /**
     * in order to generate the ChatOps Query Language (service options)
     */
    public List<String> getAllServiceNameList() {
        List<String> allServiceNameList = new ArrayList<>();
        for (MicroserviceSystem microserviceSystem : microserviceSystemMap.values()) {
            allServiceNameList.addAll(microserviceSystem.getAllServiceNameList());
        }
        return allServiceNameList;
    }

    /**
     * in order to generate the ChatOps Query Language (command name)
     */
    public Map<String, DeclaredFunction> getAllNonPrivateDeclaredFunctionMap() {
        Map<String, DeclaredFunction> map = new HashMap<>();
        for (DevOpsTool tool : devOpsToolMap.values()) {
            map.putAll(tool.getLowCode().getAllNonPrivateDeclaredFunctionMap());
        }
        for (MessageDelivery messageDelivery : messageDeliveryMap.values()) {
            map.putAll(messageDelivery.getLowCode().getAllNonPrivateDeclaredFunctionMap());
        }
        return map;
    }

    /**
     * in order to generate the ChatOps Query Language (command description)
     */
    public DevOpsTool getDevOpsToolObj(String toolName) {
        return this.devOpsToolMap.get(toolName);
    }

    /**
     * in order to perform the capability
     */
    public Map<String, DeclaredFunction> getAllDeclaredFunctionMap() {
        Map<String, DeclaredFunction> map = new HashMap<>();
        for (DevOpsTool tool : devOpsToolMap.values()) {
            map.putAll(tool.getLowCode().getAllDeclaredFunctionMap());
        }
        for (MessageDelivery messageDelivery : messageDeliveryMap.values()) {
            map.putAll(messageDelivery.getLowCode().getAllDeclaredFunctionMap());
        }
        return map;
    }

    /**
     * for the toolkit-info-get
     */
    public MicroserviceSystem getMicroserviceSystemObj(String systemName) {
        return this.microserviceSystemMap.get(systemName);
    }
}
