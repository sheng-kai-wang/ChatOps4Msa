package ntou.soselab.chatops4msa.Entity.Capability.DevOpsTool.LowCode;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import ntou.soselab.chatops4msa.Service.LowCodeService.InvokedOperationNameDeserializer;

import java.util.Map;

@JsonDeserialize(using = InvokedOperationNameDeserializer.class)
public class InvokedOperation {

    private String name;
    private Map<String, String> parameterMap;

    public void setName(String name) {
        this.name = name;
    }

    public String verify() {
        StringBuilder sb = new StringBuilder();

        // name verify
        if (name == null) sb.append("        name is null").append("\n");

        // parameter verify


        return sb.toString();
    }
}