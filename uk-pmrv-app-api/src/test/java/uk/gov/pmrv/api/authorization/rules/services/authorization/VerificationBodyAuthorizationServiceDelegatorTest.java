package uk.gov.pmrv.api.authorization.rules.services.authorization;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.pmrv.api.common.domain.enumeration.RoleType.REGULATOR;
import static uk.gov.pmrv.api.common.domain.enumeration.RoleType.VERIFIER;

import java.util.List;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.pmrv.api.authorization.core.domain.Permission;
import uk.gov.pmrv.api.common.domain.model.PmrvUser;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(MockitoExtension.class)
class VerificationBodyAuthorizationServiceDelegatorTest {

    private VerificationBodyAuthorizationServiceDelegator verificationBodyAuthorizationServiceDelegator;
    private VerifierVerificationBodyAuthorizationService verifierVerificationBodyAuthorizationService;

    private final PmrvUser verifierUser = PmrvUser.builder().roleType(VERIFIER).build();
    private final PmrvUser regulatorUser = PmrvUser.builder().roleType(REGULATOR).build();

    @BeforeAll
    void beforeAll() {
        verifierVerificationBodyAuthorizationService = mock(VerifierVerificationBodyAuthorizationService.class);
        verificationBodyAuthorizationServiceDelegator =
            new VerificationBodyAuthorizationServiceDelegator(List.of(verifierVerificationBodyAuthorizationService));
        when(verifierVerificationBodyAuthorizationService.getRoleType()).thenReturn(VERIFIER);
    }

    @Test
    void isAuthorized_verifier_no_permissions() {
        when(verifierVerificationBodyAuthorizationService.isAuthorized(verifierUser, 1L))
            .thenReturn(true);

        assertTrue(verificationBodyAuthorizationServiceDelegator.isAuthorized(verifierUser, 1L));

        verify(verifierVerificationBodyAuthorizationService, times(1))
            .isAuthorized(verifierUser, 1L);
    }

    @Test
    void isAuthorized_verifier_with_permissions() {
        Permission permission = Permission.PERM_TASK_ASSIGNMENT;

        when(verifierVerificationBodyAuthorizationService.isAuthorized(verifierUser, 1L, permission))
            .thenReturn(true);

        assertTrue(verificationBodyAuthorizationServiceDelegator.isAuthorized(verifierUser, 1L, permission));

        verify(verifierVerificationBodyAuthorizationService, times(1))
            .isAuthorized(verifierUser, 1L, permission);
    }

    @Test
    void isAuthorized_non_verifier_no_permissions() {
        assertFalse(verificationBodyAuthorizationServiceDelegator.isAuthorized(regulatorUser, 1L));
    }

    @Test
    void isAuthorized_non_verifier_with_permissions() {
        Permission permission = Permission.PERM_TASK_ASSIGNMENT;

        when(verifierVerificationBodyAuthorizationService.isAuthorized(regulatorUser, 1L, permission))
            .thenReturn(false);

        assertFalse(verificationBodyAuthorizationServiceDelegator.isAuthorized(regulatorUser, 1L, permission));
    }

}