package ntou.soselab.chatops4msa.Service;

import ntou.soselab.chatops4msa.Entity.Capability.DevOpsTool.LowCode.AccessPermission;
import ntou.soselab.chatops4msa.Entity.Capability.DevOpsTool.LowCode.DeclaredFunction;
import ntou.soselab.chatops4msa.Entity.Capability.DevOpsTool.LowCode.InvokedFunction;
import ntou.soselab.chatops4msa.Exception.CapabilityRoleException;
import ntou.soselab.chatops4msa.Exception.ToolkitFunctionException;
import ntou.soselab.chatops4msa.Service.LowCodeService.CapabilityConfigLoader;
import ntou.soselab.chatops4msa.Service.LowCodeService.LowCodeVariableExtractor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.List;
import java.util.Map;

@Service
public class CapabilityOrchestrator {
    private final Map<String, DeclaredFunction> capabilityMap;

    @Autowired
    public CapabilityOrchestrator(CapabilityConfigLoader configLoader) {
        this.capabilityMap = configLoader.getAllNonPrivateDeclaredFunctionMap();
    }

    public void performTheCapability(String functionName,
                                       Map<String, String> argumentMap,
                                       String roleName) throws CapabilityRoleException, ToolkitFunctionException {
        // get function data
        DeclaredFunction functionData = capabilityMap.get(functionName);

        // Check the role of the user, the "public" keyword is ignored.
        AccessPermission access = functionData.getAccess();
        String accessType = access.getAccess();
        if ("protected".equals(accessType)) {
            List<String> protectedAccess = access.getProtectedAccess();
            if (!protectedAccess.contains(roleName)) {
                String warningMessage = "[WARNING] Sorry, Only The " + protectedAccess + " Can Perform This Capability." +
                        "\n" +
                        "You Are " + roleName + ".";
                throw new CapabilityRoleException(warningMessage);
            }
        }

        // invoke
        invokeCustomFunction(functionData, argumentMap);
    }

    private String invokeCustomFunction(DeclaredFunction functionData,
                                        Map<String, String> argumentMap) throws ToolkitFunctionException {

        // put argumentMap into localVariableMap
        Map<String, String> localVariableMap = functionData.getLocalVariableMap();
        localVariableMap.putAll(argumentMap);

        // invoke all the functions in the body
        List<InvokedFunction> allInvokedFunctionList = functionData.getAllInvokedFunctionList();
        for (InvokedFunction invokedFunction : allInvokedFunctionList) {
            String invokedFunctionName = invokedFunction.getName();

            if (!invokedFunctionName.startsWith("toolkit")) {

                // custom-function
                // prepare the arguments
                Map<String, String> subArgumentMap = invokedFunction.getArgumentMap();
                for (Map.Entry<String, String> entry : subArgumentMap.entrySet()) {
                    String parameterName = entry.getKey();
                    String parameterValue = entry.getValue();
                    parameterValue = assignVariableToDefaultArgument(parameterValue, localVariableMap);
                    subArgumentMap.put(parameterName, parameterValue);
                }

                // invoke
                DeclaredFunction invokedFunctionData = capabilityMap.get(invokedFunctionName);
                String returnValue = invokeCustomFunction(invokedFunctionData, subArgumentMap);

                // assign the return value to the localVariableMap
                if (argumentMap.containsKey("assign")) {
                    String assignName = argumentMap.get("assign");
                    localVariableMap.put(assignName, returnValue);
                } else {
                    localVariableMap.put(functionData.getName(), returnValue);
                }

            } else {

                // toolkit-function
                if ("toolkit-flow-return".equals(invokedFunctionName)) {
                    return invokedFunction.getArgumentMap().get("return");
                }
                invokeToolkitFunction(invokedFunction, localVariableMap);
            }
        }

        return null;
    }

    private void invokeToolkitFunction(InvokedFunction functionData,
                                       Map<String, String> localVariableMap) throws ToolkitFunctionException {

        String functionName = functionData.getName();
        String toolkitClassName = extractToolkitClassName(functionName);
        String toolkitFunctionName = extractToolkitFunctionName(functionName);

        try {
            // prepare the arguments
            Class<?> clazz = Class.forName(toolkitClassName);
            Method method = clazz.getMethod(toolkitFunctionName);
            Object[] arguments = generateArgumentsArray(method.getParameters(), functionData, localVariableMap);

            // invoke the toolkit-function
            Object toolkitClass = clazz.getDeclaredConstructor().newInstance();
            String returnValue = (String) method.invoke(toolkitClass, arguments);

            // assign the return value to the localVariableMap
            Map<String, String> parameterMap = functionData.getArgumentMap();
            if (parameterMap.containsKey("assign")) {
                String assignName = parameterMap.get("assign");
                localVariableMap.put(assignName, returnValue);
            } else {
                localVariableMap.put(functionName, returnValue);
            }

        } catch (ClassNotFoundException |
                 InstantiationException |
                 IllegalAccessException |
                 InvocationTargetException |
                 NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @param functionName like  toolkit-discord-info
     * @return like              DiscordToolkit
     */
    private String extractToolkitClassName(String functionName) {
        String functionType = functionName.split("-")[1];
        String capitalizedFunctionType = functionType.substring(0, 1).toUpperCase() + functionType.substring(1);
        return capitalizedFunctionType + "Toolkit";
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
            Map<String, String> defaultArgumentMap = functionData.getArgumentMap();
            if (defaultArgumentMap.containsKey(requiredParameterName)) {
                String defaultArgument = defaultArgumentMap.get(requiredParameterName);
                defaultArgument = assignVariableToDefaultArgument(defaultArgument, localVariableMap);
                arguments[index] = defaultArgument;

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

    private String assignVariableToDefaultArgument(String defaultArgument, Map<String, String> localVariableMap) {
        String actualArgument = defaultArgument;

        // if the defaultArgument contains variables
        for (String variableName : LowCodeVariableExtractor.extractVariableList(defaultArgument)) {
            String localVariable = localVariableMap.get(variableName);
            actualArgument = LowCodeVariableExtractor.assignVariable(defaultArgument, variableName, localVariable);
        }

        return actualArgument;
    }
}
