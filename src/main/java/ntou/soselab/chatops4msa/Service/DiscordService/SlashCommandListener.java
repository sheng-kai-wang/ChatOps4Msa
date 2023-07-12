package ntou.soselab.chatops4msa.Service.DiscordService;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import ntou.soselab.chatops4msa.Exception.CapabilityRoleException;
import ntou.soselab.chatops4msa.Exception.ToolkitFunctionException;
import ntou.soselab.chatops4msa.Service.CapabilityOrchestrator.CapabilityOrchestrator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class SlashCommandListener extends ListenerAdapter {

    private final CapabilityOrchestrator orchestrator;
    private final JDAService jdaService;

    @Lazy
    @Autowired
    public SlashCommandListener(CapabilityOrchestrator orchestrator, JDAService jdaService) {
        this.orchestrator = orchestrator;
        this.jdaService = jdaService;
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {

        System.out.println(">>> trigger slash command event");

        // print time
        System.out.println("[Time] " + new Date());

        // print command
        String commandName = event.getFullCommandName();
        String declaredFunctionName = commandNameToDeclaredFunctionName(commandName);
        System.out.println("[Command] /" + declaredFunctionName);

        // print options
        Map<String, String> optionMap = new HashMap<>();
        for (OptionMapping optionMapping : event.getOptions()) {
            optionMap.put(optionMapping.getName(), optionMapping.getAsString());
        }
        System.out.println("[Options] " + optionMap);

        // print user name
        System.out.println("[User Name] " + event.getUser().getName());

        // print user roles
        List<String> roleNameList = new ArrayList<>();
        Member member = event.getMember();
        if (member != null) {
            for (Role role : member.getRoles()) {
                roleNameList.add(role.getName());
            }
        }
        System.out.println("[User Role] " + roleNameList);

        // perform the capability
        try {
            orchestrator.performTheCapability(declaredFunctionName, optionMap, roleNameList);
            event.reply("got it\n").queue();

        } catch (CapabilityRoleException e) {
            e.printStackTrace();
            String warningMessage = "[WARNING] " + e.getLocalizedMessage();
            System.out.println(warningMessage);
            jdaService.sendChatOpsChannelWarningMessage(warningMessage);

        } catch (ToolkitFunctionException e) {
            e.printStackTrace();
            String errorMessage = "[ERROR] " + e.getLocalizedMessage();
            System.out.println(errorMessage);
            jdaService.sendChatOpsChannelErrorMessage(errorMessage);

        }

        System.out.println("<<< end of current slash command event");
        System.out.println();
    }

    private String commandNameToDeclaredFunctionName(String commandName) {
        return commandName.replaceAll(" ", "-");
    }
}
