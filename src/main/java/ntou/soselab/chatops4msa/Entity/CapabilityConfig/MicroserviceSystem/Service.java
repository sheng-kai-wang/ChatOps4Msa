package ntou.soselab.chatops4msa.Entity.CapabilityConfig.MicroserviceSystem;

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
        if (name == null) sb.append("    there is NO name").append("\n");
        if (url == null) sb.append("    there is NO url").append("\n");
        if (repository == null) sb.append("    there is NO repository").append("\n");
        if (description == null) sb.append("    there is NO description").append("\n");
        return sb.toString();
    }
}
