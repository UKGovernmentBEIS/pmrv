package uk.gov.pmrv.api.web.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.aop.aspectj.annotation.AspectJProxyFactory;
import org.springframework.aop.framework.AopProxy;
import org.springframework.aop.framework.DefaultAopProxyFactory;
import uk.gov.pmrv.api.authorization.rules.services.PmrvUserAuthorizationService;
import uk.gov.pmrv.api.common.domain.enumeration.RoleType;
import uk.gov.pmrv.api.common.domain.model.PmrvUser;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthorizedAspectTest {

    @Mock
    private PmrvUserAuthorizationService pmrvUserAuthorizationService;

    @Mock
    private PmrvSecurityComponent pmrvSecurityComponent;

    private AuthorizedTest authorizedTest = new AuthorizedTest();
    private static final PmrvUser USER = PmrvUser.builder().userId("userId").roleType(RoleType.OPERATOR).build();

    @BeforeEach
    public void setUp() {
        AuthorizationAspectUserResolver authorizationAspectUserResolver = new AuthorizationAspectUserResolver(pmrvSecurityComponent);
        AuthorizedAspect aspect = new AuthorizedAspect(pmrvUserAuthorizationService, authorizationAspectUserResolver);

        AspectJProxyFactory aspectJProxyFactory = new AspectJProxyFactory(authorizedTest);
        aspectJProxyFactory.addAspect(aspect);

        DefaultAopProxyFactory proxyFactory = new DefaultAopProxyFactory();
        AopProxy aopProxy = proxyFactory.createAopProxy(aspectJProxyFactory);

        authorizedTest = (AuthorizedTest) aopProxy.getProxy();
    }

    @Test
    void authorizeLong() {
        authorizedTest.testMethodResourceLong(USER, 1L);
        verify(pmrvUserAuthorizationService, times(1)).authorize(USER, "testMethodResourceLong", "1");
    }

    @Test
    void authorizeString() {
        authorizedTest.testMethodResourceString(USER, "aaa");
        verify(pmrvUserAuthorizationService, times(1)).authorize(USER, "testMethodResourceString", "aaa");
    }

    @Test
    void authorizeEmptyUser() {
        when(pmrvSecurityComponent.getAuthenticatedUser()).thenReturn(USER);

        authorizedTest.testMethodResourceEmptyUser("aaa");
        verify(pmrvUserAuthorizationService, times(1)).authorize(USER, "testMethodResourceEmptyUser", "aaa");
    }

    @Test
    void authorizeNull() {
        authorizedTest.testMethodResourceNull(USER);
        verify(pmrvUserAuthorizationService, times(1)).authorize(USER, "testMethodResourceNull");
    }

    @Test
    void authorizeWithResourceSubType() {
        authorizedTest.testMethodResourceSubTypeString(USER, "resourceId", "resourceSubType");
        verify(pmrvUserAuthorizationService, times(1)).authorize(USER, "testMethodResourceSubTypeString", "resourceId", "resourceSubType");
    }

    public static class AuthorizedTest {
        @Authorized(resourceId = "#resourceId")
        public void testMethodResourceLong(PmrvUser user, Long resourceId) {
        }

        @Authorized(resourceId = "#resourceId")
        public void testMethodResourceString(PmrvUser user, String resourceId) {
        }

        @Authorized(resourceId = "#resourceId")
        public void testMethodResourceNull(PmrvUser user) {
        }

        @Authorized(resourceId = "#resourceId")
        public void testMethodResourceEmptyUser(String resourceId) {
        }

        @Authorized(resourceId = "#resourceId", resourceSubType = "#resourceSubType")
        public void testMethodResourceSubTypeString(PmrvUser user, String resourceId, String resourceSubType) {
        }
    }
}