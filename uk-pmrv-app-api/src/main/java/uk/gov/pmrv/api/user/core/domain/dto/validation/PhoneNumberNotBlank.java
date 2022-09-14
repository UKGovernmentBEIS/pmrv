package uk.gov.pmrv.api.user.core.domain.dto.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The annotation for PhoneNumber validation.
 */
@Constraint(validatedBy = PhoneNumberNotBlankValidator.class)
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface PhoneNumberNotBlank {

    String message() default "Enter your phone number";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
