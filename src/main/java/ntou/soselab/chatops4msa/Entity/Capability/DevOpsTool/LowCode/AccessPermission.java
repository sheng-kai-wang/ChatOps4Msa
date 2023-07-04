package ntou.soselab.chatops4msa.Entity.Capability.DevOpsTool.LowCode;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class AccessPermission {
    private final String access;
    @JsonProperty("protected")
    private List<String> protectedAccess;

    public AccessPermission(String access) {
        this.access = access;
    }

    public AccessPermission() {
        this.access = "protected";
    }

    public String getAccess() {
        return this.access;
    }

    public List<String> getProtectedAccess() {
        return this.protectedAccess;
    }

    public String verify() {
        String errorMessage = "      the access must be public, private or protected list\n";
        if (!access.equals("public") && !access.equals("protected") && !access.equals("private")) return errorMessage;
        if ("protected".equals(access) && protectedAccess == null) return errorMessage;
        return "";
    }
}
