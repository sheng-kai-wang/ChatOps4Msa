package ntou.soselab.chatops4msa.Service.ToolkitFunctionService;

import ntou.soselab.chatops4msa.Entity.Capability.DevOpsTool.LowCode.InvokedFunction;

import java.util.List;

/**
 * Rename certain parameters to avoid conflicts with reserved keywords.
 * Implement toolkit-flow-return inside CapabilityOrchestrator.
 */
public class FlowToolkit extends ToolkitFunction {
    public String toolkitFlowIf(boolean condition, List<InvokedFunction> trueList, List<InvokedFunction> falseList) {
        if (condition) {
            for (InvokedFunction function : trueList) {
                // TODO: how to invoke...
            }

        } else {
            for (InvokedFunction function : falseList) {
                // TODO: how to invoke...
            }
        }
    }

    /**
     * return to the outside of this declared function, and stop it
     */
//    public void toolkitFlowReturn(Map<String, String> propertyMap,
//                                  String declaredFunctionName,
//                                  String returnValue) {
//
//        propertyMap.put(declaredFunctionName, returnValue);
//        // TODO: how to stop the declared function
//        // throw a stop function exception
//    }
}
