package uk.pmrv.app.admin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.validation.ValidationAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

/**
 * Used to initialize the Spring Boot application.
 */
//https://github.com/camunda-consulting/camunda-7-code-examples/tree/master/snippets/springboot-keycloak-sso/spring-security-and-springboot-adapter
//https://github.com/camunda-community-hub/camunda-platform-7-keycloak/tree/master/examples/sso-kubernetes
@SpringBootApplication(exclude = ValidationAutoConfiguration.class)
@ConfigurationPropertiesScan
public class Application {
    /**
     * Main method to initialize the Spring Boot application.
     *
     * @param args The command line arguments
     */
    public static void main(final String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
