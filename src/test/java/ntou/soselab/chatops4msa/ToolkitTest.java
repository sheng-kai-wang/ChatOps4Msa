package ntou.soselab.chatops4msa;

import ntou.soselab.chatops4msa.Entity.ToolkitFunction.StringToolkit;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class ToolkitTest {
    private final StringToolkit stringToolkit;

    @Autowired
    public ToolkitTest(StringToolkit stringToolkit) {
        this.stringToolkit = stringToolkit;
    }

    @Test
    public void toolkitStringPatternTest() {
        String s = stringToolkit.toolkitStringPattern("15", "^[1-9]|10$");
        System.out.println("s: " + s);
    }
}
