package uk.gov.pmrv.api.authorization.rules.services.authorization;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.pmrv.api.authorization.core.domain.Permission;
import uk.gov.pmrv.api.authorization.rules.domain.ResourceType;
import uk.gov.pmrv.api.common.domain.enumeration.CompetentAuthority;
import uk.gov.pmrv.api.common.domain.enumeration.RoleType;
import uk.gov.pmrv.api.common.domain.model.PmrvUser;

@ExtendWith(MockitoExtension.class)
class PmrvCompAuthAuthorizationServiceTest {

    @InjectMocks
    private PmrvCompAuthAuthorizationService pmrvCompAuthAuthorizationService;

    @Mock
    private CompAuthAuthorizationServiceDelegator compAuthAuthorizationServiceDelegator;

    private final PmrvUser user = PmrvUser.builder().roleType(RoleType.REGULATOR).build();

    @Test
    void isAuthorized_no_permission() {
        CompetentAuthority compAuth = CompetentAuthority.ENGLAND;
        AuthorizationCriteria criteria = AuthorizationCriteria.builder().competentAuthority(compAuth).build();

        when(compAuthAuthorizationServiceDelegator.isAuthorized(user, compAuth)).thenReturn(true);

        assertTrue(pmrvCompAuthAuthorizationService.isAuthorized(user, criteria));
        verify(compAuthAuthorizationServiceDelegator, times(1)).isAuthorized(user, compAuth);
    }

    @Test
    void isAuthorized_with_permission() {
        CompetentAuthority compAuth = CompetentAuthority.ENGLAND;
        Permission permission = Permission.PERM_TASK_ASSIGNMENT;
        AuthorizationCriteria criteria = AuthorizationCriteria
            .builder()
            .competentAuthority(compAuth)
            .permission(permission)
            .build();

        when(compAuthAuthorizationServiceDelegator.isAuthorized(user, compAuth, permission))
            .thenReturn(true);

        assertTrue(pmrvCompAuthAuthorizationService.isAuthorized(user, criteria));
        verify(compAuthAuthorizationServiceDelegator, times(1))
            .isAuthorized(user, compAuth, permission);
    }

    @Test
    void getResourceType() {
        assertEquals(ResourceType.CA, pmrvCompAuthAuthorizationService.getResourceType());
    }

}