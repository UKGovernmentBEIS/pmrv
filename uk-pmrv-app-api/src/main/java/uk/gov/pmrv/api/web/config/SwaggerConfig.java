package uk.gov.pmrv.api.web.config;

import com.fasterxml.classmate.TypeResolver;
import io.swagger.annotations.ApiOperation;
import java.time.LocalDateTime;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.info.BuildProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.servlet.mvc.method.RequestMappingInfoHandlerMapping;
import springfox.documentation.builders.OAuthBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.schema.AlternateTypeRules;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.AuthorizationScope;
import springfox.documentation.service.GrantType;
import springfox.documentation.service.ResourceOwnerPasswordCredentialsGrant;
import springfox.documentation.service.SecurityReference;
import springfox.documentation.service.SecurityScheme;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.spring.web.plugins.WebMvcRequestHandlerProvider;
import springfox.documentation.swagger.web.SecurityConfiguration;
import springfox.documentation.swagger.web.SecurityConfigurationBuilder;
import uk.gov.pmrv.api.common.domain.model.PmrvUser;
import uk.gov.pmrv.api.web.constants.SwaggerApiInfo;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.keycloak.OAuth2Constants.CLIENT_ID;
import static org.keycloak.OAuth2Constants.CLIENT_SECRET;

/**
 * Configuration for REST API documentation.
 */
@Configuration
public class SwaggerConfig {

    /** The keycloak authentication server. */
    @Value("${keycloak.auth-server-url}")
    private String authServer;

    /** The keycloak realm. */
    @Value("${keycloak.realm}")
    private String realm;

    /** The {@link BuildProperties}. */
    private final BuildProperties buildProperties;

    /**
     * The {@link SwaggerConfig} constructor.
     *
     * @param buildProperties {@link BuildProperties}
     */
    public SwaggerConfig(BuildProperties buildProperties) {
        this.buildProperties = buildProperties;
    }

    /**
     * Configures the documentation.
     *
     * @return {@link Docket}.
     */
    @Bean
    public Docket configure(TypeResolver typeResolver) {
        return new Docket(DocumentationType.OAS_30)
                .select()
                .apis(RequestHandlerSelectors.basePackage("uk.gov.pmrv.api"))
                .paths(PathSelectors.any())
                .build().apiInfo(apiInfo())
                .securitySchemes(Collections.singletonList(securityScheme()))
                .securityContexts(Collections.singletonList(securityContext()))
                .useDefaultResponseMessages(false)
                .alternateTypeRules(
                    AlternateTypeRules.newRule(
                        typeResolver.resolve(List.class, LocalDateTime.class),
                        typeResolver.resolve(List.class, String.class)
                    )
                )
                .ignoredParameterTypes(PmrvUser.class);
    }

    /**
     * Swagger security configuration.
     *
     * @return {@link SecurityConfiguration}.
     */
    @Bean
    public SecurityConfiguration security() {
        return SecurityConfigurationBuilder.builder()
                .clientId(CLIENT_ID)
                .clientSecret(CLIENT_SECRET)
                .realm(realm)
                .useBasicAuthenticationWithAccessCodeGrant(false)
                .build();
    }

    /**
     * Provides API meta-data information.
     *
     * @return {@link ApiInfo}.
     */
    private ApiInfo apiInfo() {
        return new ApiInfo(
                "PMRV API Documentation",
                "Back-end REST API documentation for the UK PMRV application",
                String.format("%s %s", buildProperties.getName(), buildProperties.getVersion()),
                "",
                ApiInfo.DEFAULT_CONTACT,
                null, null, Collections.emptyList());
    }

    /**
     * Provides the Oauth security scheme.
     *
     * @return {@link SecurityScheme}.
     */
    private SecurityScheme securityScheme() {
        GrantType grantType = new ResourceOwnerPasswordCredentialsGrant(authServer + "/realms/" + realm + "/protocol/openid-connect/token");

        return new OAuthBuilder()
                .name("pmrv_oauth")
                .grantTypes(Collections.singletonList(grantType))
                .build();
    }

    /**
     * Provides the API security context needed for specific requests.
     *
     * @return {@link SecurityContext}.
     */
    private SecurityContext securityContext() {
        return SecurityContext.builder()
                .securityReferences(Collections.singletonList(new SecurityReference("pmrv_oauth", new AuthorizationScope[]{})))
                .operationSelector(operationContext -> operationContext.findAnnotation(ApiOperation.class).isPresent()
                    && Arrays.asList(operationContext.findAnnotation(ApiOperation.class).get().tags()).contains(SwaggerApiInfo.TAG_AUTHENTICATED))
                .build();
    }

    /*springfox is not compatible with spring-boot 2.6, so this is a workaround
    https://github.com/springfox/springfox/issues/3462
    https://github.com/spring-projects/spring-boot/wiki/Spring-Boot-2.6-Release-Notes*/
    @Bean
    public static BeanPostProcessor springfoxHandlerProviderBeanPostProcessor() {
        return new BeanPostProcessor() {

            @Override
            public Object postProcessAfterInitialization(Object bean, String beanName)
                    throws BeansException {
                if (bean instanceof WebMvcRequestHandlerProvider) {
                    customizeSpringfoxHandlerMappings(getHandlerMappings(bean));
                }
                return bean;
            }

            private <T extends RequestMappingInfoHandlerMapping> void customizeSpringfoxHandlerMappings(List<T> mappings) {
                List<T> copy =
                        mappings.stream()
                                .filter(mapping -> mapping.getPatternParser() == null)
                                .collect(Collectors.toList());
                mappings.clear();
                mappings.addAll(copy);
            }

            @SuppressWarnings("unchecked")
            private List<RequestMappingInfoHandlerMapping> getHandlerMappings(Object bean) {
                try {
                    Field field = ReflectionUtils.findField(bean.getClass(), "handlerMappings");
                    field.setAccessible(true);
                    return (List<RequestMappingInfoHandlerMapping>) field.get(bean);
                } catch (IllegalArgumentException | IllegalAccessException e) {
                    throw new IllegalStateException(e);
                }
            }
        };
    }
}