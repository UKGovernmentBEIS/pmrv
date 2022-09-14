package uk.gov.pmrv.api.web.security;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.common.domain.enumeration.RoleType;
import uk.gov.pmrv.api.common.domain.model.PmrvUser;

import java.lang.reflect.Method;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthorizationAspectUserResolverTest {

    @InjectMocks
    private AuthorizationAspectUserResolver authorizationAspectUserResolver;

    @Mock
    private PmrvSecurityComponent pmrvSecurityComponent;

    @Test
    void getUser_when_provided_in_parameters() throws NoSuchMethodException {
        JoinPoint joinPoint = Mockito.mock(JoinPoint.class);
        MethodSignature signature = Mockito.mock(MethodSignature.class);
        PmrvUser user = PmrvUser.builder().userId("userId").roleType(RoleType.OPERATOR).build();

        when(joinPoint.getSignature()).thenReturn(signature);
        when(joinPoint.getArgs()).thenReturn(new Object[]{user});
        when(signature.getMethod()).thenReturn(getMethod("testMethodWithParam"));

        PmrvUser expectedUser = authorizationAspectUserResolver.getUser(joinPoint);

        assertEquals(expectedUser, user);

        verify(pmrvSecurityComponent, never()).getAuthenticatedUser();
    }

    @Test
    void getUser_when_no_provided_in_parameters() throws NoSuchMethodException {
        JoinPoint joinPoint = Mockito.mock(JoinPoint.class);
        MethodSignature signature = Mockito.mock(MethodSignature.class);
        PmrvUser user = PmrvUser.builder().userId("userId").roleType(RoleType.OPERATOR).build();

        when(joinPoint.getSignature()).thenReturn(signature);
        when(signature.getMethod()).thenReturn(getMethod("testMethod"));
        when(pmrvSecurityComponent.getAuthenticatedUser()).thenReturn(user);

        PmrvUser expectedUser = authorizationAspectUserResolver.getUser(joinPoint);

        assertEquals(expectedUser, user);

        verify(pmrvSecurityComponent, times(1)).getAuthenticatedUser();
    }

    private Method getMethod(String methodName) throws NoSuchMethodException {
        return Arrays.stream(getClass().getDeclaredMethods())
            .filter(method -> methodName.equalsIgnoreCase(method.getName()))
            .findAny()
            .orElseThrow(NoSuchMethodException::new);
    }

    private void testMethod() {}

    private void testMethodWithParam(PmrvUser pmrvUser){}
}