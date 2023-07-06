package ntou.soselab.chatops4msa.Service.CapabilityOrchestratorService;

import ntou.soselab.chatops4msa.Entity.Capability.DevOpsTool.LowCode.AccessPermission;
import ntou.soselab.chatops4msa.Entity.Capability.DevOpsTool.LowCode.DeclaredFunction;
import ntou.soselab.chatops4msa.Entity.Capability.DevOpsTool.LowCode.InvokedFunction;
import ntou.soselab.chatops4msa.Exception.CapabilityRoleException;
import ntou.soselab.chatops4msa.Exception.ToolkitFunctionException;
import ntou.soselab.chatops4msa.Service.LowCodeService.CapabilityConfigLoader;
import ntou.soselab.chatops4msa.Service.LowCodeService.LowCodeVariableExtractor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CapabilityOrchestrator {
    private final Map<String, DeclaredFunction> capabilityMap;
    private final ApplicationContext appContext;
    private final String TOOLKIT_CLASSPATH;

    @Autowired
    public CapabilityOrchestrator(CapabilityConfigLoader configLoader,
                                  ApplicationContext appContext,
                                  Environment env) {

        this.capabilityMap = configLoader.getAllNonPrivateDeclaredFunctionMap();
        this.appContext = appContext;
        this.TOOLKIT_CLASSPATH = env.getProperty("toolkit.classpath.prefix");
    }

    public void performTheCapability(String functionName,
                                     Map<String, String> argumentMap,
                                     List<String> roleNameList) throws CapabilityRoleException, ToolkitFunctionException {
        // get function data
        DeclaredFunction functionData = capabilityMap.get(functionName);

        // Check the role of the user, the "public" keyword is ignored.
        AccessPermission access = functionData.getAccess();
        String accessType = access.getAccess();
        if ("protected".equals(accessType)) {
            List<String> protectedAccess = access.getProtectedAccess();
            if (!hasPermission(roleNameList, protectedAccess)) {
                String warningMessage = "Sorry, Only The " + protectedAccess + " Can Perform This Capability." +
                        "\n" +
                        "You Are " + roleNameList + ".";
                throw new CapabilityRoleException(warningMessage);
            }
        }

        // invoke
        invokeCustomFunction(functionData, argumentMap);
    }

    private boolean hasPermission(List<String> roleNameList, List<String> protectedAccess) {
        for (String roleName : roleNameList) {
            if (protectedAccess.contains(roleName)) return true;
        }
        return false;
    }

    public void invokeSpecialParameter(List<InvokedFunction> functionList,
                                       Map<String, String> localVariableMap) throws ToolkitFunctionException {

        // local variable in todo_function, true_function or false_function (deep copy)
        Map<String, String> functionListLocalVariableMap = new HashMap<>(localVariableMap);

        for (InvokedFunction function : functionList) {
            String functionName = function.getName();
            DeclaredFunction functionData = capabilityMap.get(functionName);

            // custom-function
            if (!functionName.startsWith("toolkit")) {
                String returnValue = invokeCustomFunction(functionData, functionListLocalVariableMap);

                // assign the return value to the functionListLocalVariableMap
                String assignName = function.getAssign();
                if (assignName != null && !assignName.isEmpty()) localVariableMap.put(assignName, returnValue);
                else localVariableMap.put(functionName, returnValue);
            }

            // toolkit-function
            else invokeToolkitFunction(function, functionListLocalVariableMap);
        }
    }

    private String invokeCustomFunction(DeclaredFunction functionData,
                                        Map<String, String> argumentMap) throws ToolkitFunctionException {

        // put argumentMap into localVariableMap
        Map<String, String> localVariableMap = functionData.getLocalVariableMap();
        localVariableMap.putAll(argumentMap);

        // invoke all the functions in the body
        List<InvokedFunction> allInvokedFunctionList = functionData.getAllInvokedFunctionList();
        for (InvokedFunction invokedFunction : allInvokedFunctionList) {
            Map<String, String> subArgumentMap = invokedFunction.copyArgumentMap();

            // update the arguments
            for (Map.Entry<String, String> entry : subArgumentMap.entrySet()) {
                String argumentName = entry.getKey();
                String argumentValue = entry.getValue();
                argumentValue = assignVariableToDefaultArgument(argumentValue, localVariableMap);
                subArgumentMap.put(argumentName, argumentValue);
            }

            // update the local variable
            localVariableMap.putAll(subArgumentMap);

            // custom-function or toolkit-function
            String invokedFunctionName = invokedFunction.getName();
            if (!invokedFunctionName.startsWith("toolkit")) {

                // custom-function
                // invoke
                DeclaredFunction invokedFunctionData = capabilityMap.get(invokedFunctionName);
                String returnValue = invokeCustomFunction(invokedFunctionData, subArgumentMap);

                // assign the return value to the localVariableMap
                String assignName = invokedFunction.getAssign();
                if (assignName != null && !assignName.isEmpty()) localVariableMap.put(assignName, returnValue);
                else localVariableMap.put(invokedFunctionName, returnValue);

            } else {

                // toolkit-function
                // return and stop the body function
                if ("toolkit-flow-return".equals(invokedFunctionName)) {
                    String returnValue = subArgumentMap.get("return");
                    // free up memory
                    localVariableMap.clear();
                    return returnValue;
                }

                // invoke
                try {
                    invokeToolkitFunction(invokedFunction, localVariableMap);
                } catch (ToolkitFunctionException e) {
                    throw new ToolkitFunctionException(functionData.getName() + " > " + e.getMessage());
                }

                // decide whether to continue invoking the body function
                if ("toolkit-flow-if".equals(invokedFunctionName)) {
                    if ("true".equals(subArgumentMap.get("condition"))) {
                        if (subArgumentMap.containsKey("true")) break;
                    } else {
                        if (subArgumentMap.containsKey("false")) break;
                    }
                }
            }
        }

        // free up memory
        localVariableMap.clear();

        return null;
    }

    private String assignVariableToDefaultArgument(String defaultArgument, Map<String, String> localVariableMap) {
        String actualArgument = defaultArgument;

        // if the defaultArgument contains variables
        for (String variableName : LowCodeVariableExtractor.extractVariableList(defaultArgument)) {
            String localVariable = localVariableMap.get(variableName);
            if (localVariable == null) localVariable = "";
            actualArgument = LowCodeVariableExtractor.assignVariable(actualArgument, variableName, localVariable);
        }

        return actualArgument;
    }

    private void invokeToolkitFunction(InvokedFunction functionData,
                                       Map<String, String> localVariableMap) throws ToolkitFunctionException {

        String functionName = functionData.getName();
        String toolkitClassName = extractToolkitClassName(functionName);
        String toolkitFunctionName = extractToolkitFunctionName(functionName);

        try {
            // prepare the arguments
            Class<?> clazz = Class.forName(toolkitClassName);
            Method method = null;
            for (Method m : clazz.getDeclaredMethods()) {
                if (m.getName().equals(toolkitFunctionName)) method = m;
            }
            assert method != null;
            Object[] arguments = generateArgumentsArray(method.getParameters(), functionData, localVariableMap);

            // invoke the toolkit-function
            Object toolkitClass = appContext.getBean(clazz);
            String returnValue = (String) method.invoke(toolkitClass, arguments);

            // assign the return value to the localVariableMap
            String assignName = functionData.getAssign();
            if (assignName != null && !assignName.isEmpty()) localVariableMap.put(assignName, returnValue);
            else localVariableMap.put(functionName, returnValue);

        } catch (ClassNotFoundException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
            throw new ToolkitFunctionException(functionName + " > " + e.getClass().getName() + ": " + e.getMessage());
        }
    }

    /**
     * @param functionName like  toolkit-discord-info
     * @return like              ntou.soselab.chatops4msa.Service.ToolkitFunctionService.DiscordToolkit
     */
    private String extractToolkitClassName(String functionName) {
        String functionType = functionName.split("-")[1];
        String capitalizedFunctionType = functionType.substring(0, 1).toUpperCase() + functionType.substring(1);
        return TOOLKIT_CLASSPATH + capitalizedFunctionType + "Toolkit";
    }

    /**
     * kebab case to camel case
     *
     * @param functionName like  toolkit-time-one_week_ago
     * @return like              toolkitTimeOneWeekAgo
     */
    private String extractToolkitFunctionName(String functionName) {
        String[] words = functionName.split("[-_]");
        StringBuilder sb = new StringBuilder(words[0]);
        for (int i = 1; i < words.length; i++) {
            String capitalized = words[i].substring(0, 1).toUpperCase() + words[i].substring(1);
            sb.append(capitalized);
        }
        return sb.toString();
    }

    /**
     * @param parameters       required parameters
     * @param functionData     the default parameter
     * @param localVariableMap including secrets, tool properties, input parameters, and return values of other functions
     * @return formatted arguments array
     */
    private Object[] generateArgumentsArray(Parameter[] parameters,
                                            InvokedFunction functionData,
                                            Map<String, String> localVariableMap) throws ToolkitFunctionException {

        Object[] arguments = new Object[parameters.length];
        int index = 0;
        for (Parameter parameter : parameters) {
            String requiredParameterName = parameter.getName();

            if ("localVariableMap".equals(requiredParameterName)) {
                // in order to special parameter (function list)
                arguments[index] = localVariableMap;

            } else if (localVariableMap.containsKey(requiredParameterName)) {
                arguments[index] = localVariableMap.get(requiredParameterName);

            } else if ("todoList".equals(requiredParameterName)) {
                List<InvokedFunction> todoList = functionData.getTodoList();
                if (todoList == null) throw new ToolkitFunctionException("todo is null");
                arguments[index] = todoList;

            } else if ("trueList".equals(requiredParameterName)) {
                List<InvokedFunction> trueList = functionData.getTrueList();
                if (trueList == null) throw new ToolkitFunctionException("true is null");
                arguments[index] = trueList;

            } else if ("falseList".equals(requiredParameterName)) {
                List<InvokedFunction> falseList = functionData.getFalseList();
                if (falseList == null) throw new ToolkitFunctionException("false is null");
                arguments[index] = falseList;

            } else {
                throw new ToolkitFunctionException(requiredParameterName + " is null");
            }

            index++;
        }

        return arguments;
    }
}
