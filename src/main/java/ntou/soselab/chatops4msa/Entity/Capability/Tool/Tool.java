package ntou.soselab.chatops4msa.Entity.Capability.Tool;

import com.fasterxml.jackson.annotation.JsonProperty;
import ntou.soselab.chatops4msa.Entity.Capability.Info;
import ntou.soselab.chatops4msa.Entity.Capability.Tool.LowCode.LowCode;

public class Tool {

    private Info info;
    @JsonProperty("low_code")
    private LowCode lowCode;
}
