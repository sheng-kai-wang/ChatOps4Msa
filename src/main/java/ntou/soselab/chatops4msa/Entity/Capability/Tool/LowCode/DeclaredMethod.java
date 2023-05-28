package ntou.soselab.chatops4msa.Entity.Capability.Tool.LowCode;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

public class DeclaredMethod {

    private Map<String, String> methodVariableMap;
    private String name;
    @JsonProperty("parameter")
    private Map<String, String> parameterDescriptionMap;
    private String description;
    private String access;
    @JsonProperty("protected")
    private List<String> protectedList;
    @JsonProperty("body")
    private List<InvokedMethod> invokedMethodList;
}
