package ntou.soselab.chatops4msa.Entity.Capability.MicroserviceSystem;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

public class Service {

    @JsonProperty("name")
    private String name;
    @JsonProperty("url")
    private String url;
    @JsonProperty("repository")
    private String repository;
    @JsonProperty("description")
    private String description;
    @JsonAnySetter
    private Map<String, String> customPropertyMap;

    public String getProperty(String info) {
        if ("name".equals(info)) return name;
        if ("url".equals(info)) return url;
        if ("repository".equals(info)) return repository;
        if ("description".equals(info)) return description;
        if (customPropertyMap == null) return null;
        return this.customPropertyMap.get(info);
    }

    public String verify() {
        StringBuilder sb = new StringBuilder();
        if (name == null) sb.append("      name is null").append("\n");
        if (url == null) sb.append("      url is null").append("\n");
        if (repository == null) sb.append("      repository is null").append("\n");
        if (description == null) sb.append("      description is null").append("\n");
        return sb.toString();
    }
}
