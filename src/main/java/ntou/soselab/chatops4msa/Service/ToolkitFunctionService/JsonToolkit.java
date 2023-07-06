package ntou.soselab.chatops4msa.Service.ToolkitFunctionService;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import org.springframework.stereotype.Component;

@Component
public class JsonToolkit extends ToolkitFunction {
    public String toolkitJsonParse(String json, String jsonpath) {
        DocumentContext jsonContext = JsonPath.parse(json);
        return jsonContext.read(jsonpath);
    }
}
