package ntou.soselab.chatops4msa.Service.ToolkitFunctionService;

import ntou.soselab.chatops4msa.Exception.ToolkitFunctionException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class CommandToolkit extends ToolkitFunction {
    public void toolkitCommandBash(String command) throws ToolkitFunctionException {
        System.out.println("[DEBUG] try to execute the bash command:");
        System.out.println("[Command] " + command);

        try {
            ProcessBuilder processBuilder = new ProcessBuilder(command);
            Process process = processBuilder.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
            System.out.println("[Exit Code] " + process.waitFor());

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            throw new ToolkitFunctionException("toolkit-command-bash error");
        }
    }
}
