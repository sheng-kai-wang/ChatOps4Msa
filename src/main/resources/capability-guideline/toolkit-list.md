# toolkit-list

> here are the currently available toolkit-functions
>
> all input values and return values are string type
>
> all functions (including custom-functions) can use the "assign" parameter to store the return value in a local
> variable

### invoke format (there are required parameters)

```yml
<<toolkit-function_name>>:
  - <<parameter_1>>
  - <<parameter_2>>
  - assign: <<custom_variable>>
```

### invoke format (no required parameter)

```yml
<<toolkit-function_name>>: null
```

---

### get the time from one week ago

```yml
toolkit-time-one_week_ago: null
```

### execute mathematical calculations using mathematical expressions (e.g. 2 * 3 / 4)

```yml
toolkit-number-calculate:
  - expression
```

### replace the text in a given string

```yml
toolkit-string-replace:
  - string
  - original
  - replace
```

### split the string into a list using a separator

```yml
toolkit-string-split:
  - string
  - separator
```

### determine if a string matches a specific pattern (return true or false)

```yml
toolkit-string-pattern:
  - string
  - regex
```

### get a specific element from a list

```yml
toolkit-list-get:
  - list
  - index
```

### process the elements in a list in batch

```yml
toolkit-list-foreach:
  - list
  - element_name
  - todo
```

### asynchronously process all elements in a list

```yml
toolkit-list-async:
  - list
  - element_name
  - todo
```

### parse the content of JSON using JSONPath

```yml
toolkit-json-parse:
  - json
  - jsonpath
```

### get information from the microservice-system configs

```yml
toolkit-config-get:
  - configs
  - jsonpath
```

### determine if the condition is true or false

```yml
toolkit-flow-if:
  - condition
  - true
  - false
```

### return the local variable to the outside of this custom-function, and stop this custom-function

```yml
toolkit-flow-return:
  - return
```

### execute a specific function periodically

```yml
toolkit-flow-subscribe:
  - function
  - function_parameter_json
  - cron
```

### send a GET request to a specific REST API

```yml
toolkit-restapi-get:
  - url
```

### send a POST request to a specific REST API

```yml
toolkit-restapi-post:
  - url
  - body
  - accept
  - content_type
  - authorization
```

### send a query request to a specific GraphQL API

```yml
toolkit-graphql-query:
  - url
  - graphql
  - accept
  - content_type
  - authorization
```

### execute terminal commands using Bash

```yml
toolkit-command-bash:
  - hostname
  - username
  - password
  - command
```

### visualize JSON data using Grafana

```yml
toolkit-render-grafana:
  - json
```

### send a text message to the Discord channel

```yml
toolkit-discord-text:
  - text
```

### send a notification message to the Discord channel

```yml
toolkit-discord-notify:
  - text
```

### send the embed message to the Discord channel (thumbnail is optional)

```yml
toolkit-discord-embed:
  - title
  - color
  - field_json
  - thumbnail
```
