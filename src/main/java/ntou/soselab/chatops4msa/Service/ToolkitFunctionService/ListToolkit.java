package ntou.soselab.chatops4msa.Service.ToolkitFunctionService;

import ntou.soselab.chatops4msa.Entity.Capability.DevOpsTool.LowCode.InvokedFunction;
import ntou.soselab.chatops4msa.Exception.ToolkitFunctionException;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * For ease of invocation by the CapabilityOrchestrator,
 * parameters are using snake case, similar to low-code.
 */
public class ListToolkit extends ToolkitFunction {

    /**
     * @param list  like ["https:", "", "github", "com", "sheng-kai-wang", "ChatOps4Msa-Sample-Bookinfo", "git"]
     * @param index like 5
     * @return like "ChatOps4Msa-Sample-Bookinfo"
     */
    public String toolkitListGet(String[] list, String index) throws ToolkitFunctionException {
        try {
            return list[Integer.parseInt(index)];
        } catch (Exception e) {
            e.printStackTrace();
            throw new ToolkitFunctionException("toolkit-list-get error");
        }
    }

    /**
     * execute the todo function synchronously
     *
     * @param list         like ["service_1", "service_2", "service_3"]
     * @param element_name like "service_name"
     * @param todoList     is a list of InvokedFunction
     */
    public String toolkitListForeach(String[] list, String element_name, List<InvokedFunction> todoList) {
        for (String element : list) {
            for (InvokedFunction function : todoList) {
                // TODO: how to pass the parameter
            }
        }
    }

    /**
     * execute the todo function asynchronously
     *
     * @param list         like ["service_1", "service_2", "service_3"]
     * @param element_name like "service_name"
     * @param todoList     is a list of InvokedFunction
     */
    public String toolkitListAsync(String[] list, String element_name, List<InvokedFunction> todoList) {
        ExecutorService executorService = Executors.newFixedThreadPool(4);
        for (String element : list) {
            for (InvokedFunction function : todoList) {
                // TODO: how to pass the parameter
                executorService.submit(() -> function.invoke());
            }
        }
        executorService.shutdown();
    }
}
