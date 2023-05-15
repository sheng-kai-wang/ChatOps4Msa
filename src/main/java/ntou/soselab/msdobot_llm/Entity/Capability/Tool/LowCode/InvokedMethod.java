package ntou.soselab.msdobot_llm.Entity.Capability.Tool.LowCode;

import java.util.Map;

public class InvokedMethod {

    private String name;
    private Map<String, String> parameterMap;

    public void setName(String name) {
        this.name = name;
    }
}