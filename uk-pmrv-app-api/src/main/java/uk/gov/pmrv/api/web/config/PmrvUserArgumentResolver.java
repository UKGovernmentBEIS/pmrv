package uk.gov.pmrv.api.web.config;

import lombok.RequiredArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import uk.gov.pmrv.api.common.domain.model.PmrvUser;
import uk.gov.pmrv.api.web.security.PmrvSecurityComponent;

@Component
@RequiredArgsConstructor
public class PmrvUserArgumentResolver implements HandlerMethodArgumentResolver {
    private final PmrvSecurityComponent pmrvSecurityComponent;

    @Override
    public PmrvUser resolveArgument(MethodParameter methodParameter,
                                  ModelAndViewContainer modelViewContainer, NativeWebRequest nativeWebRequest,
                                  WebDataBinderFactory webDataBinderFactory) {
        return pmrvSecurityComponent.getAuthenticatedUser();
    }

    @Override
    public boolean supportsParameter(MethodParameter methodParameter) {
        return methodParameter.getParameterType().equals(PmrvUser.class);
    }
}
