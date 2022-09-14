# UK PMRV ADMIN application

The UK PMRV ADMIN is a Java(SpringBoot) application for managing the bpmn engine (Camunda) of PMRV.

## Structure

## Running the application

You can run the Spring Boot application by typing:

    $ mvn clean spring-boot:run

## REST API Documentation

Camunda rest is unauthenticated and can be used to view/edit camunda processes.
- https://docs.camunda.org/manual/latest/reference/rest/

## WEB APP Documentation

Camunda webapp is authenticated through keycloak in master realm.
One should update Valid Redirect URIs in Camunda-identity-service client in keycloak based on the ip:port that pmrv-app-admin is deployed.

- https://camunda.com/platform-7/cockpit/
- https://camunda.com/platform/tasklist/
- https://github.com/camunda/camunda-bpm-platform/tree/master/webapps

