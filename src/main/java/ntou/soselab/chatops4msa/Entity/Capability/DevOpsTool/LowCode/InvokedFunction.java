package ntou.soselab.chatops4msa.Entity.Capability.DevOpsTool.LowCode;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import ntou.soselab.chatops4msa.Service.LowCodeService.InvokedFunctionNameDeserializer;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import java.io.IOException;
import java.util.List;
import java.util.Map;

//@JsonDeserialize(using = InvokedFunctionNameDeserializer.class)
public class InvokedFunction {

//    @JsonSetter("json")
    private String name;
    //    @JsonProperty("toolkit-json-parse")
//    @JsonAnySetter
//    private Map<String, Object> parameterMap;
//    @JsonSetter("todo")
//    private List<InvokedFunction> todoList;
//    @JsonProperty("true")
//    private List<InvokedFunction> trueList;
//    @JsonProperty("false")
//    private List<InvokedFunction> falseList;

//    public void setName(String name) {
//        this.name = name;
//    }
//
//    public void setParameterMap(Map<String, String> parameterMap) {
//        this.parameterMap = parameterMap;
//    }

    @JsonSetter("json")
    public void setJson(String name) {
        this.name = name;
    }

    public String verify() {
//        final Map<String, List<String>> toolkitSpecialCaseConfigMap = loadToolkitSpecialCaseConfig();
//
//        StringBuilder sb = new StringBuilder();
//
//        // name verify
//        if (name == null) sb.append("          name is null").append("\n");
//
//        // parameter verify
//        if (parameterMap == null) {
//            if (!toolkitSpecialCaseConfigMap.get("function_without_parameter").contains(name)) {
//                sb.append("          parameter is null").append("\n");
//            }
//
//        } else {
//            for (Map.Entry<String, String> entry : parameterMap.entrySet()) {
//                if (entry.getValue() == null) {
//                    String parameterName = entry.getKey();
//                    if (!toolkitSpecialCaseConfigMap.get("nullable_parameter_of_all_function").contains(parameterName)) {
//                        sb.append("          parameter[").append(parameterName).append("] is null").append("\n");
//                    }
//                }
//            }
//        }
//
//        return sb.toString();
        return "";
    }

    private Map<String, List<String>> loadToolkitSpecialCaseConfig() {
        final String TOOLKIT_SPECIAL_CASE_CLASSPATH = "classpath*:toolkit_special_case.{yml,yaml}";

        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver(classLoader);
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());

        try {
            Resource[] resources = resolver.getResources(TOOLKIT_SPECIAL_CASE_CLASSPATH);
            return mapper.readValue(resources[0].getInputStream(), Map.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
