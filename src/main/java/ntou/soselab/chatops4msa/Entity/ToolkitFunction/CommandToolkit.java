package ntou.soselab.chatops4msa.Entity.ToolkitFunction;

import ntou.soselab.chatops4msa.Service.DiscordService.JDAService;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.Executors;

@Component
public class CommandToolkit extends ToolkitFunction {
    private final JDAService jdaService;

    public CommandToolkit(JDAService jdaService) {
        this.jdaService = jdaService;
    }

    public void toolkitCommandBash(String command, String input_stream) {
        String[] commands = command.split(" ");

        CommandToolkit.executeInBackground(() -> {
            try {
                ProcessBuilder processBuilder = new ProcessBuilder(commands);
                processBuilder.redirectInput(new File(input_stream));
                Process process = processBuilder.start();

                // testing started
                jdaService.sendChatOpsChannelMessage("=============== COMMAND START ===============\n");

                StringBuilder sb = new StringBuilder();
                sb.append("```c").append("\n");

                // success output
                String line;
                BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                while ((line = reader.readLine()) != null) {
                    System.out.println(line);
                    if (line.contains("..:")) sb.append(line.trim()).append("\n");
                }

                // error output
                reader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
                while ((line = reader.readLine()) != null) {
                    System.out.println(line);
                    if (line.contains("..:")) sb.append(line.trim()).append("\n");
                }

                sb.append("```");
                jdaService.sendChatOpsChannelMessage(sb.toString());

                // testing completed
                jdaService.sendChatOpsChannelMessage("\n=============== COMMAND END ===============");

            } catch (IOException e) {
                e.printStackTrace();
                jdaService.sendChatOpsChannelErrorMessage("[ERROR] " + e.getLocalizedMessage());
            }
        });
    }

    private static void executeInBackground(Runnable task) {
        Executors.newSingleThreadExecutor().execute(task);
    }
}
