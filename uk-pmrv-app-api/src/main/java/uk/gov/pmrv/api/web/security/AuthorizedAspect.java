package uk.gov.pmrv.api.web.security;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import uk.gov.pmrv.api.authorization.rules.services.PmrvUserAuthorizationService;
import uk.gov.pmrv.api.common.domain.model.PmrvUser;
import uk.gov.pmrv.api.common.utils.SpELParser;

import java.lang.reflect.Method;

/**
 * Aspect triggered {@link Before} {@link AuthorizedAspect} annotated methods.
 * Retrieves:
 * <ul>
 *     <li>resourceId based on {@link AuthorizedAspect} parameters</li>
 *     <li>resourceSubType on {@link AuthorizedAspect} parameters </li>
 *     <li>serviceName the annotated method name</li>
 *     <li>{@link PmrvUser} from annotated method parameters</li>
 * </ul>
 * Calls {@link PmrvUserAuthorizationService} to evaluate authorization.
 */
@Aspect
@Component
@RequiredArgsConstructor
public class AuthorizedAspect {

    private final PmrvUserAuthorizationService pmrvUserAuthorizationService;
    private final AuthorizationAspectUserResolver authorizationAspectUserResolver;

    @Before("@annotation(uk.gov.pmrv.api.web.security.Authorized)")
    public void authorize(JoinPoint joinPoint) {
        String serviceName = getServiceName(joinPoint);
        String resourceId = getResourceId(joinPoint);
        String resourceSubType = getResourceSubType(joinPoint);
        PmrvUser pmrvUser = authorizationAspectUserResolver.getUser(joinPoint);
        
        if (!StringUtils.isEmpty(resourceSubType)) {
            pmrvUserAuthorizationService.authorize(pmrvUser, serviceName, resourceId, resourceSubType);
        } else if (!StringUtils.isEmpty(resourceId)) {
            pmrvUserAuthorizationService.authorize(pmrvUser, serviceName, resourceId);
        } else {
            pmrvUserAuthorizationService.authorize(pmrvUser, serviceName);
        }
    }

    private String getResourceId(JoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        Authorized authorized = method.getAnnotation(Authorized.class);
        return SpELParser.parseExpression(authorized.resourceId(), signature.getParameterNames(), joinPoint.getArgs(), String.class);
    }

    private String getServiceName(JoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        return signature.getMethod().getName();
    }

    private String getResourceSubType(JoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        Authorized authorized = method.getAnnotation(Authorized.class);
        return SpELParser.parseExpression(authorized.resourceSubType(), signature.getParameterNames(), joinPoint.getArgs(), String.class);
    }
}
