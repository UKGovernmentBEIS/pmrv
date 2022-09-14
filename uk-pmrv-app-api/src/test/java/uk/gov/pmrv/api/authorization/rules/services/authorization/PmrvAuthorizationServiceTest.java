package uk.gov.pmrv.api.authorization.rules.services.authorization;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.authorization.rules.domain.ResourceType;
import uk.gov.pmrv.api.common.domain.enumeration.CompetentAuthority;
import uk.gov.pmrv.api.common.domain.enumeration.RoleType;
import uk.gov.pmrv.api.common.domain.model.PmrvUser;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;

@ExtendWith(MockitoExtension.class)
class PmrvAuthorizationServiceTest {
    @InjectMocks
    private PmrvAuthorizationService pmrvAuthorizationService;

    @Mock
    private PmrvResourceAuthorizationServiceDelegator pmrvResourceAuthorizationServiceDelegator;

    @Test
    void isAuthorized_criteria_with_account_only() {
        PmrvUser user = PmrvUser.builder().roleType(RoleType.OPERATOR).build();
        AuthorizationCriteria criteria = AuthorizationCriteria
            .builder()
            .accountId(1L)
            .build();

        when(pmrvResourceAuthorizationServiceDelegator.isAuthorized(ResourceType.ACCOUNT, user, criteria))
            .thenReturn(true);

        pmrvAuthorizationService.authorize(user, criteria);

        verify(pmrvResourceAuthorizationServiceDelegator, times(1))
            .isAuthorized(ResourceType.ACCOUNT, user, criteria);
    }

    @Test
    void isAuthorized_criteria_with_competent_authority_only() {
        PmrvUser user = PmrvUser.builder().roleType(RoleType.REGULATOR).build();
        AuthorizationCriteria criteria = AuthorizationCriteria
            .builder()
            .competentAuthority(CompetentAuthority.ENGLAND)
            .build();

        when(pmrvResourceAuthorizationServiceDelegator.isAuthorized(ResourceType.CA, user, criteria))
            .thenReturn(true);

        pmrvAuthorizationService.authorize(user, criteria);

        verify(pmrvResourceAuthorizationServiceDelegator, times(1))
            .isAuthorized(ResourceType.CA, user, criteria);
    }

    @Test
    void isAuthorized_creiteria_with_verification_body_only() {
        PmrvUser user = PmrvUser.builder().roleType(RoleType.VERIFIER).build();
        AuthorizationCriteria criteria = AuthorizationCriteria
            .builder()
            .verificationBodyId(1L)
            .build();

        when(pmrvResourceAuthorizationServiceDelegator.isAuthorized(ResourceType.VERIFICATION_BODY, user, criteria))
            .thenReturn(true);

        pmrvAuthorizationService.authorize(user, criteria);

        verify(pmrvResourceAuthorizationServiceDelegator, times(1))
            .isAuthorized(ResourceType.VERIFICATION_BODY, user, criteria);
    }

    @Test
    void isAuthorized_operator_user() {
        PmrvUser user = PmrvUser.builder().roleType(RoleType.OPERATOR).build();
        AuthorizationCriteria criteria = AuthorizationCriteria
            .builder()
            .accountId(1L)
            .competentAuthority(CompetentAuthority.ENGLAND)
            .build();

        when(pmrvResourceAuthorizationServiceDelegator.isAuthorized(ResourceType.ACCOUNT, user, criteria))
            .thenReturn(true);

        pmrvAuthorizationService.authorize(user, criteria);

        verify(pmrvResourceAuthorizationServiceDelegator, times(1))
            .isAuthorized(ResourceType.ACCOUNT, user, criteria);
    }

    @Test
    void isAuthorized_regulator_user() {
        PmrvUser user = PmrvUser.builder().roleType(RoleType.REGULATOR).build();
        AuthorizationCriteria criteria = AuthorizationCriteria
            .builder()
            .accountId(1L)
            .competentAuthority(CompetentAuthority.ENGLAND)
            .build();

        when(pmrvResourceAuthorizationServiceDelegator.isAuthorized(ResourceType.CA, user, criteria))
            .thenReturn(true);

        pmrvAuthorizationService.authorize(user, criteria);

        verify(pmrvResourceAuthorizationServiceDelegator, times(1))
            .isAuthorized(ResourceType.CA, user, criteria);
    }

    @Test
    void isAuthorized_verifier_user() {
        PmrvUser user = PmrvUser.builder().roleType(RoleType.VERIFIER).build();
        AuthorizationCriteria criteria = AuthorizationCriteria
            .builder()
            .accountId(1L)
            .verificationBodyId(2L)
            .build();

        when(pmrvResourceAuthorizationServiceDelegator.isAuthorized(ResourceType.VERIFICATION_BODY, user, criteria))
            .thenReturn(true);

        pmrvAuthorizationService.authorize(user, criteria);

        verify(pmrvResourceAuthorizationServiceDelegator, times(1))
            .isAuthorized(ResourceType.VERIFICATION_BODY, user, criteria);
    }

    @Test
    void isAuthorized_throws_forbidden() {
        PmrvUser user = PmrvUser.builder().roleType(RoleType.OPERATOR).build();
        AuthorizationCriteria criteria = AuthorizationCriteria
            .builder()
            .accountId(1L)
            .build();

        when(pmrvResourceAuthorizationServiceDelegator.isAuthorized(ResourceType.ACCOUNT, user, criteria))
            .thenReturn(false);

        BusinessException businessException = assertThrows(BusinessException.class,
            () -> pmrvAuthorizationService.authorize(user, criteria));

        assertEquals(ErrorCode.FORBIDDEN, businessException.getErrorCode());

        verify(pmrvResourceAuthorizationServiceDelegator, times(1))
            .isAuthorized(ResourceType.ACCOUNT, user, criteria);
    }

}