package ntou.soselab.chatops4msa.Entity.Capability;

import com.fasterxml.jackson.annotation.JsonProperty;
import ntou.soselab.chatops4msa.Entity.Capability.Tool.LowCode.LowCode;

public class RabbitMQ {

    private Info info;
    @JsonProperty("low_code")
    private LowCode lowCode;
}
