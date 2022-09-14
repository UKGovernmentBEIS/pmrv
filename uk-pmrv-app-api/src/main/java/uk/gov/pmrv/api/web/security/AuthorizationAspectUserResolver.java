package uk.gov.pmrv.api.web.security;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import uk.gov.pmrv.api.common.domain.model.PmrvUser;

@Component
@RequiredArgsConstructor
public class AuthorizationAspectUserResolver {

    private final PmrvSecurityComponent pmrvSecurityComponent;

    public PmrvUser getUser(JoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        List<Parameter> parameterList = Arrays.asList(method.getParameters());
        return parameterList.stream()
            .filter(parameter -> parameter.getType().equals(PmrvUser.class))
            .findAny()
            .map(param -> (PmrvUser) Arrays.asList(joinPoint.getArgs()).get(parameterList.indexOf(param)))
            .orElseGet(pmrvSecurityComponent::getAuthenticatedUser);
    }
}
