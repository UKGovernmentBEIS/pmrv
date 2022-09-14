# UK PMRV API application

The UK PMRV API is a Java(SpringBoot) application.

## Structure

## Running the application

You can run the Spring Boot application by typing:

    $ mvn clean spring-boot:run
	
or

    $ ./_runme.sh

You can then access the final jar file that contains both the API and the
UI code from here :

    uk-pmrv-app-api\target

## REST API Documentation

The API is documented using Swagger 3.

After running the application, the documentation is available here:

- http://localhost:8080/api/swagger-ui/index.html (UI)
- http://localhost:8080/api/v2/api-docs (JSON)

## Development

## Securing with Keycloak

Prior to running the application for the first time you need to create a client in <span>Keycloak</span>.

The following steps shows how to create the client required:

- Open the <span>Keycloak</span> admin console
- Make sure you have selected the correct realm from the upper left corner.
- Select `Clients` from the menu
- Click `Create`
- Add the following values:
- Client ID: You choose (for example `uk-ets-registry-api`)
- Client Protocol: `openid-connect`
- Click `Save`

Once saved you need to change the `Access Type` to `bearer-only` and click save.
Next you need to add the roles (e.g. user,admin,representative) in the realm.

- Open the <span>Keycloak</span> admin console
- Open the client you have created in the previous step
- Move to the `Roles` tab.
- Click the `Add Role` button in the right.
- Add the `Role Name` & click `Save`.

Last you should assign this role (e.g. `user`) in the users you want to give authorization to access the REST endpoint.

- Click the `Users` link in the `Manage` section
- Click on th `ID` of the user you want to authorize and then click on the `Role Mappings` tab.
- In the `Realm Roles` pane the newly added role must appear in the `Available Roles`.
- Select the role and press the `Add selected>>` button.

**NOTE:**You may run the application with **security disabled** using the Spring 'no-keycloak' profile.

## Enable Authentication through Swagger UI

Due to keycloak same-origin security policy CORS, you have to configure the web origin of incoming authentication requests through 8080 port.

The following steps are needed:

- Open the <span>Keycloak</span> admin console
- Make sure you have selected the correct realm from the upper left corner (`Uk-pmrv`).
- Select `Clients` from the menu
- Choose relevant Client ID that Swagger uses for authentication (e.g. `admin-cli`)
- On `Settings` tab add to `Web Origins` field the value `http://localhost:8080`
- Click `Save`

### Code quality

In order to enforce code quality the following plugins are used when building:

- Checkstyle using google_checks.xml (you can use this configuration
  from inside your IDE)
- PMD with the default configuration
- SpotBugs with the default configuration

If an issue is found then the build is failed.

#### Some tips on code quality

- The REST controllers need to be declared in SpotBugs, in file `spotbugs-security-exclude.xml`, as per the example below:

```
<Match>
    <Class name="AccountController" />
    <Bug pattern="SPRING_ENDPOINT,PREDICTABLE_RANDOM" />
</Match>
```

- Automatically generated classes need to be excluded from PMD checks, in `pom.xml`. For example, the QueryDSL generates classes which do not respect several PMD rules:

```
<plugin>
<groupId>org.apache.maven.plugins</groupId>
...
<configuration>
	...
	<excludeRoots>
		<excludeRoot>target/generated-sources/java</excludeRoot>
	</excludeRoots>
</configuration>
</plugin>
```

### How to disable Keycloak when developing back-end REST APIs

- Disable the following property in `application.properties`.

```
keycloak.enabled = false
```

### Actuator

Actuator can be accessed in:

```
http://localhost:8080/actuator
```

Note that the actuator is not secured by default because it is not meant to be
exposed to the public internet but only be accessible from the internal
network.

### Feature flags

Feature flags provide the capability to deploy unfinished features
in production. Feature flags can be specified in `application.properties`:

```
togglz.features.FOO.enabled=true
togglz.features.BAR.enabled=false
```

You can query the current status of the feature flags by accessing actuator:

```
http://localhost:8080/actuator/togglz
```

You can inject `FeatureManager` bean in your components and use code similar to
following snippet:

```
@Controller
public class MyClass {
  private FeatureManager manager;

  public MyClass(FeatureManager manager) {
      this.manager = manager;
  }

  @RequestMapping("/")
  public ResponseEntity<?> index() {
      if (manager.isActive(HELLO_WORLD)) {
           ...
      }
  }
}
```

### How to execute an AER workflow manually

```
curl --location --request POST 'http://localhost:8080/api/camunda-api/process-definition/key/PROCESS_AER_INITIATE/start' \
--header 'Content-Type: application/json' \
--data-raw '{
    "variables": {
        "accountIds": {
            "value": ["account-id-1", "account-id-2"]
        }
    }
}'

```
