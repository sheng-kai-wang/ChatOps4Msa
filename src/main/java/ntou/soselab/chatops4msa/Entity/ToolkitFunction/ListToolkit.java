package ntou.soselab.chatops4msa.Entity.ToolkitFunction;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import ntou.soselab.chatops4msa.Entity.CapabilityConfig.DevOpsTool.LowCode.InvokedFunction;
import ntou.soselab.chatops4msa.Exception.ToolkitFunctionException;
import ntou.soselab.chatops4msa.Service.CapabilityOrchestrator.CapabilityOrchestrator;
import ntou.soselab.chatops4msa.Service.DiscordService.JDAService;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
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
    private final ObjectMapper objectMapper = new ObjectMapper();

    public ListToolkit(CapabilityOrchestrator orchestrator, JDAService jdaService) {
        this.orchestrator = orchestrator;
        this.jdaService = jdaService;
    }

    /**
     * from ["content"] to content
     */
    public String toolkitListToString(String list) throws ToolkitFunctionException {
        List<String> listObj;
        try {
            listObj = objectMapper.readValue(list, new TypeReference<List<String>>() {
            });
        } catch (JsonProcessingException e) {
            throw new ToolkitFunctionException(e.getOriginalMessage());
        }
        if (listObj.size() == 1) return list.replaceAll("\\[\"", "").replaceAll("\"]", "");
        return list;
    }

    /**
     * [] is an empty list
     */
    public String toolkitListIsEmpty(String list) {
        if ("[]".equals(list)) return "true";
        return "false";
    }

    /**
     * @param list  like ["https:", "", "github", "com", "sheng-kai-wang", "ChatOps4Msa-Sample-Bookinfo", "git"]
     * @param index like 5
     * @return like "ChatOps4Msa-Sample-Bookinfo"
     */
    public String toolkitListGet(String list, String index) throws ToolkitFunctionException {
        String[] array;
        try {
            array = objectMapper.readValue(list, String[].class);
            int i = Integer.parseInt(index);
            return array[i];
        } catch (JsonProcessingException e) {
            throw new ToolkitFunctionException(e.getOriginalMessage());
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
    public void toolkitListForeach(String list,
                                   String element_name,
                                   List<InvokedFunction> todoList,
                                   Map<String, String> localVariableMap) throws ToolkitFunctionException {
        List<Object> listObj;
        try {
            listObj = objectMapper.readValue(list, new TypeReference<List<Object>>() {
            });

            // temporary storage of local variable with the same name
            String localVariableTemp = localVariableMap.get(element_name);

            for (int i = 0; i < listObj.size(); i++) {
                // put the element from foreach list
                String json = objectMapper.writeValueAsString(listObj.get(i));
                // from "content" to content
                if (json.startsWith("\"")) json = json.replaceAll("\"", "");
                localVariableMap.put(element_name, json);
                // put the index into local variable
                localVariableMap.put("i", String.valueOf(i));
                // invoke all the todo_function
                String returnSignal = orchestrator.invokeSpecialParameter(todoList, localVariableMap);
                if (returnSignal != null) break;
            }

            // restore the local variable
            localVariableMap.put(element_name, localVariableTemp);

        } catch (JsonProcessingException e) {
            throw new ToolkitFunctionException(e.getOriginalMessage());
        }
    }

    /**
     * execute the todo_function asynchronously
     *
     * @param list             like ["service_1", "service_2", "service_3"]
     * @param element_name     like "service_name"
     * @param todoList         is a list of InvokedFunction
     * @param localVariableMap come from declaredFunction
     */
    public void toolkitListAsync(String list,
                                 String element_name,
                                 List<InvokedFunction> todoList,
                                 Map<String, String> localVariableMap) throws ToolkitFunctionException {

        // there are 4 microservices for Bookinfo (4 threads)
        ExecutorService executorService = Executors.newFixedThreadPool(4);

        List<String> listObj;
        try {
            listObj = objectMapper.readValue(list, new TypeReference<List<String>>() {
            });
        } catch (JsonProcessingException e) {
            throw new ToolkitFunctionException(e.getOriginalMessage());
        }

        // temporary storage of local variable with the same name
        String localVariableTemp = localVariableMap.get(element_name);


        for (int i = 0; i < listObj.size(); i++) {
            String element = listObj.get(i);
            int finalIndex = i;
            executorService.submit(() -> {
                // put the element from async list
                localVariableMap.put(element_name, element);
                // invoke all the todo_function
                try {
                    // put the index into local variable
                    localVariableMap.put("i", String.valueOf(finalIndex));
                    // invoke the todo_function
                    InvokedFunction function = todoList.get(finalIndex);
                    List<InvokedFunction> functionList = new ArrayList<>();
                    functionList.add(function);
                    orchestrator.invokeSpecialParameter(functionList, localVariableMap);
                } catch (ToolkitFunctionException e) {
                    jdaService.sendChatOpsChannelErrorMessage("[ERROR] " + e.getLocalizedMessage() + " (" + element + ")");
                }
            });
        }
        executorService.shutdown();

        // restore the local variable
        localVariableMap.put(element_name, localVariableTemp);
    }
}
