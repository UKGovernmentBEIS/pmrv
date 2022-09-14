package gov.uk.pmrv.keycloak.user.api.provider;

import gov.uk.pmrv.keycloak.user.api.controller.UserController;
import gov.uk.pmrv.keycloak.user.api.repository.UserDetailsRepository;
import gov.uk.pmrv.keycloak.user.api.repository.UserEntityRepository;
import gov.uk.pmrv.keycloak.user.api.transform.UserDetailsMapper;
import gov.uk.pmrv.keycloak.user.api.transform.UserMapper;
import gov.uk.pmrv.keycloak.user.api.service.UserOtpService;
import gov.uk.pmrv.keycloak.user.api.service.UserDetailsService;
import gov.uk.pmrv.keycloak.user.api.service.UserEntityService;
import gov.uk.pmrv.keycloak.user.api.service.UserSessionService;
import javax.persistence.EntityManager;
import org.keycloak.connections.jpa.JpaConnectionProvider;
import org.keycloak.models.KeycloakSession;
import org.keycloak.services.resource.RealmResourceProvider;

public class UserResourceProvider implements RealmResourceProvider {

    private final UserController userController;

    public UserResourceProvider(KeycloakSession session) {
        EntityManager entityManager = session.getProvider(JpaConnectionProvider.class).getEntityManager();
        UserEntityRepository userEntityRepository = new UserEntityRepository(entityManager);
        UserDetailsRepository userDetailsRepository = new UserDetailsRepository(entityManager);

        UserSessionService userSessionService = new UserSessionService(session);
        UserEntityService userEntityService = new UserEntityService(userEntityRepository, userSessionService, new UserMapper());
        UserDetailsService userDetailsService = new UserDetailsService(userDetailsRepository, userSessionService, new UserDetailsMapper());
        UserOtpService userOtpService = new UserOtpService(session, userSessionService);

        userController = new UserController(userEntityService, userDetailsService, userOtpService);
    }

    @Override
    public Object getResource() {
        return userController;
    }

    @Override
    public void close() {
    }

}
