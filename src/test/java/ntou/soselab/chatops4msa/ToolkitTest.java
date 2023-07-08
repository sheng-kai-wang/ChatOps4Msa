package ntou.soselab.chatops4msa;

import ntou.soselab.chatops4msa.Entity.ToolkitFunction.JsonToolkit;
import ntou.soselab.chatops4msa.Entity.ToolkitFunction.StringToolkit;
import ntou.soselab.chatops4msa.Entity.ToolkitFunction.TimeToolkit;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class ToolkitTest {
    private final StringToolkit stringToolkit;
    private final JsonToolkit jsonToolkit;
    private final TimeToolkit timeToolkit;

    @Autowired
    public ToolkitTest(StringToolkit stringToolkit, JsonToolkit jsonToolkit, TimeToolkit timeToolkit) {
        this.stringToolkit = stringToolkit;
        this.jsonToolkit = jsonToolkit;
        this.timeToolkit = timeToolkit;
    }

    @Test
    public void toolkitStringPatternTest() {
        String s = stringToolkit.toolkitStringPattern("15", "^[1-9]|10$");
        System.out.println("s: " + s);
    }

    @Test
    public void toolkitStringReplaceTest() {
        String graphql = """
                                {
                  repository(owner: $owner, name: $name) {
                    defaultBranchRef {
                      name
                    }
                    ref(qualifiedName: "refs/heads/master") {
                      target {
                        ... on Commit {
                          history(first: $first) {
                            nodes {
                              oid
                              messageHeadline
                              messageBody
                              author {
                                name
                                email
                                date
                              }
                              committedDate
                            }
                          }
                        }
                      }
                    }
                  }
                }
                  """;
        String s = stringToolkit.toolkitStringReplace(graphql, "\\$name", "111111111");
        System.out.println("s: " + s);
    }

    @Test
    public void toolkitTimeNowTest() {
        System.out.println("timeToolkit.toolkitTimeNow(): " + timeToolkit.toolkitTimeNow());
    }


}
