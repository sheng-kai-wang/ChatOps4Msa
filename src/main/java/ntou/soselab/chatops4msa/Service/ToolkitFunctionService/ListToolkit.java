package ntou.soselab.chatops4msa.Service.ToolkitFunctionService;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import ntou.soselab.chatops4msa.Entity.Capability.DevOpsTool.LowCode.InvokedFunction;
import ntou.soselab.chatops4msa.Exception.ToolkitFunctionException;
import ntou.soselab.chatops4msa.Service.CapabilityOrchestratorService.CapabilityOrchestrator;
import ntou.soselab.chatops4msa.Service.DiscordService.JDAService;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * For ease of invocation by the CapabilityOrchestrator,
 * the parameters are using snake case, similar to low-code.
 */
@Component
public class ListToolkit extends ToolkitFunction {
    private final CapabilityOrchestrator orchestrator;
    private final JDAService jdaService;

    public ListToolkit(CapabilityOrchestrator orchestrator, JDAService jdaService) {
        this.orchestrator = orchestrator;
        this.jdaService = jdaService;
    }

    /**
     * @param list  like ["https:", "", "github", "com", "sheng-kai-wang", "ChatOps4Msa-Sample-Bookinfo", "git"]
     * @param index like 5
     * @return like "ChatOps4Msa-Sample-Bookinfo"
     */
    public String toolkitListGet(String list, String index) throws ToolkitFunctionException {
        ObjectMapper objectMapper = new ObjectMapper();
        String[] array;
        try {
            array = objectMapper.readValue(list, String[].class);
            int i = Integer.parseInt(index);
            return array[i];
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * execute the todo_function synchronously
     *
     * @param list             like ["service_1", "service_2", "service_3"]
     * @param element_name     like "service_name"
     * @param todoList         is a list of InvokedFunction
     * @param localVariableMap come from declaredFunction
     */
    public void toolkitListForeach(String[] list,
                                   String element_name,
                                   List<InvokedFunction> todoList,
                                   Map<String, String> localVariableMap) throws ToolkitFunctionException {

        // temporary storage of local variable with the same name
        String localVariableTemp = localVariableMap.get(element_name);

        for (String element : list) {
            // put the element from foreach list
            localVariableMap.put(element_name, element);
            // invoke all the todo_function
            orchestrator.invokeSpecialParameter(todoList, localVariableMap);
        }

        // restore the local variable
        localVariableMap.put(element_name, localVariableTemp);
    }

    /**
     * execute the todo_function asynchronously
     *
     * @param list             like ["service_1", "service_2", "service_3"]
     * @param element_name     like "service_name"
     * @param todoList         is a list of InvokedFunction
     * @param localVariableMap come from declaredFunction
     */
    public void toolkitListAsync(String[] list,
                                 String element_name,
                                 List<InvokedFunction> todoList,
                                 Map<String, String> localVariableMap) {

        // temporary storage of local variable with the same name
        String localVariableTemp = localVariableMap.get(element_name);

        // there are 4 microservices for Bookinfo (4 threads)
        ExecutorService executorService = Executors.newFixedThreadPool(4);
        for (String element : list) {
            executorService.submit(() -> {
                // put the element from async list
                localVariableMap.put(element_name, element);
                // invoke all the todo_function
                try {
                    orchestrator.invokeSpecialParameter(todoList, localVariableMap);
                } catch (ToolkitFunctionException e) {
                    jdaService.sendChatOpsChannelErrorMessage("[ERROR] " + e.getMessage() + " (" + element + ")");
                }
            });
        }
        executorService.shutdown();

        // restore the local variable
        localVariableMap.put(element_name, localVariableTemp);
    }
}
