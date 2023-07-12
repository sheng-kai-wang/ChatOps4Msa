package ntou.soselab.chatops4msa.Entity.NLP;

import java.util.Map;

public class IntentAndEntity {
    private final String intentName;
    private final Map<String, String> entities;
    private Long expiredTimestamp;
    private boolean canPerform;

    public IntentAndEntity(String intentName, Long expiredTimestamp, Map<String, String> entities) {
        this.intentName = intentName;
        this.entities = entities;
        this.expiredTimestamp = expiredTimestamp;
        this.canPerform = false;
    }

    public String getIntentName() {
        return this.intentName;
    }

    public Map<String, String> getEntities() {
        return this.entities;
    }

    public Long getExpiredTimestamp() {
        return this.expiredTimestamp;
    }

    public void updateExpiredTimestamp(Long expiredInterval) {
        this.expiredTimestamp = System.currentTimeMillis() + expiredInterval;
    }

    public boolean canPerform() {
        return this.canPerform;
    }

    public void preparePerform() {
        this.canPerform = true;
    }
}
