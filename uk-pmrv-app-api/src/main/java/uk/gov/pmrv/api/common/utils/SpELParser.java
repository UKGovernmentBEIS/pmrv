package uk.gov.pmrv.api.common.utils;

import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.expression.MapAccessor;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

@UtilityClass
public class SpELParser {
    
    public <T> T parseExpression(String expression, String[] parameterNames, Object[] parameterValues, Class<T> desiredResultType) {
        if(StringUtils.isEmpty(expression)) {
            return null;
        }
        
        ExpressionParser parser = new SpelExpressionParser();
        StandardEvaluationContext context = new StandardEvaluationContext();
        context.addPropertyAccessor(new MapAccessor());

        for (int i = 0; i < parameterNames.length; i++) {
            context.setVariable(parameterNames[i], parameterValues[i]);
        }
        return parser.parseExpression(expression).getValue(context, desiredResultType);
    }
}