package ntou.soselab.msdobot_llm.Entity.ChatOpsQueryLanguage;

import com.fasterxml.jackson.annotation.JsonProperty;
import ntou.soselab.msdobot_llm.Entity.ChatOpsQueryLanguage.Tool.Tool.LowCode.LowCode;

public class RabbitMQ {

    private Info info;
    @JsonProperty("low_code")
    private LowCode lowCode;
}
