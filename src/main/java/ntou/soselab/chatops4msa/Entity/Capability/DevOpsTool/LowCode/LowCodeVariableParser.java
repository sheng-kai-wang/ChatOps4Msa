package ntou.soselab.chatops4msa.Entity.Capability.DevOpsTool.LowCode;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class LowCodeVariableParser {
    private static final String VARIABLE_REGEX = "\\$\\{([^}]+)}";

    static List<String> extractVariableList(String variableString) {
        ArrayList<String> variableNameList = new ArrayList<>();
        Matcher matcher = Pattern.compile(VARIABLE_REGEX).matcher(variableString);
        while (matcher.find()) variableNameList.add(matcher.group(1));
        return variableNameList;
    }
}