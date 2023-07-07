package ntou.soselab.chatops4msa;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import ntou.soselab.chatops4msa.Exception.ToolkitFunctionException;
import ntou.soselab.chatops4msa.Service.ToolkitFunctionService.JsonToolkit;
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
    private final JsonToolkit jsonToolkit;

    @Autowired
    public ToolkitTest(StringToolkit stringToolkit, JsonToolkit jsonToolkit) {
        this.stringToolkit = stringToolkit;
        this.jsonToolkit = jsonToolkit;
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

    @Test
    public void toolkitJsonParseTest() {
        String json = """
                                [
                  {
                    "id": "30226046787",
                    "type": "PushEvent",
                    "actor": {
                      "id": 76858274,
                      "login": "sheng-kai-wang",
                      "display_login": "sheng-kai-wang",
                      "gravatar_id": "",
                      "url": "https://api.github.com/users/sheng-kai-wang",
                      "avatar_url": "https://avatars.githubusercontent.com/u/76858274?"
                    },
                    "repo": {
                      "id": 649342002,
                      "name": "sheng-kai-wang/ChatOps4Msa-Sample-Bookinfo",
                      "url": "https://api.github.com/repos/sheng-kai-wang/ChatOps4Msa-Sample-Bookinfo"
                    },
                    "payload": {
                      "repository_id": 649342002,
                      "push_id": 14221039465,
                      "size": 1,
                      "distinct_size": 1,
                      "ref": "refs/heads/master",
                      "head": "a9a64bd6c9e50f70cabd279b74b510c4927c3e7a",
                      "before": "7bdd16e51922b333ca8b5f87b1b87c67c2da7076",
                      "commits": [
                        {
                          "sha": "a9a64bd6c9e50f70cabd279b74b510c4927c3e7a",
                          "author": {
                            "email": "nssh94879487@gmail.com",
                            "name": "sheng-kai-wang"
                          },
                          "message": "feat: github actions test",
                          "distinct": true,
                          "url": "https://api.github.com/repos/sheng-kai-wang/ChatOps4Msa-Sample-Bookinfo/commits/a9a64bd6c9e50f70cabd279b74b510c4927c3e7a"
                        }
                      ]
                    },
                    "public": true,
                    "created_at": "2023-07-06T00:39:23Z"
                  },
                  {
                    "id": "29947188505",
                    "type": "IssuesEvent",
                    "actor": {
                      "id": 76858274,
                      "login": "sheng-kai-wang",
                      "display_login": "sheng-kai-wang",
                      "gravatar_id": "",
                      "url": "https://api.github.com/users/sheng-kai-wang",
                      "avatar_url": "https://avatars.githubusercontent.com/u/76858274?"
                    },
                    "repo": {
                      "id": 649342002,
                      "name": "sheng-kai-wang/ChatOps4Msa-Sample-Bookinfo",
                      "url": "https://api.github.com/repos/sheng-kai-wang/ChatOps4Msa-Sample-Bookinfo"
                    },
                    "payload": {
                      "action": "opened",
                      "issue": {
                        "url": "https://api.github.com/repos/sheng-kai-wang/ChatOps4Msa-Sample-Bookinfo/issues/1",
                        "repository_url": "https://api.github.com/repos/sheng-kai-wang/ChatOps4Msa-Sample-Bookinfo",
                        "labels_url": "https://api.github.com/repos/sheng-kai-wang/ChatOps4Msa-Sample-Bookinfo/issues/1/labels{/name}",
                        "comments_url": "https://api.github.com/repos/sheng-kai-wang/ChatOps4Msa-Sample-Bookinfo/issues/1/comments",
                        "events_url": "https://api.github.com/repos/sheng-kai-wang/ChatOps4Msa-Sample-Bookinfo/issues/1/events",
                        "html_url": "https://github.com/sheng-kai-wang/ChatOps4Msa-Sample-Bookinfo/issues/1",
                        "id": 1770344712,
                        "node_id": "I_kwDOJrQsMs5phVEI",
                        "number": 1,
                        "title": "test",
                        "user": {
                          "login": "sheng-kai-wang",
                          "id": 76858274,
                          "node_id": "MDQ6VXNlcjc2ODU4Mjc0",
                          "avatar_url": "https://avatars.githubusercontent.com/u/76858274?v=4",
                          "gravatar_id": "",
                          "url": "https://api.github.com/users/sheng-kai-wang",
                          "html_url": "https://github.com/sheng-kai-wang",
                          "followers_url": "https://api.github.com/users/sheng-kai-wang/followers",
                          "following_url": "https://api.github.com/users/sheng-kai-wang/following{/other_user}",
                          "gists_url": "https://api.github.com/users/sheng-kai-wang/gists{/gist_id}",
                          "starred_url": "https://api.github.com/users/sheng-kai-wang/starred{/owner}{/repo}",
                          "subscriptions_url": "https://api.github.com/users/sheng-kai-wang/subscriptions",
                          "organizations_url": "https://api.github.com/users/sheng-kai-wang/orgs",
                          "repos_url": "https://api.github.com/users/sheng-kai-wang/repos",
                          "events_url": "https://api.github.com/users/sheng-kai-wang/events{/privacy}",
                          "received_events_url": "https://api.github.com/users/sheng-kai-wang/received_events",
                          "type": "User",
                          "site_admin": false
                        },
                        "labels": [

                        ],
                        "state": "open",
                        "locked": false,
                        "assignee": null,
                        "assignees": [

                        ],
                        "milestone": null,
                        "comments": 0,
                        "created_at": "2023-06-22T20:24:33Z",
                        "updated_at": "2023-06-22T20:24:33Z",
                        "closed_at": null,
                        "author_association": "OWNER",
                        "active_lock_reason": null,
                        "body": "test1",
                        "reactions": {
                          "url": "https://api.github.com/repos/sheng-kai-wang/ChatOps4Msa-Sample-Bookinfo/issues/1/reactions",
                          "total_count": 0,
                          "+1": 0,
                          "-1": 0,
                          "laugh": 0,
                          "hooray": 0,
                          "confused": 0,
                          "heart": 0,
                          "rocket": 0,
                          "eyes": 0
                        },
                        "timeline_url": "https://api.github.com/repos/sheng-kai-wang/ChatOps4Msa-Sample-Bookinfo/issues/1/timeline",
                        "performed_via_github_app": null,
                        "state_reason": null
                      }
                    },
                    "public": true,
                    "created_at": "2023-06-22T20:24:35Z"
                  },
                  {
                    "id": "29548322233",
                    "type": "PushEvent",
                    "actor": {
                      "id": 76858274,
                      "login": "sheng-kai-wang",
                      "display_login": "sheng-kai-wang",
                      "gravatar_id": "",
                      "url": "https://api.github.com/users/sheng-kai-wang",
                      "avatar_url": "https://avatars.githubusercontent.com/u/76858274?"
                    },
                    "repo": {
                      "id": 649342002,
                      "name": "sheng-kai-wang/ChatOps4Msa-Sample-Bookinfo",
                      "url": "https://api.github.com/repos/sheng-kai-wang/ChatOps4Msa-Sample-Bookinfo"
                    },
                    "payload": {
                      "repository_id": 649342002,
                      "push_id": 13880053936,
                      "size": 1,
                      "distinct_size": 1,
                      "ref": "refs/heads/master",
                      "head": "7bdd16e51922b333ca8b5f87b1b87c67c2da7076",
                      "before": "023067372e8b7548e36475fc026567f887743ac4",
                      "commits": [
                        {
                          "sha": "7bdd16e51922b333ca8b5f87b1b87c67c2da7076",
                          "author": {
                            "email": "nssh94879487@gmail.com",
                            "name": "sheng-kai-wang"
                          },
                          "message": "fix: remove platforms",
                          "distinct": true,
                          "url": "https://api.github.com/repos/sheng-kai-wang/ChatOps4Msa-Sample-Bookinfo/commits/7bdd16e51922b333ca8b5f87b1b87c67c2da7076"
                        }
                      ]
                    },
                    "public": true,
                    "created_at": "2023-06-06T00:28:02Z"
                  },
                  {
                    "id": "29548065255",
                    "type": "PushEvent",
                    "actor": {
                      "id": 76858274,
                      "login": "sheng-kai-wang",
                      "display_login": "sheng-kai-wang",
                      "gravatar_id": "",
                      "url": "https://api.github.com/users/sheng-kai-wang",
                      "avatar_url": "https://avatars.githubusercontent.com/u/76858274?"
                    },
                    "repo": {
                      "id": 649342002,
                      "name": "sheng-kai-wang/ChatOps4Msa-Sample-Bookinfo",
                      "url": "https://api.github.com/repos/sheng-kai-wang/ChatOps4Msa-Sample-Bookinfo"
                    },
                    "payload": {
                      "repository_id": 649342002,
                      "push_id": 13879927831,
                      "size": 1,
                      "distinct_size": 1,
                      "ref": "refs/heads/master",
                      "head": "023067372e8b7548e36475fc026567f887743ac4",
                      "before": "7c48819e5a18631c7a8fc70d40c769dc7bf1a038",
                      "commits": [
                        {
                          "sha": "023067372e8b7548e36475fc026567f887743ac4",
                          "author": {
                            "email": "nssh94879487@gmail.com",
                            "name": "sheng-kai-wang"
                          },
                          "message": "chore: add args and platforms to the docker-compose.yml",
                          "distinct": true,
                          "url": "https://api.github.com/repos/sheng-kai-wang/ChatOps4Msa-Sample-Bookinfo/commits/023067372e8b7548e36475fc026567f887743ac4"
                        }
                      ]
                    },
                    "public": true,
                    "created_at": "2023-06-06T00:05:19Z"
                  },
                  {
                    "id": "29520128737",
                    "type": "PushEvent",
                    "actor": {
                      "id": 76858274,
                      "login": "sheng-kai-wang",
                      "display_login": "sheng-kai-wang",
                      "gravatar_id": "",
                      "url": "https://api.github.com/users/sheng-kai-wang",
                      "avatar_url": "https://avatars.githubusercontent.com/u/76858274?"
                    },
                    "repo": {
                      "id": 649342002,
                      "name": "sheng-kai-wang/ChatOps4Msa-Sample-Bookinfo",
                      "url": "https://api.github.com/repos/sheng-kai-wang/ChatOps4Msa-Sample-Bookinfo"
                    },
                    "payload": {
                      "repository_id": 649342002,
                      "push_id": 13866531120,
                      "size": 1,
                      "distinct_size": 1,
                      "ref": "refs/heads/master",
                      "head": "7c48819e5a18631c7a8fc70d40c769dc7bf1a038",
                      "before": "424d64081b695bc5b047578b1617d9f6a457da77",
                      "commits": [
                        {
                          "sha": "7c48819e5a18631c7a8fc70d40c769dc7bf1a038",
                          "author": {
                            "email": "nssh94879487@gmail.com",
                            "name": "sheng-kai-wang"
                          },
                          "message": "chore: link service to db",
                          "distinct": true,
                          "url": "https://api.github.com/repos/sheng-kai-wang/ChatOps4Msa-Sample-Bookinfo/commits/7c48819e5a18631c7a8fc70d40c769dc7bf1a038"
                        }
                      ]
                    },
                    "public": true,
                    "created_at": "2023-06-05T00:08:08Z"
                  },
                  {
                    "id": "29519666790",
                    "type": "PushEvent",
                    "actor": {
                      "id": 76858274,
                      "login": "sheng-kai-wang",
                      "display_login": "sheng-kai-wang",
                      "gravatar_id": "",
                      "url": "https://api.github.com/users/sheng-kai-wang",
                      "avatar_url": "https://avatars.githubusercontent.com/u/76858274?"
                    },
                    "repo": {
                      "id": 649342002,
                      "name": "sheng-kai-wang/ChatOps4Msa-Sample-Bookinfo",
                      "url": "https://api.github.com/repos/sheng-kai-wang/ChatOps4Msa-Sample-Bookinfo"
                    },
                    "payload": {
                      "repository_id": 649342002,
                      "push_id": 13866247334,
                      "size": 1,
                      "distinct_size": 1,
                      "ref": "refs/heads/master",
                      "head": "424d64081b695bc5b047578b1617d9f6a457da77",
                      "before": "e88e0ac370e0253bf64eb7ece085ead4ced74cf8",
                      "commits": [
                        {
                          "sha": "424d64081b695bc5b047578b1617d9f6a457da77",
                          "author": {
                            "email": "nssh94879487@gmail.com",
                            "name": "sheng-kai-wang"
                          },
                          "message": "chore: deploy the mongodb and the mysql",
                          "distinct": true,
                          "url": "https://api.github.com/repos/sheng-kai-wang/ChatOps4Msa-Sample-Bookinfo/commits/424d64081b695bc5b047578b1617d9f6a457da77"
                        }
                      ]
                    },
                    "public": true,
                    "created_at": "2023-06-04T22:45:54Z"
                  },
                  {
                    "id": "29519624965",
                    "type": "PushEvent",
                    "actor": {
                      "id": 76858274,
                      "login": "sheng-kai-wang",
                      "display_login": "sheng-kai-wang",
                      "gravatar_id": "",
                      "url": "https://api.github.com/users/sheng-kai-wang",
                      "avatar_url": "https://avatars.githubusercontent.com/u/76858274?"
                    },
                    "repo": {
                      "id": 649342002,
                      "name": "sheng-kai-wang/ChatOps4Msa-Sample-Bookinfo",
                      "url": "https://api.github.com/repos/sheng-kai-wang/ChatOps4Msa-Sample-Bookinfo"
                    },
                    "payload": {
                      "repository_id": 649342002,
                      "push_id": 13866220328,
                      "size": 1,
                      "distinct_size": 1,
                      "ref": "refs/heads/master",
                      "head": "e88e0ac370e0253bf64eb7ece085ead4ced74cf8",
                      "before": "fc60df490bfa5b098a548be08a8c32c656002479",
                      "commits": [
                        {
                          "sha": "e88e0ac370e0253bf64eb7ece085ead4ced74cf8",
                          "author": {
                            "email": "nssh94879487@gmail.com",
                            "name": "sheng-kai-wang"
                          },
                          "message": "fix: relative path of docker-compose file",
                          "distinct": true,
                          "url": "https://api.github.com/repos/sheng-kai-wang/ChatOps4Msa-Sample-Bookinfo/commits/e88e0ac370e0253bf64eb7ece085ead4ced74cf8"
                        }
                      ]
                    },
                    "public": true,
                    "created_at": "2023-06-04T22:37:31Z"
                  },
                  {
                    "id": "29518448758",
                    "type": "PushEvent",
                    "actor": {
                      "id": 76858274,
                      "login": "sheng-kai-wang",
                      "display_login": "sheng-kai-wang",
                      "gravatar_id": "",
                      "url": "https://api.github.com/users/sheng-kai-wang",
                      "avatar_url": "https://avatars.githubusercontent.com/u/76858274?"
                    },
                    "repo": {
                      "id": 649342002,
                      "name": "sheng-kai-wang/ChatOps4Msa-Sample-Bookinfo",
                      "url": "https://api.github.com/repos/sheng-kai-wang/ChatOps4Msa-Sample-Bookinfo"
                    },
                    "payload": {
                      "repository_id": 649342002,
                      "push_id": 13865448464,
                      "size": 1,
                      "distinct_size": 1,
                      "ref": "refs/heads/master",
                      "head": "fc60df490bfa5b098a548be08a8c32c656002479",
                      "before": "1dcf5a2ef4fef1af100df3d8bdc3c93c6a7084e0",
                      "commits": [
                        {
                          "sha": "fc60df490bfa5b098a548be08a8c32c656002479",
                          "author": {
                            "email": "nssh94879487@gmail.com",
                            "name": "sheng-kai-wang"
                          },
                          "message": "fix: relative path",
                          "distinct": true,
                          "url": "https://api.github.com/repos/sheng-kai-wang/ChatOps4Msa-Sample-Bookinfo/commits/fc60df490bfa5b098a548be08a8c32c656002479"
                        }
                      ]
                    },
                    "public": true,
                    "created_at": "2023-06-04T19:22:04Z"
                  },
                  {
                    "id": "29518394140",
                    "type": "PushEvent",
                    "actor": {
                      "id": 76858274,
                      "login": "sheng-kai-wang",
                      "display_login": "sheng-kai-wang",
                      "gravatar_id": "",
                      "url": "https://api.github.com/users/sheng-kai-wang",
                      "avatar_url": "https://avatars.githubusercontent.com/u/76858274?"
                    },
                    "repo": {
                      "id": 649342002,
                      "name": "sheng-kai-wang/ChatOps4Msa-Sample-Bookinfo",
                      "url": "https://api.github.com/repos/sheng-kai-wang/ChatOps4Msa-Sample-Bookinfo"
                    },
                    "payload": {
                      "repository_id": 649342002,
                      "push_id": 13865413519,
                      "size": 1,
                      "distinct_size": 1,
                      "ref": "refs/heads/master",
                      "head": "1dcf5a2ef4fef1af100df3d8bdc3c93c6a7084e0",
                      "before": "51dda022694ab52436808401149715bb004f4ba3",
                      "commits": [
                        {
                          "sha": "1dcf5a2ef4fef1af100df3d8bdc3c93c6a7084e0",
                          "author": {
                            "email": "nssh94879487@gmail.com",
                            "name": "sheng-kai-wang"
                          },
                          "message": "chore: create docker-compose.yaml",
                          "distinct": true,
                          "url": "https://api.github.com/repos/sheng-kai-wang/ChatOps4Msa-Sample-Bookinfo/commits/1dcf5a2ef4fef1af100df3d8bdc3c93c6a7084e0"
                        }
                      ]
                    },
                    "public": true,
                    "created_at": "2023-06-04T19:13:49Z"
                  },
                  {
                    "id": "29517202215",
                    "type": "PushEvent",
                    "actor": {
                      "id": 76858274,
                      "login": "sheng-kai-wang",
                      "display_login": "sheng-kai-wang",
                      "gravatar_id": "",
                      "url": "https://api.github.com/users/sheng-kai-wang",
                      "avatar_url": "https://avatars.githubusercontent.com/u/76858274?"
                    },
                    "repo": {
                      "id": 649342002,
                      "name": "sheng-kai-wang/ChatOps4Msa-Sample-Bookinfo",
                      "url": "https://api.github.com/repos/sheng-kai-wang/ChatOps4Msa-Sample-Bookinfo"
                    },
                    "payload": {
                      "repository_id": 649342002,
                      "push_id": 13864644500,
                      "size": 1,
                      "distinct_size": 1,
                      "ref": "refs/heads/master",
                      "head": "51dda022694ab52436808401149715bb004f4ba3",
                      "before": "91a9c263d4ed400042f9857bc24b6fbe896fd36b",
                      "commits": [
                        {
                          "sha": "51dda022694ab52436808401149715bb004f4ba3",
                          "author": {
                            "email": "nssh94879487@gmail.com",
                            "name": "sheng-kai-wang"
                          },
                          "message": "feat: keep only the bookinfo sample",
                          "distinct": true,
                          "url": "https://api.github.com/repos/sheng-kai-wang/ChatOps4Msa-Sample-Bookinfo/commits/51dda022694ab52436808401149715bb004f4ba3"
                        }
                      ]
                    },
                    "public": true,
                    "created_at": "2023-06-04T16:24:01Z"
                  }
                ]
                                """;

        try {
            String s = jsonToolkit.toolkitJsonParseGithubEmbed(json, "5");
        } catch (ToolkitFunctionException e) {
            throw new RuntimeException(e);
        }
//        DocumentContext jsonContext = JsonPath.parse(json);
//        Object o = jsonContext.read("$[*].{author: $.payload.commits[0].author.name, message: $.payload.commits[0].message, url: $.payload.commits[0].url, created_at: $.created_at}");
//        System.out.println("o: " + o);
    }
}
