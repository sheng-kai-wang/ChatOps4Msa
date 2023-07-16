package ntou.soselab.chatops4msa.Entity.NLP;

import ntou.soselab.chatops4msa.Exception.UnexpectedServiceEntityException;
import ntou.soselab.chatops4msa.Service.NLPService.CapabilityGenerator;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.*;

public class User {

    private final String userId;
    private final String name;
    private final Stack<String> intentNameStack;
    private final Map<String, IntentAndEntity> intentAndEntityMap;

    public User(String userId, String name) {
        this.userId = userId;
        this.name = name;
        this.intentNameStack = new Stack<>();
        this.intentAndEntityMap = new HashMap<>();
    }

    public String getId() {
        return this.userId;
    }

    public String getName() {
        return this.name;
    }

    public String cancelTopIntent() {
        String topIntentName = getTopIntentAndEntity().getIntentName();
        intentNameStack.remove(topIntentName);
        intentAndEntityMap.remove(topIntentName);
        return topIntentName;
    }

    public IntentAndEntity getTopIntentAndEntity() {
        if (!isWaitingForPerform()) return null;
        String topIntentName = intentNameStack.peek();
        return intentAndEntityMap.get(topIntentName);
    }

    private boolean isWaitingForPerform() {
        return !intentNameStack.isEmpty();
    }

    public String getTopIntentAndEntitiesString() {
        IntentAndEntity topIntentAndEntity = getTopIntentAndEntity();
        if (topIntentAndEntity == null || topIntentAndEntity.canPerform()) return "";
        StringBuilder sb = new StringBuilder();
        sb.append("{\"").append(topIntentAndEntity.getIntentName()).append("\":{");
        for (Map.Entry<String, String> entityEntry : topIntentAndEntity.getEntities().entrySet()) {
            sb.append("\"").append(entityEntry.getKey()).append("\":\"").append(entityEntry.getValue()).append("\",");
        }
        sb.append("}}");
        return sb.toString();
    }

    public String updateIntentAndEntity(JSONObject matchedIntentAndEntity,
                                        CapabilityGenerator capabilityGenerator,
                                        Long expiredInterval) throws JSONException, UnexpectedServiceEntityException {

        System.out.println();
        System.out.println("[DEBUG] updateIntentAndEntity()");
        Iterator<String> intentAndEntityIt = matchedIntentAndEntity.keys();
        while (intentAndEntityIt.hasNext()) {
            String intentName = intentAndEntityIt.next();
            System.out.println("[Intent Name] " + intentName);
            JSONObject matchedEntitiesJSON = matchedIntentAndEntity.getJSONObject(intentName);
            System.out.println("[Matched Entities JSON] " + matchedEntitiesJSON);

            // out of scope
            if ("out_of_scope".equals(intentName) || "".equals(intentName)) {
                System.out.println("[DEBUG] OUT OF SCOPE");
                return "Sorry, the message you entered is beyond the scope of the capability.\n";
            }

            // only entity
            if ("no_intent".equals(intentName)) {
                if (getTopIntentAndEntity() == null) continue;
                Map<String, String> originalEntityMap = getTopIntentAndEntity().getEntities();
                Iterator<String> entityIt = matchedEntitiesJSON.keys();
                while (entityIt.hasNext()) {
                    String matchedEntityName = entityIt.next();
                    String matchedEntityValue = matchedEntitiesJSON.getString(matchedEntityName);
                    if (isIgnoredEntity(matchedEntityValue)) continue;
                    if ("service_name".equals(matchedEntityName)) {
                        if (!isExpectedServiceEntity(matchedEntityValue, capabilityGenerator)) {
                            throw new UnexpectedServiceEntityException(matchedEntityValue);
                        }
                    }
                    originalEntityMap.replace(matchedEntityName, matchedEntityValue);
                }
                getTopIntentAndEntity().updateExpiredTimestamp(expiredInterval);
                System.out.println("[DEBUG] NO intent, ONLY entity");
                System.out.println("[DEBUG] update original intent: " + intentNameStack.peek());

                // update the performable status of the TOP intent and entity
                IntentAndEntity currentIntentAndEntity = getTopIntentAndEntity();
                updatePerformableStatusOfIntent(currentIntentAndEntity);
                continue;
            }

            // push new intent
            if (!intentNameStack.contains(intentName)) {
                Map<String, String> newEntityMap = new HashMap<>();
                List<String> entityNameList = capabilityGenerator.getParameterList(intentName);
                for (String entityName : entityNameList) {
                    if (matchedEntitiesJSON.has(entityName)) {
                        String entityValue = null;
                        Object entityValueObj = matchedEntitiesJSON.opt(entityName);
                        // avoid the value is JSONObject
                        if (entityValueObj instanceof String) {
                            entityValue = matchedEntitiesJSON.getString(entityName);
                        }
                        if (!isIgnoredEntity(entityValue)) {
                            if ("service_name".equals(entityName)) {
                                if (!isExpectedServiceEntity(entityValue, capabilityGenerator)) {
                                    throw new UnexpectedServiceEntityException(entityValue);
                                }
                            }
                            newEntityMap.put(entityName, entityValue);
                            continue;
                        }
                    }
                    newEntityMap.put(entityName, null);
                }
                Long expiredTimestamp = System.currentTimeMillis() + expiredInterval;
                IntentAndEntity newIntentAndEntity = new IntentAndEntity(intentName, expiredTimestamp, newEntityMap);
                intentNameStack.push(intentName);
                intentAndEntityMap.put(intentName, newIntentAndEntity);
                System.out.println("[DEBUG] push new intent: " + intentName);

            }

            // update original intent and entity
            else {
                Map<String, String> originalEntityMap = intentAndEntityMap.get(intentName).getEntities();
                Iterator<String> entityIt = matchedEntitiesJSON.keys();
                if (!entityIt.hasNext()) {
                    System.out.println("[DEBUG] match original intent but NO EXTRACTED ENTITY");
                    continue;
                }
                while (entityIt.hasNext()) {
                    String matchedEntityName = entityIt.next();
                    String matchedEntityValue = null;
                    Object matchedEntityValueObj = matchedEntitiesJSON.opt(matchedEntityName);
                    // avoid the value is JSONObject
                    if (matchedEntityValueObj instanceof String) {
                        matchedEntityValue = matchedEntitiesJSON.getString(matchedEntityName);
                    }
                    if (isIgnoredEntity(matchedEntityValue)) continue;
                    if ("service_name".equals(matchedEntityName)) {
                        if (!isExpectedServiceEntity(matchedEntityValue, capabilityGenerator)) {
                            throw new UnexpectedServiceEntityException(matchedEntityValue);
                        }
                    }
                    originalEntityMap.replace(matchedEntityName, matchedEntityValue);
                }
                intentAndEntityMap.get(intentName).updateExpiredTimestamp(expiredInterval);
                System.out.println("[DEBUG] update original intent: " + intentName);
            }

            // update the performable status of the intent and entity
            IntentAndEntity currentIntentAndEntity = intentAndEntityMap.get(intentName);
            updatePerformableStatusOfIntent(currentIntentAndEntity);
        }

        System.out.println();
        System.out.println("[DEBUG] The intent map for " + this.name + " currently: ");
        System.out.println(getIntentMapString());
        return "ok\n";
    }

    private boolean isIgnoredEntity(String entityValue) {
        return entityValue == null ||
                entityValue.isEmpty() ||
                "null".equals(entityValue) ||
                "unspecified".equals(entityValue) ||
                "未提供".equals(entityValue) ||
                entityValue.startsWith("<");
    }

    private boolean isExpectedServiceEntity(String serviceEntityValue, CapabilityGenerator capabilityGenerator) {
        if ("all_service".equals(serviceEntityValue)) return true;
        List<String> serviceNameList = capabilityGenerator.getServiceNameList();
        return serviceNameList.contains(serviceEntityValue);
    }

    private void updatePerformableStatusOfIntent(IntentAndEntity intentAndEntity) {
        Map<String, String> currentIntentEntityMap = intentAndEntity.getEntities();
        System.out.println("[DEBUG] Current Intent's Entity Map:");
        System.out.println(currentIntentEntityMap);
        if (!currentIntentEntityMap.containsValue(null)) intentAndEntity.preparePerform();
        if (currentIntentEntityMap.isEmpty()) intentAndEntity.preparePerform();
    }

    private String getIntentMapString() {
        StringBuilder intentSb = new StringBuilder();
        intentSb.append("{ ");
        for (Map.Entry<String, IntentAndEntity> intentEntry : intentAndEntityMap.entrySet()) {
            Map<String, String> entities = intentEntry.getValue().getEntities();
            StringBuilder entitySb = new StringBuilder();
            entitySb.append("[ ");
            for (Map.Entry<String, String> entityEntry : entities.entrySet()) {
                entitySb.append(entityEntry.getKey()).append(": ").append(entityEntry.getValue()).append(", ");
            }
            entitySb.delete(entitySb.length() - 2, entitySb.length());
            entitySb.append(" ]");

            intentSb.append(intentEntry.getKey()).append(": ").append(entitySb).append(", ");
        }
        intentSb.delete(intentSb.length() - 2, intentSb.length());
        intentSb.append(" }");
        return intentSb.toString();
    }

    public List<IntentAndEntity> getPerformableIntentList() {
        ArrayList<IntentAndEntity> performableIntentList = new ArrayList<>();
        if (!isWaitingForPerform()) {
            System.out.println("[WARNING] NO Performable Intent");
            return performableIntentList;
        }
        System.out.println();
        System.out.println("[DEBUG] Performable Intent:");
        for (IntentAndEntity intentAndEntity : intentAndEntityMap.values()) {
            if (intentAndEntity.canPerform()) {
                System.out.println("[Intent Name] " + intentAndEntity.getIntentName());
                performableIntentList.add(intentAndEntity);
            }
        }
        return performableIntentList;
    }

    public List<String> removeExpiredIntent() {
        ArrayList<String> removedIntentList = new ArrayList<>();
        if (!isWaitingForPerform()) return removedIntentList;
        // to avoid java.util.ConcurrentModificationException
        List<String> intentToRemoveList = new ArrayList<>();
        for (IntentAndEntity intent : intentAndEntityMap.values()) {
            if (System.currentTimeMillis() > intent.getExpiredTimestamp()) {
                String intentName = intent.getIntentName();
                intentNameStack.remove(intentName);
                intentToRemoveList.add(intentName);
            }
        }
        for (String intentName : intentToRemoveList) {
            intentAndEntityMap.remove(intentName);
            removedIntentList.add(intentName);
        }
        return removedIntentList;
    }

    public List<IntentAndEntity> removeAllPerformableIntentAndEntity() {
        List<IntentAndEntity> performableIntentList = getPerformableIntentList();
        if (performableIntentList.isEmpty()) return null;
        // clear the temporary data
        for (IntentAndEntity intentAndEntity : performableIntentList) {
            String intentName = intentAndEntity.getIntentName();
            intentNameStack.remove(intentName);
            intentAndEntityMap.remove(intentName);
        }
        return performableIntentList;
    }
}
