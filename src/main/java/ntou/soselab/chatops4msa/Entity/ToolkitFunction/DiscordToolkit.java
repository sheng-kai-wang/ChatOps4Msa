package ntou.soselab.chatops4msa.Entity.ToolkitFunction;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;
import net.dv8tion.jda.api.EmbedBuilder;
import ntou.soselab.chatops4msa.Exception.ToolkitFunctionException;
import ntou.soselab.chatops4msa.Service.DiscordService.JDAService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.util.Map;

/**
 * For ease of invocation by the Capability Orchestrator,
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
     * JSON message
     */
    public void toolkitDiscordJson(String text) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String formattedJson = gson.toJson(JsonParser.parseString(text));
        jdaService.sendChatOpsChannelBlocksMessage(formattedJson);
    }

    /**
     * Embed message
     */
    public void toolkitDiscordEmbed(String title,
                                    String color,
                                    String field_json) throws ToolkitFunctionException {

        processToolkitDiscordEmbed(title, color, field_json, null, null);
    }

    /**
     * Embed message with thumbnail
     */
    public void toolkitDiscordEmbedThumbnail(String title,
                                             String color,
                                             String field_json,
                                             String thumbnail) throws ToolkitFunctionException {

        processToolkitDiscordEmbed(title, color, field_json, thumbnail, null);
    }

    /**
     * Embed message with image
     */
    public void toolkitDiscordEmbedImage(String title,
                                         String color,
                                         String field_json,
                                         String image) throws ToolkitFunctionException {

        processToolkitDiscordEmbed(title, color, field_json, null, image);
    }

    private void processToolkitDiscordEmbed(String title,
                                            String color,
                                            String field_json,
                                            String thumbnail,
                                            String image) throws ToolkitFunctionException {
        Color colorObj = parseColor(color);

        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, String> map;
        try {
            map = objectMapper.readValue(field_json, new TypeReference<Map<String, String>>() {
            });
        } catch (JsonProcessingException e) {
            throw new ToolkitFunctionException(e.getOriginalMessage());
        }

        EmbedBuilder eb = new EmbedBuilder().setTitle(title).setColor(colorObj);
        if (thumbnail != null) eb.setThumbnail(thumbnail);
        if (image != null) eb.setImage(image);
        for (Map.Entry<String, String> entry : map.entrySet()) {
            eb.addField(entry.getKey(), entry.getValue(), false);
        }
        jdaService.sendChatOpsChannelEmbedMessage(eb.build());
    }

    private Color parseColor(String colorName) {
        Color colorObj = Color.GRAY;
        if ("green".equals(colorName)) colorObj = Color.GREEN;
        if ("orange".equals(colorName)) colorObj = Color.ORANGE;
        if ("red".equals(colorName)) colorObj = Color.RED;
        return colorObj;
    }
}
