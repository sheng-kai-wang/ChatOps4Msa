package ntou.soselab.msdobot_llm.Entity.Capability.Tool;

import com.fasterxml.jackson.annotation.JsonProperty;
import ntou.soselab.msdobot_llm.Entity.Capability.Info;
import ntou.soselab.msdobot_llm.Entity.Capability.Tool.LowCode.LowCode;

public class Tool {

    private Info info;
    @JsonProperty("low_code")
    private LowCode lowCode;
}
