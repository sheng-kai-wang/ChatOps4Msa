package ntou.soselab.chatops4msa.Entity.CapabilityConfig;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Info {
    @JsonProperty("version")
    private String version;
    @JsonProperty("title")
    private String title;
    @JsonProperty("description")
    private String description;

    public String getDescription() {
        return this.description;
    }

    public String verify() {
        StringBuilder sb = new StringBuilder();
        if (version == null) sb.append("    there is NO version").append("\n");
        if (title == null) sb.append("    there is NO title").append("\n");
        if (description == null) sb.append("    there is NO description").append("\n");
        return sb.toString();
    }
}
