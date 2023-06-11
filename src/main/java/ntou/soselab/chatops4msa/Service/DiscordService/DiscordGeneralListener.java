package ntou.soselab.chatops4msa.Service.DiscordService;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.internal.interactions.CommandDataImpl;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.awt.*;

@Service
public class DiscordGeneralListener extends ListenerAdapter {

    @Override
    public void onReady(@NotNull ReadyEvent event) {

        // demo for tcse2023
//        OptionData serviceOption = new OptionData(OptionType.STRING, "service", "service name", true)
//                .addChoice("Ordering", "Ordering")
//                .addChoice("Payment", "Payment")
//                .addChoice("Notification", "Notification")
//                .addChoice("All Service", "All Service");
//
//        event.getJDA()
//                .upsertCommand("get-github", "get-github")
//                .addSubcommands(
//                        new SubcommandData("service_recent_activity", "Retrieve recent activities of a service's repo.")
//                                .addOptions(serviceOption)
//                                .addOptions(
//                                        new OptionData(OptionType.STRING, "number_of_activity", "1 <= n <= 10", true)
//                                ),
//                        new SubcommandData("service_latest_commit_report", "service_latest_commit_report"),
////                                .addOption(OptionType.STRING, "github", "github", true, true)
////                                .addOption(OptionType.STRING, "k6", "k6", true, true)
////                                .addOption(OptionType.STRING, "prometheus", "prometheus", true, true)
////                                .addOption(OptionType.STRING, "custom_test", "custom_test", true, true),
//                        new SubcommandData("service_past_week_team_activity", "service_past_week_team_activity"),
////                                .addOption(OptionType.STRING, "github", "github", true, true)
////                                .addOption(OptionType.STRING, "k6", "k6", true, true)
////                                .addOption(OptionType.STRING, "prometheus", "prometheus", true, true)
////                                .addOption(OptionType.STRING, "custom_test", "custom_test", true, true),
//                        new SubcommandData("all_service_risk_past_week", "all_service_dependency_risk_past_week")
////                                .addOption(OptionType.STRING, "github", "github", true, true)
////                                .addOption(OptionType.STRING, "k6", "k6", true, true)
////                                .addOption(OptionType.STRING, "prometheus", "prometheus", true, true)
////                                .addOption(OptionType.STRING, "custom_test", "custom_test", true, true),
//                ).queue();
//
//        event.getJDA()
//                .upsertCommand("test-k6", "test-k6")
//                .addSubcommands(
//                        new SubcommandData("stress_testing", "Perform stress testing on a specific service.")
//                                .addOptions(serviceOption)
//                                .addOptions(
//                                        new OptionData(OptionType.STRING, "virtual_user", "1 <= n <= 5", true)
//                                ),
//                        new SubcommandData("service_latest_commit_report", "service_latest_commit_report"),
////                                .addOption(OptionType.STRING, "github", "github", true, true)
////                                .addOption(OptionType.STRING, "k6", "k6", true, true)
////                                .addOption(OptionType.STRING, "prometheus", "prometheus", true, true)
////                                .addOption(OptionType.STRING, "custom_test", "custom_test", true, true),
//                        new SubcommandData("service_past_week_team_activity", "service_past_week_team_activity"),
////                                .addOption(OptionType.STRING, "github", "github", true, true)
////                                .addOption(OptionType.STRING, "k6", "k6", true, true)
////                                .addOption(OptionType.STRING, "prometheus", "prometheus", true, true)
////                                .addOption(OptionType.STRING, "custom_test", "custom_test", true, true),
//                        new SubcommandData("all_service_risk_past_week", "all_service_dependency_risk_past_week")
////                                .addOption(OptionType.STRING, "github", "github", true, true)
////                                .addOption(OptionType.STRING, "k6", "k6", true, true)
////                                .addOption(OptionType.STRING, "prometheus", "prometheus", true, true)
////                                .addOption(OptionType.STRING, "custom_test", "custom_test", true, true),
//                ).queue();
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
    }
}
