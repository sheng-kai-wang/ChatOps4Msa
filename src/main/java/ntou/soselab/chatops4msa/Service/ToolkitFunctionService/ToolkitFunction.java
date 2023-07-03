package ntou.soselab.chatops4msa.Service.ToolkitFunctionService;

public class ToolkitFunction {
    String generateFunctionErrorMessage() {
        String functionName = new Object() {
        }.getClass().getEnclosingMethod().getName();
        return camelCaseToKebabCase(functionName) + " error";
    }

    private String camelCaseToKebabCase(String camelCaseFunctionName) {
        String kebabCaseFunctionName = camelCaseFunctionName.replaceAll("([A-Z])", "-$1").toLowerCase();
        return kebabCaseFunctionName.replaceFirst("^-", "");
    }
}
