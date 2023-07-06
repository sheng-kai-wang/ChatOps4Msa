package ntou.soselab.chatops4msa.Service.ToolkitFunctionService;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonArray;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.PathNotFoundException;
import ntou.soselab.chatops4msa.Exception.ToolkitFunctionException;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class JsonToolkit extends ToolkitFunction {
    public String toolkitJsonParse(String json, String jsonpath) {
        DocumentContext jsonContext = JsonPath.parse(json);
        return jsonContext.read(jsonpath);
    }

    public String toolkitJsonParseGithubEmbed(String json, String first) throws ToolkitFunctionException {
        try {
            int length = Integer.parseInt(first) + 1;
            List<String> authorName = JsonPath.parse(json).read("$[0:" + length + "].payload.commits[0].author.name");
            List<String> message = JsonPath.parse(json).read("$[0:" + length + "].payload.commits[0].message");
            List<String> url = JsonPath.parse(json).read("$[0:" + length + "].payload.commits[0].url");
            List<String> createdAt = JsonPath.parse(json).read("$[0:" + length + "].created_at");

            System.err.println("authorName: " + authorName.size());

            JSONArray array = new JSONArray();
            for (int i = 0; i < authorName.size(); i++) {
                JSONObject object = new JSONObject();
                object.put("author", authorName.get(i));
                object.put("message", message.get(i));
                object.put("url", url.get(i));
                object.put("createdAt", createdAt.get(i));
                array.put(object);
            }

            System.err.println("array: " + array.length());

            return array.toString();

        } catch (PathNotFoundException e) {
            e.printStackTrace();
            throw new ToolkitFunctionException(e.getMessage());
        }
    }
}
