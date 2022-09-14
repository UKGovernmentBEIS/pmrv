package uk.gov.pmrv.api.web.config;

import java.time.Duration;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import uk.gov.pmrv.api.common.config.property.AppProperties;

/**
 * Rest client configuration.
 */
@Configuration
public class ClientConfig {

	private final AppProperties appProperties;
	
    public ClientConfig(AppProperties appProperties) {
    	this.appProperties = appProperties;
    }

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder
            .setConnectTimeout(Duration.ofMillis(appProperties.getClient().getConnectTimeout()))
            .setReadTimeout(Duration.ofMillis(appProperties.getClient().getReadTimeout()))
            .build();
    }

}
