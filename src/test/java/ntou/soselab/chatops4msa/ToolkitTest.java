package ntou.soselab.chatops4msa;

import ntou.soselab.chatops4msa.Service.ToolkitFunctionService.StringToolkit;
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

    @Autowired
    public ToolkitTest(StringToolkit stringToolkit) {
        this.stringToolkit = stringToolkit;
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
    public void gitHubTest() {

        String GITHUB_API_URL = "https://api.github.com";
        String ACCESS_TOKEN = null;
        String OWNER = "sheng-kai-wang";
        String REPO = "ChatOps4Msa-Sample-Bookinfo";

        OkHttpClient client = new OkHttpClient();

        String apiUrl = GITHUB_API_URL + "/repos/" + OWNER + "/" + REPO + "/events";

        Request request = new Request.Builder()
                .url(apiUrl)
                .header("Authorization", "Bearer " + ACCESS_TOKEN)
                .build();

        try {
            Response response = client.newCall(request).execute();

            if (response.isSuccessful()) {
                String jsonData = response.body().string();

                System.out.println(jsonData);
            } else {
                System.out.println("Request failed with code: " + response.code());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
