package ntou.soselab.chatops4msa.Service.DiscordService;

import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.springframework.stereotype.Service;

@Service
public class GeneralListener extends ListenerAdapter {
//    @Override
//    public void onSlashCommandsReady(@NotNull SlashCommandsReadyEvent event) {


//
//        MessageCreateBuilder mb = new MessageCreateBuilder();
//        mb.addContent("Here is the report of smoke test.");
//        EmbedBuilder eb = new EmbedBuilder();
//        eb.setTitle("Test Failed - Payment");
//        eb.setDescription("Monitoring of the Payment service and activity status of its repository.");
//        eb.setColor(Color.RED);
//        eb.addField("Uptime", "92.0%", true);
//        eb.addField("Total Series", "647", true);
//        eb.addField("Memory Chunks", "3339", true);
//        eb.addField("Merged Pull Requests", "1", true);
//        eb.addField("Open Pull Requests", "7", true);
//        eb.addField("Closed Issues", "1", true);
//        eb.addField("New Issues", "13", true);
//        eb.setImage("https://media.discordapp.net/attachments/930847447180247070/1112139697376997546/r1Uhall8n.png?width=2237&height=1207");
//        mb.addEmbeds(eb.build());
//
//        Guild guild = event.getJDA().getGuildById("1106888759213051944");
//        TextChannel channel = guild.getTextChannelById("1106889208443969566");
//        channel.sendMessage(mb.build()).queue();
//    }
}
