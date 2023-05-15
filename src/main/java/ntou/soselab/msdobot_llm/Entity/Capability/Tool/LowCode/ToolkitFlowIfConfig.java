package ntou.soselab.msdobot_llm.Entity.Capability.Tool.LowCode;

import java.util.List;

public class ToolkitFlowIfConfig extends InvokedMethod {

    private String condition;
    private List<InvokedMethod> todoList;
    private String end;

    public ToolkitFlowIfConfig() {
        setName("toolkit-flow-if");
    }
}
