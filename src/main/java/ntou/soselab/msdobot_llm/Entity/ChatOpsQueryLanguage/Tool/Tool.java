package ntou.soselab.msdobot_llm.Entity.ChatOpsQueryLanguage.Tool;

import com.fasterxml.jackson.annotation.JsonProperty;
import ntou.soselab.msdobot_llm.Entity.ChatOpsQueryLanguage.Info;
import ntou.soselab.msdobot_llm.Entity.ChatOpsQueryLanguage.Tool.LowCode.LowCode;
import ntou.soselab.msdobot_llm.Entity.ChatOpsQueryLanguage.Tool.Tool.LowCode.LowCode;

public class Tool {

    private Info info;
    @JsonProperty("low_code")
    private LowCode lowCode;
}
