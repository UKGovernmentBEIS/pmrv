package uk.pmrv.app.admin.camunda;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Component
public class KeycloakLogoutHandler implements LogoutSuccessHandler {
    private final RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

    private String oauth2UserLogoutUri;

    public KeycloakLogoutHandler(@Value("${plugin.identity.keycloak.keycloakIssuerUrl:}") String oauth2UserAuthorizationUri) {
        if (!StringUtils.isEmpty(oauth2UserAuthorizationUri)) {
            this.oauth2UserLogoutUri = oauth2UserAuthorizationUri + "/protocol/openid-connect/logout";
        }
    }

    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        if (!StringUtils.isEmpty(oauth2UserLogoutUri)) {
            String requestUrl = request.getRequestURL().toString();
            String redirectUri = requestUrl.substring(0, requestUrl.indexOf("/app"));
            String logoutUrl = oauth2UserLogoutUri + "?redirect_uri=" + URLEncoder.encode(redirectUri, StandardCharsets.UTF_8);
            redirectStrategy.sendRedirect(request, response, logoutUrl);
        }
    }
}
