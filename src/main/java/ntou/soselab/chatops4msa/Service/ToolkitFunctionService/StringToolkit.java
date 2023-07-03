package ntou.soselab.chatops4msa.Service.ToolkitFunctionService;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringToolkit extends ToolkitFunction {

    /**
     * @param string like "hello world"
     * @param original like "world"
     * @param replace like "world2"
     * @return like "hello world2"
     */
    public String toolkitStringReplace(String string, String original, String replace) {
        return string.replaceAll(original, replace);
    }

    /**
     * @param string like "https://github.com/sheng-kai-wang/ChatOps4Msa-Sample-Bookinfo.git"
     * @param separator like "/|\\."
     * @return like ["https:", "", "github", "com", "sheng-kai-wang", "ChatOps4Msa-Sample-Bookinfo", "git"]
     */
    public String[] toolkitStringSplit(String string, String separator) {
        return string.split(separator);
    }

    /**
     * @param string like "6"
     * @param regex like "^(?!(?:[1-9]|10)$)\d+$"
     * @return like true
     */
    public boolean toolkitStringPattern(String string, String regex) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(string);
        return matcher.matches();
    }
}
