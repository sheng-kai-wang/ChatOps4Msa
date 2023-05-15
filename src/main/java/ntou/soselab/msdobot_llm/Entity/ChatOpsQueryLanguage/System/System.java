package ntou.soselab.msdobot_llm.Entity.ChatOpsQueryLanguage.System;

import com.fasterxml.jackson.annotation.JsonProperty;
import ntou.soselab.msdobot_llm.Entity.ChatOpsQueryLanguage.Info;

import java.util.List;

public class System {

    private Info info;
    @JsonProperty("service")
    private List<Service> serviceList;
    @JsonProperty("capability")
    private List<String> capabilityList;
}
