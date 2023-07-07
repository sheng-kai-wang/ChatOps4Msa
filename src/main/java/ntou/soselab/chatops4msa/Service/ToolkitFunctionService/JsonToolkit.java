package ntou.soselab.chatops4msa.Service.ToolkitFunctionService;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonArray;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.PathNotFoundException;
import ntou.soselab.chatops4msa.Exception.ToolkitFunctionException;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class JsonToolkit extends ToolkitFunction {
    public String toolkitJsonParse(String json, String jsonpath) {
        return JsonPath.parse(json).read(jsonpath).toString();
    }

    /**
     * Writing "Github" intentionally instead of "GitHub" is for the convenience of function name conversion.
     */
    public String toolkitJsonParseGithubCommit(String json, String first) throws ToolkitFunctionException {
        try {
            int length = Integer.parseInt(first) + 1;
            List<String> author = JsonPath.parse(json).read("$[0:" + length + "].commit.author.name");
            List<String> message = JsonPath.parse(json).read("$[0:" + length + "].commit.message");
            List<String> url = JsonPath.parse(json).read("$[0:" + length + "].html_url");
            List<String> date = JsonPath.parse(json).read("$[0:" + length + "].commit.author.date");

            JSONArray array = new JSONArray();
            for (int i = 0; i < author.size(); i++) {
                JSONObject object = new JSONObject();
                object.put("author", author.get(i));
                object.put("message", message.get(i));
                object.put("url", url.get(i));
                object.put("date", date.get(i));
                array.put(object);
            }

            return array.toString();

        } catch (PathNotFoundException e) {
            e.printStackTrace();
            throw new ToolkitFunctionException(e.getMessage());
        }
    }

    /**
     * Writing "Github" intentionally instead of "GitHub" is for the convenience of function name conversion.
     */
    public String toolkitJsonParseGithubIssue(String json, String first) throws ToolkitFunctionException {
        try {
            int length = Integer.parseInt(first) + 1;
            List<String> htmlUrl = JsonPath.parse(json).read("$[0:" + length + "].html_url");
            List<String> author = JsonPath.parse(json).read("$[0:" + length + "].user.login");
            List<String> createdAt = JsonPath.parse(json).read("$[0:" + length + "].created_at");
            List<String> body = JsonPath.parse(json).read("$[0:" + length + "].body");
            List<String> event = JsonPath.parse(json).read("$[0:" + length + "].event");

            JSONArray array = new JSONArray();
            for (int i = 0; i < author.size(); i++) {
                JSONObject object = new JSONObject();
                object.put("html url", htmlUrl.get(i));
                object.put("author", author.get(i));
                object.put("created at", createdAt.get(i));
                object.put("body", body.get(i));
                object.put("event", event.get(i));
                array.put(object);
            }

            return array.toString();

        } catch (PathNotFoundException e) {
            e.printStackTrace();
            throw new ToolkitFunctionException(e.getMessage());
        }
    }
}
