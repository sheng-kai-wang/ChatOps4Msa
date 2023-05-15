package ntou.soselab.msdobot_llm.Entity.Capability;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Info {

    @JsonProperty("version")
    private String version;
    @JsonProperty("title")
    private String title;
    @JsonProperty("description")
    private String description;

    public String verify() {
        StringBuilder sb = new StringBuilder();
        if (version == null) sb.append("  info.version is null").append("\n");
        if (title == null) sb.append("  info.title is null").append("\n");
        if (description == null) sb.append("  info.description is null").append("\n");
        return sb.toString();
    }
}
