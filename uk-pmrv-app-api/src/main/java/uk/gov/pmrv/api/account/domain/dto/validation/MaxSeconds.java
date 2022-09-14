package uk.gov.pmrv.api.account.domain.dto.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The annotation for max seconds validation.
 */
@Constraint(validatedBy = MaxSecondsValidator.class)
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface MaxSeconds {

    String message() default "Invalid seconds";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    int seconds();
}
