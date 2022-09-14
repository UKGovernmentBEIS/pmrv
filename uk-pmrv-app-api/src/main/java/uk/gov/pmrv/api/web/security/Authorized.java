package uk.gov.pmrv.api.web.security;

import uk.gov.pmrv.api.authorization.rules.domain.AuthorizationRule;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation for authorized resources based on user's {@link uk.gov.pmrv.api.common.domain.enumeration.RoleType}
 * Triggers authorization on account/CA based on service's {@link AuthorizationRule}.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Authorized {
    /**
     * The resourceId to grant access to.
     */
    String resourceId() default "";
    String resourceSubType() default "";
}