package ntou.soselab.chatops4msa.Service.ToolkitFunctionService;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.dv8tion.jda.api.EmbedBuilder;
import ntou.soselab.chatops4msa.Exception.ToolkitFunctionException;
import ntou.soselab.chatops4msa.Service.DiscordService.JDAService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.util.Map;

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
    public void toolkitDiscordEmbed(String title, String color, String field_json) throws ToolkitFunctionException {
        Color colorObj = Color.GRAY;
        if ("green".equals(color)) colorObj = Color.GREEN;
        if ("yellow".equals(color)) colorObj = Color.YELLOW;
        if ("red".equals(color)) colorObj = Color.RED;

        EmbedBuilder eb = new EmbedBuilder().setTitle(title).setColor(colorObj);

        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, String> map;
        try {
            map = objectMapper.readValue(field_json, new TypeReference<Map<String, String>>() {});
        } catch (JsonProcessingException e) {
            throw new ToolkitFunctionException(e.getMessage());
        }
        for (Map.Entry<String, String> entry : map.entrySet()) {
            eb.addField(entry.getKey(), entry.getValue(), false);
        }

        jdaService.sendChatOpsChannelEmbedMessage(eb.build());
    }

    /**
     * embed message with thumbnail
     */
    public void toolkitDiscordEmbed(String title, String color, String field_json, String thumbnail) {
        // TODO: to finish this function
    }
}
