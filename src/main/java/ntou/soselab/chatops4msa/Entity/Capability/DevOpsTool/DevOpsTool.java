package ntou.soselab.chatops4msa.Entity.Capability.DevOpsTool;

import com.fasterxml.jackson.annotation.JsonProperty;
import ntou.soselab.chatops4msa.Entity.Capability.Configs;
import ntou.soselab.chatops4msa.Entity.Capability.Info;
import ntou.soselab.chatops4msa.Entity.Capability.DevOpsTool.LowCode.LowCode;

public class DevOpsTool implements Configs {

    @JsonProperty("info")
    private Info info;
    @JsonProperty("low_code")
    private LowCode lowCode;

    public String verify() {
        StringBuilder sb = new StringBuilder();

        // info verify
        String infoErrorMessage = info.verify();
        if (!"".equals(infoErrorMessage)) {
            sb.append("  info error:").append("\n")
                    .append(infoErrorMessage).append("\n");
        }

        // low-code verify
        String lowCodeErrorMessage = lowCode.verify();
        if (!"".equals(lowCodeErrorMessage)) {
            sb.append("  low-code error:").append("\n")
                    .append(lowCodeErrorMessage).append("\n");
        }

        return sb.toString();
    }
}
