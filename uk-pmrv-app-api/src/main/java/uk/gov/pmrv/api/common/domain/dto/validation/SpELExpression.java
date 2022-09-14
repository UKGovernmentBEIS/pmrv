package uk.gov.pmrv.api.common.domain.dto.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = SpELExpressionValidator.class)
@Repeatable(SpELExpression.List.class)
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface SpELExpression {
    
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

    String expression();
    String message() default "SpELExpression validation error";
    
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    @interface List{
        SpELExpression[] value();
    }
}
