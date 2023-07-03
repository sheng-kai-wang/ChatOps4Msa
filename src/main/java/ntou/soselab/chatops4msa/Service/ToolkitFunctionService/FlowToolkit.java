package ntou.soselab.chatops4msa.Service.ToolkitFunctionService;

import ntou.soselab.chatops4msa.Entity.Capability.DevOpsTool.LowCode.InvokedFunction;

import java.util.List;
import java.util.Map;

public class FlowToolkit extends ToolkitFunction {
    public void toolkitFlowIf(boolean condition, List<InvokedFunction> trueList, List<InvokedFunction> falseList) {
        if (condition) {
            for (InvokedFunction function : trueList) {
                // TODO: how to invoke...
                function.invoke();
            }

        } else {
            for (InvokedFunction function : falseList) {
                // TODO: how to invoke...
                function.invoke();
            }
        }
    }

    /**
     * return to the outside of this declared function, and stop it
     */
    public void toolkitFlowReturn(Map<String, String> propertyMap,
                                  String declaredFunctionName,
                                  String returnValue) {

        propertyMap.put(declaredFunctionName, returnValue);
        // TODO: how to stop the declared function
    }
}
