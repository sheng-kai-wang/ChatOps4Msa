package ntou.soselab.chatops4msa.Service.ToolkitFunctionService;

import ntou.soselab.chatops4msa.Service.DiscordService.JDAService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class DiscordToolkit extends ToolkitFunction implements ApplicationContextAware {
    private JDAService jdaService;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.jdaService = applicationContext.getBean(JDAService.class);
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
    public void toolkitDiscordEmbed(String title, String color, String fieldJson) {
        // TODO: to finish
    }

    /**
     * embed message with thumbnail
     */
    public void toolkitDiscordEmbed(String title, String color, String fieldJson, String thumbnail) {
        // TODO: to finish
    }
}
