package uk.gov.pmrv.api.common.domain.dto.validation;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.fasterxml.jackson.databind.SerializationFeature;
import uk.gov.pmrv.api.common.utils.SpELParser;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.apache.commons.lang3.BooleanUtils;

import java.util.Map;

public class SpELExpressionValidator implements ConstraintValidator<SpELExpression, Object> {
    
    private ObjectMapper objectMapper;
    private String expression;
    private String message;
    
    @Override
    public void initialize(SpELExpression constraintAnnotation) {
        objectMapper = new ObjectMapper().findAndRegisterModules().disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        
        expression = constraintAnnotation.expression();
        message = constraintAnnotation.message();
    }

    @Override
    public boolean isValid(Object object, ConstraintValidatorContext context) {
        Map<String, Object> modelFields = objectMapper.convertValue(object, Map.class);
        final Boolean valid = SpELParser.parseExpression(
                expression,
                modelFields.keySet().toArray(String[]::new),
                modelFields.values().toArray(),
                Boolean.class);
        
        if(BooleanUtils.isNotTrue(valid)) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("{" + this.message + "}").addConstraintViolation();
            return false;
        } else {
            return true;    
        }
    }
}
