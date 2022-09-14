package uk.gov.pmrv.api.user.core.config;

import org.keycloak.adapters.springboot.KeycloakSpringBootProperties;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.keycloak.OAuth2Constants.CLIENT_CREDENTIALS;

/**
 * Keycloak configuration.
 */
@Configuration
public class KeycloakConfig {

	@Bean
	public Keycloak createKeycloakAdminClient(
			KeycloakSpringBootProperties keycloakProperties) {
		return KeycloakBuilder.builder()
				.grantType(CLIENT_CREDENTIALS)
				.serverUrl(keycloakProperties.getAuthServerUrl())
				.realm(keycloakProperties.getRealm())
				.clientId(keycloakProperties.getResource())
				.clientSecret((String) keycloakProperties.getCredentials().get("secret"))
				.build();
	}

}
