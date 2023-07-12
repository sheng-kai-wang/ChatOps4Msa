package ntou.soselab.chatops4msa.Entity.NLP;

import java.util.List;

public class Capability {
    private final String description;
    private final String name;
    private final List<String> parameterList;

    public Capability(String description, String name, List<String> parameterList) {
        this.description = description.trim();
        this.name = name.trim();
        this.parameterList = parameterList;
    }

    /**
     * example:
     * # Retrieve recent activities of a service's repo.
     * get-github-service_recent_activity:
     *   - service_name
     *   - number_of_activity
     */
    public String toString() {
        StringBuilder sb = new StringBuilder();
        // append the description
        if (description != null && !description.isEmpty()) sb.append("# ").append(description).append("\n");
        // append the name
        sb.append(name).append(":").append("\n");
        // append the parameter
        if (parameterList.isEmpty()) sb.append("  null").append("\n");
        for (String parameter : parameterList) {
            sb.append("  - ").append(parameter).append("\n");
        }
        return sb.toString();
    }

    public List<String> getParameterList() {
        return this.parameterList;
    }
}
