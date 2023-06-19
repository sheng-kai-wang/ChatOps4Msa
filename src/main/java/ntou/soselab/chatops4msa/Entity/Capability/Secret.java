package ntou.soselab.chatops4msa.Entity.Capability;

import com.fasterxml.jackson.annotation.JsonCreator;

import java.util.Map;

public class Secret implements Configs {
    private final Map<String, String> secretMap;

    @JsonCreator
    public Secret(Map<String, String> secretMap) {
        this.secretMap = secretMap;
    }

    public String getSecretProperty(String propertyName) {
        return this.secretMap.get(propertyName);
    }

    public Map<String, String> getSecretPropertyMap() {
        return this.secretMap;
    }

    public String verify() {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> entry : secretMap.entrySet()) {
            if (entry.getValue() == null) {
                sb.append("  ").append(entry.getKey()).append(" is null").append("\n");
            }
        }
        return sb.toString();
    }
}
