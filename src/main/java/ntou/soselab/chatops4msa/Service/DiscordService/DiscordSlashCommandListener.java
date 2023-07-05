package ntou.soselab.chatops4msa.Service.DiscordService;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import ntou.soselab.chatops4msa.Service.CapabilityOrchestratorService.CapabilityOrchestrator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class DiscordSlashCommandListener extends ListenerAdapter {

//    @Autowired
//    private CapabilityOrchestrator orchestrator;

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        System.out.println(">>> trigger slash command event");
        String commandName = event.getFullCommandName();
        String declaredFunctionName = commandNameToDeclaredFunctionName(commandName);
        System.out.println("[Command] " + declaredFunctionName);

        Map<String, String> optionMap = new HashMap<>();
        for (OptionMapping optionMapping : event.getOptions()) {
            optionMap.put(optionMapping.getName(), optionMapping.getAsString());
        }
        System.out.println("[Options] " + optionMap);



//        if (event.getName().equals("tag")) {
//            event.deferReply().queue(); // Tell discord we received the command, send a thinking... message to the user
//            String tagName = event.getOption("name").getAsString();
//            TagDatabase.fingTag(tagName,
//                    (tag) -> event.getHook().sendMessage(tag).queue() // delayed response updates our inital "thinking..." message with the tag value
//            );
//        }

        System.out.println("<<< end of current slash command event");
        System.out.println();
    }

    private String commandNameToDeclaredFunctionName(String commandName) {
        return commandName.replaceAll(" ", "-");
    }

//    @SubscribeEvent
//    public void onSlashCommand(SlashCommandEvent event) {
//        if (event.getCommandName().equals("commandName")) {
//            // 处理 Command
//            // 可以使用 event.getOption("optionName") 获取 Command 的选项值
//            // 例如：event.getOption("username").getAsString()
//
//        } else if (event.getSubcommandGroup() != null) {
//            if (event.getSubcommandGroup().equals("subcommandGroup")) {
//                if (event.getSubcommandName().equals("subcommandName")) {
//                    // 处理 Subcommand
//                    // 可以使用 event.getOption("optionName") 获取 Subcommand 的选项值
//                }
//            }
//        }
//    }
}
