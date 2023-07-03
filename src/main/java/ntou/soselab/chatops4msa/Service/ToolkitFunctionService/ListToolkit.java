package ntou.soselab.chatops4msa.Service.ToolkitFunctionService;

import ntou.soselab.chatops4msa.Entity.Capability.DevOpsTool.LowCode.InvokedFunction;
import ntou.soselab.chatops4msa.Exception.ToolkitFunctionException;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
     * @param list        like ["service_1", "service_2", "service_3"]
     * @param elementName like "service_name"
     * @param todo        is a list of InvokedFunction
     */
    public void toolkitListForeach(String[] list, String elementName, List<InvokedFunction> todo) {
        for (String element : list) {
            for (InvokedFunction function : todo) {
                function.invoke();
                // TODO: how to pass the parameter
            }
        }
    }

    /**
     * execute the todo function asynchronously
     *
     * @param list        like ["service_1", "service_2", "service_3"]
     * @param elementName like "service_name"
     * @param todo        is a list of InvokedFunction
     */
    public void toolkitListAsync(String[] list, String elementName, List<InvokedFunction> todo) {
        ExecutorService executorService = Executors.newFixedThreadPool(4);
        for (String element : list) {
            for (InvokedFunction function : todo) {
                // TODO: how to pass the parameter
                executorService.submit(() -> function.invoke());
            }
        }
        executorService.shutdown();
    }
}
