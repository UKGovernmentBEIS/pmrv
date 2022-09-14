package gov.uk.pmrv.keycloak.user.api.service;

import org.keycloak.models.KeycloakSession;
import org.keycloak.models.UserModel;
import org.keycloak.services.managers.AppAuthManager;
import org.keycloak.services.managers.AuthenticationManager;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.NotAuthorizedException;

public class UserSessionService {

    private final KeycloakSession session;

    public UserSessionService(KeycloakSession session) {
        this.session = session;
    }

    public UserModel getUserByEmail(String email) {
        UserModel user = session.users().getUserByEmail(session.getContext().getRealm(), email);
        if (user == null) {
            throw new BadRequestException("User email does not exist");
        }
        return user;
    }

    public UserModel getAuthenticatedUser() {
        AppAuthManager.BearerTokenAuthenticator authenticator = new AppAuthManager.BearerTokenAuthenticator(session);
        AuthenticationManager.AuthResult authResult = authenticator.authenticate();

        if (authResult == null) {
            throw new NotAuthorizedException("Unauthorized user");
        }

        return authResult.getUser();
    }
}
