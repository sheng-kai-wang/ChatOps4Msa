package ntou.soselab.chatops4msa.Service.ToolkitFunctionService;

import ntou.soselab.chatops4msa.Service.DiscordService.JDAService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * For ease of invocation by the CapabilityOrchestrator,
 * the parameters are using snake case, similar to low-code.
 */
@Component
public class DiscordToolkit extends ToolkitFunction {
    private final JDAService jdaService;

    @Autowired
    public DiscordToolkit(JDAService jdaService) {
        this.jdaService = jdaService;
    }

    /**
     * general text message
     */
    public void toolkitDiscordText(String text) {
        jdaService.sendChatOpsChannelMessage(text);
    }

    /**
     * blue info message
     */
    public void toolkitDiscordInfo(String text) {
        jdaService.sendChatOpsChannelInfoMessage(text);
    }

    /**
     * orange warning message
     */
    public void toolkitDiscordWarning(String text) {
        jdaService.sendChatOpsChannelWarningMessage(text);
    }

    /**
     * red error message
     */
    public void toolkitDiscordError(String text) {
        jdaService.sendChatOpsChannelErrorMessage(text);
    }

    /**
     * blocks message
     */
    public void toolkitDiscordBlocks(String text) {
        jdaService.sendChatOpsChannelBlocksMessage(text);
    }

    /**
     * embed message
     */
    public void toolkitDiscordEmbed(String title, String color, String field_json) {
        // TODO: to finish this function
    }

    /**
     * embed message with thumbnail
     */
    public void toolkitDiscordEmbed(String title, String color, String field_json, String thumbnail) {
        // TODO: to finish this function
    }
}
