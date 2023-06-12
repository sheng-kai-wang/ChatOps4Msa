package ntou.soselab.chatops4msa.Entity.Capability;

import com.fasterxml.jackson.annotation.JsonProperty;
import ntou.soselab.chatops4msa.Entity.Capability.DevOpsTool.LowCode.LowCode;

public class MessageDelivery implements Configs {

    private Info info;
    @JsonProperty("low_code")
    private LowCode lowCode;

    @Override
    public String verify() {
        return null;
    }
}
