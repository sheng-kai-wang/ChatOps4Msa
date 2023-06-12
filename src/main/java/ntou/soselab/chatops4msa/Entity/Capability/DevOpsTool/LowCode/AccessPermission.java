package ntou.soselab.chatops4msa.Entity.Capability.DevOpsTool.LowCode;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class AccessPermission {
    private String access;
    @JsonProperty("protected")
    private List<String> protectedAccess;

    public String verify() {
        String errorMessage = "      the access must be public, private or protected list";
        if (access == null && protectedAccess == null) return errorMessage;
        if (access != null && !access.equals("public") && !access.equals("private")) return errorMessage;
        return "";
    }
}
