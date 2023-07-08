package ntou.soselab.chatops4msa.Entity.ToolkitFunction;

import ntou.soselab.chatops4msa.Entity.CapabilityConfig.DevOpsTool.LowCode.InvokedFunction;
import ntou.soselab.chatops4msa.Exception.ToolkitFunctionException;
import ntou.soselab.chatops4msa.Service.CapabilityOrchestratorService.CapabilityOrchestrator;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * Rename certain parameters to avoid conflicts with reserved keywords.
 * Implement toolkit-flow-return inside CapabilityOrchestrator.
 */
@Component
public class FlowToolkit extends ToolkitFunction {
    private final CapabilityOrchestrator orchestrator;

    public FlowToolkit(CapabilityOrchestrator orchestrator) {
        this.orchestrator = orchestrator;
    }

    public void toolkitFlowIf(String condition,
                              List<InvokedFunction> trueList,
                              List<InvokedFunction> falseList,
                              Map<String, String> localVariableMap) throws ToolkitFunctionException {

        if ("true".equals(condition)) orchestrator.invokeSpecialParameter(trueList, localVariableMap);
        else orchestrator.invokeSpecialParameter(falseList, localVariableMap);
    }
}
