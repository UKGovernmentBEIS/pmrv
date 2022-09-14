package uk.gov.pmrv.api.web.orchestrator.validate;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Constraint(validatedBy = AccountOperatorAuthorityUpdateValidator.class)
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface AccountOperatorAuthorityUpdate {
    String message() default "{accountOperatorAuthorityUpdate.notEmpty}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
