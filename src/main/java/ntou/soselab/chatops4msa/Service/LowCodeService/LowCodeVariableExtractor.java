package ntou.soselab.chatops4msa.Service.LowCodeService;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LowCodeVariableExtractor {
    private static final String VARIABLE_REGEX = "\\$\\{([^}]+)}";

    /**
     * ${custom_variable} -> custom_variable
     */
    public static List<String> extractVariableList(String variableString) {
        ArrayList<String> variableNameList = new ArrayList<>();
        Matcher matcher = Pattern.compile(VARIABLE_REGEX).matcher(variableString);
        while (matcher.find()) variableNameList.add(matcher.group(1));
        return variableNameList;
    }

    /**
     * @param string        like "hello, ${name}"
     * @param variableName  like "name"
     * @param variableValue like "kermit"
     * @return like "hello, kermit"
     */
    public static String assignVariable(String string, String variableName, String variableValue) {
        String variablePattern = "\\$\\{" + variableName + "}";
        return string.replaceAll(variablePattern, variableValue);
    }

    public static boolean hasVariable(String parameterValue) {
        return Pattern.compile(VARIABLE_REGEX).matcher(parameterValue).find();
    }
}