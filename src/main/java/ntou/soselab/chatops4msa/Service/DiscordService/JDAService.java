package ntou.soselab.chatops4msa.Service.DiscordService;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.requests.GatewayIntent;
import ntou.soselab.chatops4msa.Exception.DiscordIdException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

@Service
public class JDAService {

    private JDA jda;
    private String guildId;
    private String channelChatOpsId;

    @Autowired
    public JDAService(Environment env,
                      DiscordGeneralListener generalListener,
                      DiscordSlashCommandListener slashCommandListener,
                      DiscordMessageListener messageListener,
                      DiscordButtonListener buttonListener) {

        final String APP_TOKEN = env.getProperty("discord.application.token");
        try {
            this.jda = JDABuilder.createDefault(APP_TOKEN)
                    .enableIntents(GatewayIntent.MESSAGE_CONTENT)
                    .enableIntents(GatewayIntent.GUILD_MEMBERS)
                    .addEventListeners(generalListener)
                    .addEventListeners(slashCommandListener)
                    .addEventListeners(messageListener)
                    .addEventListeners(buttonListener)
                    .build()
                    .awaitReady();

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        this.guildId = env.getProperty("discord.guild.id");
        this.channelChatOpsId = env.getProperty("discord.channel.chatops.id");

        System.out.println();
        System.out.println("[DEBUG] JDA START!");
        System.out.println();
    }

    public void sendChatOpsChannelInfoMessage(String message) {
        sendChatOpsChannelMessage("```yaml\n" + message + "```");
    }

    public void sendChatOpsChannelWarningMessage(String message) {
        sendChatOpsChannelMessage("```prolog\n" + message + "```");
    }

    public void sendChatOpsChannelErrorMessage(String message) {
        sendChatOpsChannelMessage("```ml\n" + message + "```");
    }

    public void sendChatOpsChannelBlocksMessage(String message) {
        sendChatOpsChannelMessage("```\n" + message + "```");
    }

    private void sendChatOpsChannelMessage(String message) {
        try {
            Guild guild = jda.getGuildById(guildId);
            if (guild == null) {
                System.out.println("[ERROR] the guild ID is incorrect");
                throw new DiscordIdException("the guild ID is incorrect");
            }

            TextChannel channel = guild.getTextChannelById(channelChatOpsId);
            if (channel == null) {
                System.out.println("[ERROR] the chatops channel ID is incorrect");
                throw new DiscordIdException("the chatops channel ID is incorrect");
            }

            channel.sendMessage(message).queue();

        } catch (DiscordIdException e) {
            throw new RuntimeException(e);
        }
    }
}
