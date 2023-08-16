# Custom Capability

The low-code configuration files are divided into the following four types, all of which are in YAML format.

| TYPE                | DESCRIPTION                                                                                                                                                                                                                                           |
| ------------------- | ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| microservice-system | The information of microservice systems and the names of available capabilities for that system are represented by a configuration file. Each configuration file corresponds to a microservice system, and there can be multiple configuration files. |
| devops-tool         | Developers can assemble the required custom capabilities here. Each configuration file represents a DevOps tool or a custom module of multiple tools, and there can be multiple configuration files.                                                  |
| message-delivery    | Message Queue-related configures describe how received notification messages should be processed, and there can be only one configuration file for this.                                                                                              |
| secret              | Global sensitive information has only one configuration file.                                                                                                                                                                                         |

And here is the architecture diagram of the low-code configuration files.

<div><img width="700px" alt="Low Code Architecture" src="./image/Low-Code-Architecture.jpg"></div>

## microservice-system

#### Template

```yml
info:
  version: <<version_number>>
  title: <<system_name>>
  description: <<system_description>>

service:
  - name: <<service_name>>
    url: <<service_url>>
    repository: <<service_repository_url>>
    description: <<service_description>>
    <<custom_info>>: <<custom_info>>

capability:
  - <<available_capability_1>>
```

#### Example

_See [bookinfo.yml](../src/main/resources/capability/microservice-system/bookinfo.yml)_

## devops-tool

#### Template

```yml
info:
  version: <<version_number>>
  title: <<tool_name>>
  description: <<tool_description>>

low_code:
  property:
    <<property_name_1>>: <<property_value_1>>

  <<constructor_or_operation>>:
    # declare a custom-function
    - name: <<function_name>>
      # required parameters
      parameter:
        <<parameter_name_1>>: <<parameter_description_1>>
      # it will be displayed in the interface of Discord
      description: <<function_description>>
      access: <<private_or_protected_or_public>>
      # this declared function is composed of the following functions in body
      # when the declared function executed,
      # the system actually executes the following functions
      body:
        - <<function_name_1>>:
            <<parameter_name_1>>: <<parameter_value_1>>
```

#### Example

_See [k6.yml](../src/main/resources/capability/devops-tool/k6.yml)_

## message-delivery

#### Template

> The only difference from `devops-tool` is the addition of the `"on_message"` execution timing.

```yml
info:
  version: <<version_number>>
  title: <<tool_name>>
  description: <<tool_description>>

low_code:
  property:
    <<property_name_1>>: <<property_value_1>>

  <<constructor_or_operation_or_on_message>>:
    - name: <<function_name>>
      parameter:
        <<parameter_name_1>>: <<parameter_description_1>>
      description: <<function_description>>
      access: <<private_or_protected_or_public>>
      body:
        - <<function_name_1>>:
            <<parameter_name_1>>: <<parameter_value_1>>
```

#### Example

_See [message-delivery.yml](../src/main/resources/capability/message-delivery.yml)_

## secret

#### Template

```yml
secret_name: <<secret_value>>
```

#### Example

> You need to fill in all the secret values.

_See [secret_template.yml](../src/main/resources/capability/secret_template.yml)_