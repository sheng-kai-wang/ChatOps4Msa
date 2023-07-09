# toolkit-list

> Here are the currently available toolkit-functions

1. All input values and return values are string type.
2. Except for `toolkit-flow-return`, all functions (including custom-functions) can use the "assign" parameter to store the return value in a local variable.
3. ${custom_variable_name} can get the value of the local variable named custom_variable_name.

### invoke format

##### parameter required 

```yml
<<toolkit-function_name>>:
  <<parameter_name_1>>: <<parameter_value_1>>
  <<parameter_name_2>>: <<parameter_value_2>>
```

##### parameter required (assign the result value to a customized local variable)

```yml
<<toolkit-function_name>>:
  <<parameter_name_1>>: <<parameter_value_1>>
  <<parameter_name_2>>: <<parameter_value_2>>
  assign: <<custom_variable_name>>
```

##### parameter no required

```yml
<<toolkit-function_name>>: null
```


### get the time from one week ago
##### the result is formatted as `Sat Jun 10 13:59:34 CST 2023`

```yml
toolkit-time-one_week_ago: null
```

### get the time from one week ago
##### the result is formatted as `Sat Jun 10 13:59:34 CST 2023`

```yml
toolkit-time-now: null
```

### execute mathematical calculations using mathematical expressions
##### the result is `1.5`

```yml
toolkit-math-calculate:
  expression: <<e.g. 2 * 3 / 4>>
```

### replace the text in a given string
##### the result is `hello world2`

```yml
toolkit-string-replace:
  string: <<e.g. hello world>>
  original: <<e.g. world>>
  replace: <<e.g. world2>>
```

### split the string into a list using a separator
##### the result is a list like `["https:", "", "github", "com", "sheng-kai-wang", "ChatOps4Msa-Sample-Bookinfo", "git"]`

```yml
toolkit-string-split:
  string: <<e.g. https://github.com/sheng-kai-wang/ChatOps4Msa-Sample-Bookinfo.git>>
  separator: <<e.g. use / and . like /|\\. >>
```

### convert list json to string (must be single element)
##### from `["element"]` to `element`

```yml
toolkit-string-to_string:
  json: <<json>>
```

### determine if a string matches a specific pattern (return true or false)
##### the result is `true`

```yml
toolkit-string-pattern:
  string: <<e.g. 6>>
  regex: <<e.g. ^(?!(?:[1-9]|10)$)\d+$ >>
```

### get a specific element from a list
##### the result is `ChatOps4Msa-Sample-Bookinfo`

```yml
toolkit-list-get:
  list: <<e.g. ["https:", "", "github", "com", "sheng-kai-wang", "ChatOps4Msa-Sample-Bookinfo", "git"] >>
  index: <<e.g. 5>>
```

### process the elements in a list in batch
##### you can use ${i} to get the index of the element

```yml
toolkit-list-foreach:
  list: <<a parameter list>>
  element_name: <<a parameter alias>>
  todo: <<a list of functions to execute>>

# example
toolkit-list-foreach:
  list: ["service_1", "service_2", "service_3"]
  element_name: service_name
  todo:
    - get-github-service_past_week_team_activity:
        service_name: ${service_name}
```

<!-- todo: for loop by index (get the element from multiple list) -->

### asynchronously process all elements in a list
##### you can use ${i} to get the index of the element
##### using Docker technology, concurrently execute the same operation on multiple services

```yml
toolkit-list-async:
  list: <<a parameter list>>
  element_name: <<a parameter alias>>
  todo: <<a list of functions to be executed asynchronously>>

# example
toolkit-list-async:
  list: ["service_1", "service_2", "service_3"]
  element_name: service_name
  todo:
    - test-k6-stress_testing:
        service_name: ${service_name}
```

### parse the content of JSON using JSONPath
##### the result is a list like `["Alert 1", "Alert 2", "Alert 3"]`

```yml
toolkit-json-parse:
  json: <<the JSON to be parsed>>
  jsonpath: <<jsonpath>>

# example
toolkit-json-parse:
  json: {"data": {"alerts": [{"alertname": "Alert 1"}, {"alertname": "Alert 2"}, {"alertname": "Alert 3"}]}}
  jsonpath: $.data.alerts[*].alertname
```

### parse the content of JSON for GitHub commits
##### the result is a list

```yml
toolkit-json-parse_github_commit:
  json: <<the JSON to be parsed>>
```

### parse the content of JSON for GitHub issues
##### the result is a list

```yml
toolkit-json-parse_github_issue:
  json: <<the JSON to be parsed>>
```

### get information from the microservice-system configs
##### the result is `https://github.com/sheng-kai-wang/ChatOps4Msa-Sample-Bookinfo.git`

```yml
toolkit-info-get:
  system: <<e.g. bookinfo>>
  service: <<e.g. productpage, reviews, details, ratings or all_service>>
  info: <<e.g. name, url, repository, description or custom property name>>
```

### determine if the condition is true or false
##### you can omit the "true" or "false" parameter:
If the "false" parameter is omitted, it means that when the condition is false, other functions within the body will continue to be executed. The same principle applies when the "true" parameter is omitted.

```yml
toolkit-flow-if:
  condition: <<true or false>>
  true: <<a list of functions to execute>>
  false: <<a list of functions to execute>>

# example
toolkit-flow-if:
  condition: true
  true:
    - toolkit-discord-warning:
        text: "[WARNING] ......"
  false:
    - toolkit-flow-return:
        return: null
```

### return the local variable to the outside of this custom-function, and stop this custom-function

```yml
toolkit-flow-return:
  return: <<return value>>
```

<!-- ### execute a specific function periodically

```yml
toolkit-flow-subscribe:
  function: <<function name>>
  function_parameter_json: <<a JSON-formatted list of parameters>>
  cron: <<e.g. 0 9 * * 1 >>
``` -->

### send a GET request to a specific REST API

```yml
toolkit-restapi-get:
  url: <<http url>>
```

### send a POST request to a specific REST API

```yml
toolkit-restapi-post:
  url: <<http_url>>
  body: <<a JSON-formatted request body>>
  authorization: <<e.g. Bearer ... >>
```

### send a query request to a specific GraphQL API

```yml
toolkit-graphql-query:
  url: <<http_url>>
  graphql: <<graphql>>
  authorization: <<e.g. Bearer ... >>
```

### execute terminal commands using Bash
##### this is `asynchronous`

```yml
toolkit-command-bash:
  command: <<command>>
  input_stream: <<input_stream>>

# example
toolkit-command-bash:
  command: docker run --rm -i grafana/k6 run - -e TEST_URL=https://test-api.k6.io
  input_stream: /path/to/script.js
```

<!-- ### visualize JSON data using Grafana

```yml
toolkit-render-grafana:
  json: <<JSON>>
``` -->

### send a text message to the Discord channel

```yml
toolkit-discord-text:
  text: <<text>>
```

### send a INFO message to the Discord channel

```yml
toolkit-discord-info:
  text: <<text>>
```

### send a WARNING message to the Discord channel

```yml
toolkit-discord-warning:
  text: <<text>>
```

### send a ERROR message to the Discord channel

```yml
toolkit-discord-error:
  text: <<text>>
```

### send a blocks message to the Discord channel

```yml
toolkit-discord-blocks:
  text: <<text>>
```

### send a JSON message to the Discord channel

```yml
toolkit-discord-json:
  json: <<json>>
```

### send the embed message to the Discord channel (no thumbnail)
##### the color can be green, orange, red or default(gray)

```yml
toolkit-discord-embed:
  title: <<embed title>>
  color: <<embed color>>
  field_json: <<embed content in JSON format>>
```

### send the embed message to the Discord channel (with thumbnail)
##### the color can be green, orange, red or default(gray)

```yml
toolkit-discord-embed_thumbnail:
  title: <<embed title>>
  color: <<embed color>>
  field_json: <<embed content in JSON format>>
  thumbnail: <<thumbnail url>>
```

### send the embed message to the Discord channel (with image)
##### the color can be green, orange, red or default(gray)

```yml
toolkit-discord-embed_image:
  title: <<embed title>>
  color: <<embed color>>
  field_json: <<embed content in JSON format>>
  thumbnail: <<image url>>
```