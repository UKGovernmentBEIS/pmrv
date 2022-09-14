package uk.gov.pmrv.api.user.core.domain.dto.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The annotation for country code validation.
 */
@Constraint(validatedBy = CountryCodeValidator.class)
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface CountryCode {

    String message() default "Invalid country code";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
