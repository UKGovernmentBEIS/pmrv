package uk.gov.pmrv.api.account.domain.dto.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = LegalEntityValidator.class)
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface LegalEntity {

    String message() default "Invalid legal entity";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
