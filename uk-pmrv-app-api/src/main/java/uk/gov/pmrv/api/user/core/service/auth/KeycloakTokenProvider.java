package uk.gov.pmrv.api.user.core.service.auth;

import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.keycloak.OAuth2Constants;
import org.keycloak.adapters.springboot.KeycloakSpringBootProperties;
import org.keycloak.admin.client.KeycloakBuilder;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class KeycloakTokenProvider {

    private final KeycloakSpringBootProperties keycloakProperties;
    
    public String grantToken() {
        return KeycloakBuilder.builder()
                .serverUrl(keycloakProperties.getAuthServerUrl())
                .grantType(OAuth2Constants.CLIENT_CREDENTIALS)
                .realm(keycloakProperties.getRealm())
                .clientId(keycloakProperties.getResource())
                .clientSecret((String) keycloakProperties.getCredentials().get("secret"))
                .resteasyClient(
                    new ResteasyClientBuilder()
                        .connectionPoolSize(10).build()
                ).build()
                .tokenManager().grantToken().getToken();
    }
}
