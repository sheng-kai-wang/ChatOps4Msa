package ntou.soselab.msdobot_llm.Entity.Capability.Tool.LowCode;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

public class LowCode {

    @JsonProperty("property")
    private Map<String, String> propertyMap;
    @JsonProperty("constructor")
    private List<DeclaredMethod> constructorList;
    @JsonProperty("method")
    private List<DeclaredMethod> methodList;
    @JsonProperty("on_message")
    private List<DeclaredMethod> onMessageList;
}
