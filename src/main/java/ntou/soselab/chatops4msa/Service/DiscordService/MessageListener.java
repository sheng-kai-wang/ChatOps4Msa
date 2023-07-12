package ntou.soselab.chatops4msa.Service.DiscordService;

import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import ntou.soselab.chatops4msa.Service.NLPService.DialogueTracker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class MessageListener extends ListenerAdapter {
    private final String GUILD_ID;
    private final String CHATOPS_CHANNEL_ID;
    private final DialogueTracker dialogueTracker;

    @Lazy
    @Autowired
    public MessageListener(Environment env, DialogueTracker dialogueTracker) {
        this.GUILD_ID = env.getProperty("discord.guild.id");
        this.CHATOPS_CHANNEL_ID = env.getProperty("discord.channel.chatops.id");
        this.dialogueTracker = dialogueTracker;
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.getAuthor().isBot()) return;

        if (shouldReply(event)) {

            System.out.println(">>> trigger message event");

            System.out.println("[TIME] " + new Date());

            event.getChannel().sendMessage("got it, processing...\n").queue();

            String userId = event.getAuthor().getId();
            String userName = event.getAuthor().getName();
            String userInput = event.getMessage().getContentRaw();

            System.out.println("[DEBUG] Receive Message");
            System.out.println("[User Name] " + userName);
            System.out.println("[User Input] " + userInput);

            try {
                MessageCreateData response = dialogueTracker.sendMessage(userId, userName, userInput);
                event.getChannel().sendMessage(response).queue();

            } catch (Exception e) {
                String errorMessage = "```prolog" + "\n[WARNING] Sorry, The System Is Currently Overloaded With Other Requests.```";
                event.getChannel().sendMessage(errorMessage).queue();
                System.out.println("[Error] maybe the system is currently overloaded with other requests.");
                System.out.println(e.getMessage());
                e.printStackTrace();

            } finally {
                System.out.println("<<< end of current message event");
                System.out.println();
            }
        }
    }

    /**
     * only for the chatops channel
     */
    private boolean shouldReply(MessageReceivedEvent event) {
        if (event.getAuthor().isBot()) return false;
        if (!event.isFromGuild()) return false;
        if (!event.getGuild().getId().equals(GUILD_ID)) return false;
        return event.getChannel().getId().equals(CHATOPS_CHANNEL_ID);
    }
}
