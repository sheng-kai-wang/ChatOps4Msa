package ntou.soselab.chatops4msa;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import ntou.soselab.chatops4msa.Exception.ToolkitFunctionException;
import ntou.soselab.chatops4msa.Service.ToolkitFunctionService.JsonToolkit;
import ntou.soselab.chatops4msa.Service.ToolkitFunctionService.StringToolkit;
import ntou.soselab.chatops4msa.Service.ToolkitFunctionService.TimeToolkit;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;

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
