package ntou.soselab.msdobot_llm.Entity.Capability;

import com.fasterxml.jackson.annotation.JsonProperty;
import ntou.soselab.msdobot_llm.Entity.Capability.Tool.LowCode.LowCode;

public class RabbitMQ {

    private Info info;
    @JsonProperty("low_code")
    private LowCode lowCode;
}
