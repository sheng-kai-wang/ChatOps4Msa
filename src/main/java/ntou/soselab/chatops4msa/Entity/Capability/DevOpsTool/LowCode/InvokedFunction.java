package ntou.soselab.chatops4msa.Entity.Capability.DevOpsTool.LowCode;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import java.io.IOException;
import java.util.*;

public class InvokedFunction {
    private String functionName;
    private Map<String, String> parameterMap;
    private String assign;
    private List<InvokedFunction> todoList;
    private List<InvokedFunction> trueList;
    private List<InvokedFunction> falseList;

    private transient StringBuilder errorMessageSb = new StringBuilder();
    private transient final String TOOLKIT_VERIFY_CLASSPATH = "classpath*:toolkit_verify.{yml,yaml}";
    private transient final Map<String, List<String>> toolkitVerifyConfigMap;

    public InvokedFunction() {
        this.toolkitVerifyConfigMap = loadToolkitVerifyConfig();
    }

    @JsonAnySetter
    public void setFunctionContent(String functionName, Map<String, Object> parameterMap) {
        this.functionName = functionName;
        this.parameterMap = new HashMap<>();

        if (parameterMap == null) return;
        for (Map.Entry<String, Object> entry : parameterMap.entrySet()) {
            String parameterName = entry.getKey();
            Object parameterValue = entry.getValue();
            if (parameterValue instanceof String) {
                if ("assign".equals(parameterName)) this.assign = (String) parameterValue;
                else this.parameterMap.put(parameterName, (String) parameterValue);

            } else if (parameterValue instanceof Integer) {
                this.parameterMap.put(parameterName, parameterValue.toString());

            } else if (parameterValue instanceof List<?> specialParameter) {
                if (specialParameter.isEmpty()) appendParameterFormatErrorMessage(parameterName);
                List<InvokedFunction> specialParameterFunctionList = new ArrayList<>();
                ObjectMapper mapper = new ObjectMapper();
                for (Object obj : specialParameter) {
                    InvokedFunction invokedFunction = mapper.convertValue(obj, InvokedFunction.class);
                    specialParameterFunctionList.add(invokedFunction);
                }
                if ("todo".equals(parameterName)) this.todoList = specialParameterFunctionList;
                else if ("true".equals(parameterName)) this.trueList = specialParameterFunctionList;
                else if ("false".equals(parameterName)) this.falseList = specialParameterFunctionList;
                else appendParameterFormatErrorMessage(parameterName);

            } else {
                appendParameterFormatErrorMessage(parameterName);
            }
        }
    }

    private void appendParameterFormatErrorMessage(String parameterName) {
        errorMessageSb
                .append("          the parameter format [")
                .append(parameterName)
                .append("] is incorrect")
                .append("\n");
    }

    public String verify() {
        // name verify
        if (functionName == null) errorMessageSb.append("          name is null").append("\n");

        // parameter verify
        if (!toolkitVerifyConfigMap.containsKey(functionName)) return errorMessageSb.toString();
        for (String requiredParameter : toolkitVerifyConfigMap.get(functionName)) {
            if (!parameterMap.containsKey(requiredParameter)) {
                if ("todo".equals(requiredParameter) && todoList != null) continue;
                if ("true".equals(requiredParameter) && trueList != null) continue;
                if ("false".equals(requiredParameter) && falseList != null) continue;
                errorMessageSb
                        .append("          the parameter \"")
                        .append(requiredParameter)
                        .append("\" is missing in ")
                        .append(functionName)
                        .append("\n");
            }
        }

        // special parameter verify
        verifySpecialParameter("todo", todoList);
        verifySpecialParameter("true", trueList);
        verifySpecialParameter("false", falseList);

        return errorMessageSb.toString();
    }

    private void verifySpecialParameter(String parameterName, List<InvokedFunction> specialParameter) {
        if (specialParameter == null) return;
        for (InvokedFunction function : specialParameter) {
            String functionErrorMessage = function.verify();
            if (!"".equals(functionErrorMessage)) {
                errorMessageSb.append("          ").append(parameterName).append(" function error ===").append("\n");
                errorMessageSb.append(functionErrorMessage).append("\n");
                errorMessageSb.append("          =======================").append("\n");
            }
        }
    }

    private Map<String, List<String>> loadToolkitVerifyConfig() {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver(classLoader);
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        try {
            Resource[] resources = resolver.getResources(TOOLKIT_VERIFY_CLASSPATH);
            return mapper.readValue(resources[0].getInputStream(), Map.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
