package uk.gov.pmrv.api.account.domain.dto.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The annotation for grid reference digits validation.
 */
@Constraint(validatedBy = GridReferenceValidator.class)
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface GridReference {

    String message() default "Invalid grid reference";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
