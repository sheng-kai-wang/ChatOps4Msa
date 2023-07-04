package ntou.soselab.chatops4msa.Service.ToolkitFunctionService;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import ntou.soselab.chatops4msa.Exception.ToolkitFunctionException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringToolkit extends ToolkitFunction {

    /**
     * @param string   like "hello world"
     * @param original like "world"
     * @param replace  like "world2"
     * @return like "hello world2"
     */
    public String toolkitStringReplace(String string, String original, String replace) {
        return string.replaceAll(original, replace);
    }

    /**
     * @param string    like "https://github.com/sheng-kai-wang/ChatOps4Msa-Sample-Bookinfo.git"
     * @param separator like "/|\\."
     * @return like ["https:", "", "github", "com", "sheng-kai-wang", "ChatOps4Msa-Sample-Bookinfo", "git"]
     */
    public String toolkitStringSplit(String string, String separator) throws ToolkitFunctionException {
        String json;
        try {
            String[] split = string.split(separator);
            json = new ObjectMapper().writeValueAsString(split);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            throw new ToolkitFunctionException("toolkit-string-split error");
        }
        return json;
    }

    /**
     * @param string like "6"
     * @param regex  like "^(?!(?:[1-9]|10)$)\d+$"
     * @return like "true"
     */
    public String toolkitStringPattern(String string, String regex) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(string);
        return matcher.matches() ? "true" : "false";
    }
}
