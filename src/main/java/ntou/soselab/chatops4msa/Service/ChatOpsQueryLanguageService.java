package ntou.soselab.chatops4msa.Service;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import ntou.soselab.chatops4msa.Entity.Capability.DevOpsTool.LowCode.DeclaredFunction;
import ntou.soselab.chatops4msa.Entity.Capability.MicroserviceSystem.MicroserviceSystem;
import ntou.soselab.chatops4msa.Service.DiscordService.JDAService;
import ntou.soselab.chatops4msa.Service.LowCodeService.CapabilityConfigLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChatOpsQueryLanguageService {
    private final JDAService jdaService;
    private CapabilityConfigLoader configLoader;

    @Autowired
    public ChatOpsQueryLanguageService(JDAService jdaService, CapabilityConfigLoader configLoader) {
        this.jdaService = jdaService;
        this.configLoader = configLoader;

        // TODO: generate all slash command (chatops query language)


        JDA jda = jdaService.getJDA();

        // remove original commands
        jda.retrieveCommands().queue(commands -> {
            for (Command command : commands) {
                jda.deleteCommandById(command.getId()).queue();
            }
        });

        // TODO: upsert all commands

        // demo for tcse2023
        OptionData serviceOption = new OptionData(OptionType.STRING, "service", "service name", true)
                .addChoice("Ordering", "Ordering")
                .addChoice("Payment", "Payment")
                .addChoice("Notification", "Notification")
                .addChoice("All Service", "All Service");

        jda.upsertCommand("get-github", "get-github")
                .addSubcommands(
                        new SubcommandData("service_recent_activity", "Retrieve recent activities of a service's repo.")
                                .addOptions(serviceOption)
                                .addOptions(
                                        new OptionData(OptionType.STRING, "number_of_activity", "1 <= n <= 10", true)
                                ),
                        new SubcommandData("service_latest_commit_report", "service_latest_commit_report"),
//                                .addOption(OptionType.STRING, "github", "github", true, true)
//                                .addOption(OptionType.STRING, "k6", "k6", true, true)
//                                .addOption(OptionType.STRING, "prometheus", "prometheus", true, true)
//                                .addOption(OptionType.STRING, "custom_test", "custom_test", true, true),
                        new SubcommandData("service_past_week_team_activity", "service_past_week_team_activity"),
//                                .addOption(OptionType.STRING, "github", "github", true, true)
//                                .addOption(OptionType.STRING, "k6", "k6", true, true)
//                                .addOption(OptionType.STRING, "prometheus", "prometheus", true, true)
//                                .addOption(OptionType.STRING, "custom_test", "custom_test", true, true),
                        new SubcommandData("all_service_risk_past_week", "all_service_dependency_risk_past_week")
//                                .addOption(OptionType.STRING, "github", "github", true, true)
//                                .addOption(OptionType.STRING, "k6", "k6", true, true)
//                                .addOption(OptionType.STRING, "prometheus", "prometheus", true, true)
//                                .addOption(OptionType.STRING, "custom_test", "custom_test", true, true),
                ).queue();
    }

    public void upsertAllCommands(JDA jda) {
//        // TODO: upsert all commands
//        List<DeclaredFunction> allDeclaredFunctionList = configLoader.getAllDeclaredFunctionObjList();
//        for (DeclaredFunction declaredFunction : allDeclaredFunctionList) {
//
//        }
//        jda.upsertCommand("get-github", "get-github").
    }

    private OptionData generateServiceOption() {
        OptionData serviceOption = new OptionData(OptionType.STRING, "service_name", "service name", true);
        for (MicroserviceSystem microserviceSystem : configLoader.microserviceSystemMap.values()) {
            for (String serviceName : microserviceSystem.getAllServiceNameList()) {
                serviceOption.addChoice(serviceName, serviceName);
            }
        }
        return serviceOption.addChoice("all_service", "all_service");
    }

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
}
