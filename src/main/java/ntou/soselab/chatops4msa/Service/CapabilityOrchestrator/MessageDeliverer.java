package ntou.soselab.chatops4msa.Service.CapabilityOrchestrator;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import ntou.soselab.chatops4msa.Exception.ToolkitFunctionException;
import ntou.soselab.chatops4msa.Service.DiscordService.JDAService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * For ease of management, the Lab's network currently utilizes a reverse proxy (NGINX) to expose the services.
 * As a result, DevOps tools can only transmit messages into the Message Queue via HTTP API. (config in GitHub Actions)
 */
@Service
public class MessageDeliverer {
    private final CapabilityOrchestrator orchestrator;
    private final JDAService jdaService;
    private final String createQueueApi;
    private final String rabbitmqUsername;
    private final String rabbitmqPassword;

    @Autowired
    public MessageDeliverer(Environment env, CapabilityOrchestrator orchestrator, JDAService jdaService) {
        this.orchestrator = orchestrator;
        this.jdaService = jdaService;
        this.createQueueApi = env.getProperty("rabbitmq.create_queue.api");
        this.rabbitmqUsername = env.getProperty("spring.rabbitmq.username");
        this.rabbitmqPassword = env.getProperty("spring.rabbitmq.password");
    }

    @PostConstruct
    private void createMessageQueue() {

        // Encode credentials in Base64
        String credentials = java.util.Base64
                .getEncoder()
                .encodeToString((rabbitmqUsername + ":" + rabbitmqPassword).getBytes(StandardCharsets.UTF_8));

        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(createQueueApi).openConnection();
            // set headers
            connection.setRequestMethod("PUT");
            connection.setRequestProperty("Authorization", "Basic " + credentials);
            connection.setRequestProperty("Content-Type", "application/json");
            // set request body (queue configuration)
            String queueConfig = "{\"durable\": true, \"auto_delete\": false, \"arguments\": {}}";
            connection.setDoOutput(true);
            connection.getOutputStream().write(queueConfig.getBytes(StandardCharsets.UTF_8));
            // check the result
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_NO_CONTENT || responseCode == HttpURLConnection.HTTP_CREATED) {
                System.out.println("[DEBUG] Successfully Create a Message Queue");
            } else {
                System.out.println("[ERROR] error code (" + responseCode + ")");
            }
            System.out.println();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @RabbitListener(queues = "event-queue")
    public void onMessage(String message) {

        System.out.println(">>> receive a message event");

        jdaService.sendChatOpsChannelMessage("receive a message event...");

        // print time
        System.out.println("[Time] " + new Date());

        // get the triggered function
        Map<String, String> messageMap = new HashMap<>();
        String triggeredFunction = null;
        try {
            messageMap = new ObjectMapper().readValue(message, new TypeReference<Map<String, String>>() {
            });
            triggeredFunction = messageMap.get("trigger_function");
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("[Trigger Function] " + triggeredFunction);

        // get the argumentMap
        Map<String, String> argumentMap = new HashMap<>();
        argumentMap.put("event", message);
        System.out.println("[Event Arguments] " + messageMap);

        // perform the capability
        try {
            orchestrator.performTheCapability(triggeredFunction, argumentMap);
        } catch (ToolkitFunctionException e) {
            e.printStackTrace();
            String errorMessage = "[ERROR] " + e.getLocalizedMessage();
            System.out.println(errorMessage);
            jdaService.sendChatOpsChannelErrorMessage(errorMessage);
        }

        System.out.println("<<< end of current message event");
        System.out.println();
    }
}