package uk.gov.pmrv.api.authorization.core.domain.dto;

import uk.gov.pmrv.api.authorization.core.service.RoleCodeValidator;
import uk.gov.pmrv.api.common.domain.enumeration.RoleType;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The annotation for role code validation.
 */
@Constraint(validatedBy = RoleCodeValidator.class)
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface RoleCode {

    String message() default "Invalid role code";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    RoleType roleType();
}
