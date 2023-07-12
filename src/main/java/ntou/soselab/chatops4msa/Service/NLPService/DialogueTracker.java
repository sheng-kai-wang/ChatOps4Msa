package ntou.soselab.chatops4msa.Service.NLPService;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.gson.JsonParseException;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import ntou.soselab.chatops4msa.Entity.NLP.IntentAndEntity;
import ntou.soselab.chatops4msa.Entity.NLP.User;
import ntou.soselab.chatops4msa.Exception.UnexpectedServiceEntityException;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Service
public class DialogueTracker {
    private final ConcurrentMap<String, User> activeUserMap;
    private final List<String> waitingButtonTesterList;
    private final Long EXPIRED_INTERVAL;
    private final LLMService llmService;
    private final CapabilityGenerator capabilityGenerator;

    @Autowired
    public DialogueTracker(Environment env, LLMService llmService, CapabilityGenerator capabilityGenerator) {
        this.activeUserMap = new ConcurrentHashMap<>();
        this.waitingButtonTesterList = new ArrayList<>();
        this.EXPIRED_INTERVAL = Long.valueOf(Objects.requireNonNull(env.getProperty("intent.expired_time")));
        this.llmService = llmService;
        this.capabilityGenerator = capabilityGenerator;
    }

    public MessageCreateData sendMessage(String userId, String name, String message) {

        // get the user data
        if (!activeUserMap.containsKey(userId)) activeUserMap.put(userId, new User(userId, name));
        User currentUser = activeUserMap.get(userId);

        MessageCreateBuilder mb = new MessageCreateBuilder();

        if (llmService.isPromptInjection(message)) {
            System.out.println("[WARNING] Prompt Injection");
            return mb.setContent("Sorry, the message you entered is beyond the scope of the capability.\n").build();
        }

        if (llmService.isEndOfTopic(message)) {
            System.out.println("[DEBUG] End Of Topic");
            String cancelledIntentName = currentUser.cancelTopIntent();
            if (cancelledIntentName == null) {
                return mb.setContent("There are no capabilities being prepared for perform yet.").build();
            }
            return mb.setContent("Okay, we have cancelled the " + cancelledIntentName + " capability for you.").build();
        }

        String outOfCapabilityErrorMessage = "```prolog" + "\n[WARNING] Sorry, this question is beyond my capabilities.```";
        String unexpectedServiceEntityErrorMessage = "```prolog" + "\n[WARNING] Sorry, the service can only be Productpage, Reviews, Ratings, Details or all_service.```";
        try {
            String topIntentAndEntities = currentUser.getTopIntentAndEntitiesString();
            JSONObject matchedIntentAndEntity = llmService.classifyIntentAndExtractEntity(topIntentAndEntities, message);
            String response = currentUser.updateIntentAndEntity(matchedIntentAndEntity, capabilityGenerator, EXPIRED_INTERVAL);
            mb.addContent(response);
        } catch (JsonParseException e) {
            System.out.println("[ERROR] After LLM -> json string to JSONObject exception");
            e.printStackTrace();
            return mb.setContent(outOfCapabilityErrorMessage).build();
        } catch (JsonProcessingException e) {
            System.out.println("[ERROR] Before LLM -> yaml to json string exception");
            e.printStackTrace();
            return mb.setContent(outOfCapabilityErrorMessage).build();
        } catch (JSONException e) {
            System.out.println("[ERROR] After LLM -> get JSONObject from JSONObject exception");
            e.printStackTrace();
            return mb.setContent(outOfCapabilityErrorMessage).build();
        } catch (UnexpectedServiceEntityException e) {
            System.out.println("[ERROR] there is a unexpected service entity");
            System.out.println("[Unexpected Service] " + e.getMessage());
            e.printStackTrace();
            return mb.setContent(unexpectedServiceEntityErrorMessage).build();
        }

        // generate perform check (button)
        List<IntentAndEntity> performableIntentList = currentUser.getPerformableIntentList();
        if (!performableIntentList.isEmpty()) {
            generatePerformCheck(mb, currentUser, performableIntentList);

            // generate question
        } else {
            removeExpiredIntent(currentUser);
            generateQuestion(mb, currentUser);
        }

        return mb.build();
    }

    private void generatePerformCheck(MessageCreateBuilder mb,
                                      User user,
                                      List<IntentAndEntity> performableIntentAndEntityList) {

        mb.addContent("Here is the capability you are about to perform.\n");
        mb.addContent("Please use the BUTTON to indicate whether you want to proceed.\n");
        System.out.println("[DEBUG] generate perform check to " + user.getName());
        for (IntentAndEntity intentAndEntity : performableIntentAndEntityList) {
            String intentName = intentAndEntity.getIntentName();
            EmbedBuilder eb = new EmbedBuilder();
            eb.setTitle(intentName);
            eb.setColor(Color.ORANGE);
            System.out.println("[Intent] " + intentName);
            for (Map.Entry<String, String> entity : intentAndEntity.getEntities().entrySet()) {
                String entityName = entity.getKey();
                String entityValue = entity.getValue();
                eb.addField(entityName, entityValue, false);
                System.out.println("[Entity] " + entityName + " = " + entityValue);
            }
            mb.addEmbeds(eb.build());
        }
        mb.setActionRow(Button.primary("Perform", "Perform"), Button.primary("Cancel", "Cancel"));
        waitingButtonTesterList.add(user.getId());
        System.out.println("[DEBUG] Waiting for " + user.getName() + " to click the button.");
    }

    public boolean isWaitingTester(String testerId) {
        return waitingButtonTesterList.contains(testerId);
    }

    public void removeWaitingTesterList(String testerId) {
        waitingButtonTesterList.remove(testerId);
    }

    public ArrayList<String> performAllPerformableIntent(String testerId) {
        return activeUserMap.get(testerId).performAllPerformableIntent();
    }

    public ArrayList<String> cancelAllPerformableIntent(String testerId) {
        return activeUserMap.get(testerId).cancelAllPerformableIntent();
    }

    private void removeExpiredIntent(User user) {
        List<String> removedIntentList = activeUserMap.get(user.getId()).removeExpiredIntent();
        System.out.println("[DEBUG] remove expired intent of " + user.getName());
        System.out.println("[DEBUG] removed intent list: " + removedIntentList);
    }

    private void generateQuestion(MessageCreateBuilder mb, User user) {
        IntentAndEntity waitingIntentAndEntity = activeUserMap.get(user.getId()).getTopIntentAndEntity();
        // No intent or entity was found
        if (waitingIntentAndEntity == null) return;
        System.out.println("[DEBUG] generate question to " + user.getName());
        try {
            String intentName = waitingIntentAndEntity.getIntentName();
            Map<String, String> entityMap = waitingIntentAndEntity.getEntities();
            String question = llmService.queryMissingParameter(intentName, entityMap);
            mb.addContent(question);
            System.out.println("[Question] " + question);
        } catch (JSONException e) {
            System.out.println("[ERROR] Before LLM -> yaml to JSONObject exception");
            e.printStackTrace();
            mb.setContent("```prolog" + "\n[WARNING] Sorry, the system has encountered a formatting exception.```");
        }
    }

    public String generateQuestionString(String userId) {
        User user = activeUserMap.get(userId);
        IntentAndEntity waitingIntentAndEntity = user.getTopIntentAndEntity();
        if (waitingIntentAndEntity == null) return "";
        System.out.println("[DEBUG] generate question to " + user.getName());
        try {
            String intentName = waitingIntentAndEntity.getIntentName();
            Map<String, String> entityMap = waitingIntentAndEntity.getEntities();
            String question = llmService.queryMissingParameter(intentName, entityMap);
            System.out.println("[Question] " + question);
            return question;
        } catch (JSONException e) {
            System.out.println("[ERROR] Before LLM -> yaml to JSONObject exception");
            e.printStackTrace();
            return "```prolog" + "\n[WARNING] Sorry, the system has encountered a formatting exception.```";
        }
    }
}
