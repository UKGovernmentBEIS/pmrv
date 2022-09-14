package uk.gov.pmrv.api.authorization.rules.services.authorization;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
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
import uk.gov.pmrv.api.common.domain.enumeration.RoleType;
import uk.gov.pmrv.api.common.domain.model.PmrvUser;

@ExtendWith(MockitoExtension.class)
class PmrvVerificationBodyAuthorizationServiceTest {

    @InjectMocks
    private PmrvVerificationBodyAuthorizationService pmrvVerificationBodyAuthorizationService;

    @Mock
    private VerificationBodyAuthorizationServiceDelegator verificationBodyAuthorizationServiceDelegator;

    private final PmrvUser user = PmrvUser.builder().roleType(RoleType.VERIFIER).build();

    @Test
    void isAuthorized_no_permission() {
        Long verificationBodyId = 1L;
        AuthorizationCriteria criteria = AuthorizationCriteria.builder().verificationBodyId(verificationBodyId).build();

        when(verificationBodyAuthorizationServiceDelegator.isAuthorized(user, verificationBodyId)).thenReturn(true);

        assertTrue(pmrvVerificationBodyAuthorizationService.isAuthorized(user, criteria));
        verify(verificationBodyAuthorizationServiceDelegator, times(1)).isAuthorized(user, verificationBodyId);
    }

    @Test
    void isAuthorized_with_permission() {
        Long verificationBodyId = 1L;
        Permission permission = Permission.PERM_VB_USERS_EDIT;
        AuthorizationCriteria criteria = AuthorizationCriteria
            .builder()
            .verificationBodyId(verificationBodyId)
            .permission(permission)
            .build();

        when(verificationBodyAuthorizationServiceDelegator.isAuthorized(user, verificationBodyId, permission))
            .thenReturn(true);

        assertTrue(pmrvVerificationBodyAuthorizationService.isAuthorized(user, criteria));
        verify(verificationBodyAuthorizationServiceDelegator, times(1))
            .isAuthorized(user, verificationBodyId, permission);
    }

    @Test
    void getResourceType() {
        assertEquals(ResourceType.VERIFICATION_BODY, pmrvVerificationBodyAuthorizationService.getResourceType());
    }
}