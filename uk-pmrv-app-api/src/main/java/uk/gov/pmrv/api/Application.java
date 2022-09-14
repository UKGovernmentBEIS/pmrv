package uk.gov.pmrv.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.validation.ValidationAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.retry.annotation.EnableRetry;

/**
 * Used to initialize the Spring Boot application.
 */
@SpringBootApplication(exclude = ValidationAutoConfiguration.class)
@ConfigurationPropertiesScan
@EnableJpaAuditing
@EnableRetry
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
