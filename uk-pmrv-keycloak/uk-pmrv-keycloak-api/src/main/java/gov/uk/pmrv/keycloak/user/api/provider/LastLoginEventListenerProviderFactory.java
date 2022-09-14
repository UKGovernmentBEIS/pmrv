package gov.uk.pmrv.keycloak.user.api.provider;

import lombok.extern.jbosslog.JBossLog;
import org.keycloak.Config;
import org.keycloak.events.EventListenerProviderFactory;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;

@JBossLog
public class LastLoginEventListenerProviderFactory implements EventListenerProviderFactory {

    private static final String ID = "last-login-listener";

    @Override
    public LastLoginEventListenerProvider create(KeycloakSession keycloakSession) {
        return new LastLoginEventListenerProvider(keycloakSession);
    }

    @Override
    public void init(Config.Scope config) {
        log.infof("init config={}", config);
    }

    @Override
    public void postInit(KeycloakSessionFactory factory) {
        log.infof("postInit factory={}", factory);
    }

    @Override
    public void close() {
        log.infof("close");
    }

    @Override
    public String getId() {
        return ID;
    }

}