package uk.gov.pmrv.api.web.security;

import java.lang.reflect.Method;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import uk.gov.pmrv.api.authorization.rules.services.RoleAuthorizationService;
import uk.gov.pmrv.api.common.domain.enumeration.RoleType;
import uk.gov.pmrv.api.common.domain.model.PmrvUser;

@Aspect
@Component
@RequiredArgsConstructor
public class AuthorizedRoleAspect {

    private final RoleAuthorizationService roleAuthorizationService;
    private final AuthorizationAspectUserResolver authorizationAspectUserResolver;

    @Before("@annotation(uk.gov.pmrv.api.web.security.AuthorizedRole)")
    public void authorize(JoinPoint joinPoint) {
        RoleType[] roleTypes = getRoleTypes(joinPoint);
        PmrvUser pmrvUser = authorizationAspectUserResolver.getUser(joinPoint);
        roleAuthorizationService.evaluate(pmrvUser, roleTypes);
    }

    private RoleType[] getRoleTypes(JoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        AuthorizedRole authorizedRole = method.getAnnotation(AuthorizedRole.class);
        return authorizedRole.roleType();
    }
}
