package uk.gov.pmrv.api.web.config.security;

import org.keycloak.adapters.springsecurity.KeycloakConfiguration;
import org.keycloak.adapters.springsecurity.authentication.KeycloakAuthenticationProvider;
import org.keycloak.adapters.springsecurity.config.KeycloakWebSecurityConfigurerAdapter;
import org.keycloak.adapters.springsecurity.filter.KeycloakAuthenticationProcessingFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.authority.mapping.SimpleAuthorityMapper;
import org.springframework.security.web.authentication.session.NullAuthenticatedSessionStrategy;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import uk.gov.pmrv.api.authorization.core.service.AuthorityService;
import uk.gov.pmrv.api.authorization.core.service.UserRoleTypeService;
import uk.gov.pmrv.api.web.config.property.CorsProperties;
import uk.gov.pmrv.api.web.security.PmrvKeycloakProvider;

import java.util.Arrays;

@KeycloakConfiguration
public class SecurityConfig extends KeycloakWebSecurityConfigurerAdapter {

    private final CorsProperties corsProperties;
    private final AuthorityService authorityService;
    private final UserRoleTypeService userRoleTypeService;

    public SecurityConfig(
            CorsProperties corsProperties,
            AuthorityService authorityService,
            UserRoleTypeService userRoleTypeService) {
        this.corsProperties = corsProperties;
        this.authorityService = authorityService;
        this.userRoleTypeService = userRoleTypeService;
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) {
        KeycloakAuthenticationProvider keycloakAuthenticationProvider = keycloakAuthenticationProvider();
        keycloakAuthenticationProvider.setGrantedAuthoritiesMapper(new SimpleAuthorityMapper());
        auth.authenticationProvider(keycloakAuthenticationProvider);
    }

    @Bean
    @Override
    protected SessionAuthenticationStrategy sessionAuthenticationStrategy() {
        return new NullAuthenticatedSessionStrategy();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        super.configure(http);
        http
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .cors().configurationSource(corsConfigurationSource())
                .and().csrf().disable()
                .authorizeRequests()
                .antMatchers(
                        "/v1.0/operator-users/registration/**",
                        "/v1.0/regulator-users/registration/**",
                        "/v1.0/verifier-users/registration/**",
                        "/v1.0/users/security-setup/2fa/delete*",
                        "/v1.0/file-attachments/**",
                        "/v1.0/file-document-templates/**",
                        "/v1.0/file-documents/**",
                        "/v1.0/user-signatures/**",
                        "/v1.0/data*",
                        "/v3/api-docs",
                        "/error",
                        "/swagger-ui/**",
                        "/swagger-resources/**",
                        "/configuration/**",
                        "/webjars/**",
                        "/actuator/**",
                        "/camunda-api/**")
                .permitAll()
                .and().authorizeRequests()
                .antMatchers("/**")
                .authenticated()
                .and().headers().httpStrictTransportSecurity().includeSubDomains(true);
    }

    protected CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(corsProperties.getAllowedOrigins());
        configuration.setAllowedMethods(Arrays.asList("HEAD", "GET", "POST", "PUT", "PATCH", "DELETE"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Override
    protected KeycloakAuthenticationProvider keycloakAuthenticationProvider() {
        return new PmrvKeycloakProvider(authorityService, userRoleTypeService);
    }

    /**
     * Add FilterRegistrationBeans to security configuration to prevent the Keycloak filters from being registered twice.
     *
     * @param filter {@link KeycloakAuthenticationProcessingFilter}
     * @return {@link FilterRegistrationBean}
     */
    @Bean
    public FilterRegistrationBean<KeycloakAuthenticationProcessingFilter> keycloakAuthenticationProcessingFilterRegistrationBean(
            KeycloakAuthenticationProcessingFilter filter) {
        FilterRegistrationBean<KeycloakAuthenticationProcessingFilter> registrationBean = new FilterRegistrationBean<>(filter);
        registrationBean.setEnabled(false);
        return registrationBean;
    }
}
