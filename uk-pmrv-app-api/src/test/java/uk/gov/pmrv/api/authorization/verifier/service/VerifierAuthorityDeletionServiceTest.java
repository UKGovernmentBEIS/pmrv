package uk.gov.pmrv.api.authorization.verifier.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import uk.gov.pmrv.api.authorization.core.domain.Authority;
import uk.gov.pmrv.api.authorization.core.domain.AuthorityStatus;
import uk.gov.pmrv.api.authorization.core.repository.AuthorityRepository;
import uk.gov.pmrv.api.authorization.verifier.event.VerifierAuthorityDeletionEvent;
import uk.gov.pmrv.api.common.domain.enumeration.RoleType;
import uk.gov.pmrv.api.common.domain.model.PmrvAuthority;
import uk.gov.pmrv.api.common.domain.model.PmrvUser;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static uk.gov.pmrv.api.common.exception.ErrorCode.AUTHORITY_VERIFIER_ADMIN_SHOULD_EXIST;

@ExtendWith(MockitoExtension.class)
class VerifierAuthorityDeletionServiceTest {

    @InjectMocks
    private VerifierAuthorityDeletionService verifierAuthorityDeletionService;

    @Mock
    private AuthorityRepository authorityRepository;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @Mock
    private VerifierAdminExistenceValidator verifierAdminExistenceValidator;

    @Test
    void deleteVerifierAuthority_verifier_auth_user() {
        final String userId = "userId";
        final Long verificationBodyId = 1L;
        PmrvAuthority pmrvAuthority = PmrvAuthority.builder().verificationBodyId(verificationBodyId).build();
        final PmrvUser authUser = PmrvUser.builder()
            .userId("authUserId")
            .roleType(RoleType.VERIFIER)
            .authorities(List.of(pmrvAuthority))
            .build();
        final Authority authority = Authority.builder().userId(userId).status(AuthorityStatus.ACTIVE).build();
        VerifierAuthorityDeletionEvent event = VerifierAuthorityDeletionEvent.builder()
                .userId(userId).build();

        when(authorityRepository.findByUserIdAndVerificationBodyId(userId, verificationBodyId))
            .thenReturn(Optional.of(authority));

        // Invoke
        verifierAuthorityDeletionService.deleteVerifierAuthority(userId, authUser);

        // Verify
        verify(authorityRepository, times(1)).findByUserIdAndVerificationBodyId(userId, verificationBodyId);
        verify(authorityRepository, times(1)).delete(authority);
        verify(eventPublisher, times(1)).publishEvent(event);
        verify(verifierAdminExistenceValidator, times(1)).validateDeletion(authority);
        verify(authorityRepository, never()).findByUserIdAndVerificationBodyIdNotNull(anyString());
    }

    @Test
    void deleteVerifierAuthority_regulator_auth_user() {
        final String userId = "userId";
        final PmrvUser authUser = PmrvUser.builder().userId("authUserId").roleType(RoleType.REGULATOR).build();
        final Authority authority = Authority.builder().userId(userId).status(AuthorityStatus.ACCEPTED).build();
        VerifierAuthorityDeletionEvent event = VerifierAuthorityDeletionEvent.builder()
                .userId(userId).build();

        when(authorityRepository.findByUserIdAndVerificationBodyIdNotNull(userId))
            .thenReturn(Optional.of(authority));

        // Invoke
        verifierAuthorityDeletionService.deleteVerifierAuthority(userId, authUser);

        // Verify
        verify(authorityRepository, times(1)).findByUserIdAndVerificationBodyIdNotNull(userId);
        verify(authorityRepository, times(1)).delete(authority);
        verify(verifierAdminExistenceValidator, times(1)).validateDeletion(authority);
        verify(eventPublisher, times(1)).publishEvent(event);
        verify(authorityRepository, never()).findByUserIdAndVerificationBodyId(anyString(), anyLong());
    }

    @Test
    void deleteVerifierAuthority_auth_user_role_not_supported() {
        final String userId = "userId";
        final PmrvUser authUser = PmrvUser.builder().userId("authUserId").roleType(RoleType.OPERATOR).build();

        // Invoke
        assertThrows(UnsupportedOperationException.class,
                () -> verifierAuthorityDeletionService.deleteVerifierAuthority(userId, authUser));

        // Verify
        verifyNoInteractions(authorityRepository, eventPublisher, verifierAdminExistenceValidator);
    }

    @Test
    void deleteVerifierAuthority_regulator_auth_user_authority_not_found() {
        final String userId = "userId";
        final PmrvUser authUser = PmrvUser.builder().userId("authUserId").roleType(RoleType.REGULATOR).build();

        when(authorityRepository.findByUserIdAndVerificationBodyIdNotNull(userId))
            .thenReturn(Optional.empty());

        // Invoke
        BusinessException businessException = assertThrows(BusinessException.class,
                () -> verifierAuthorityDeletionService.deleteVerifierAuthority(userId, authUser));

        // Verify
        assertThat(businessException.getErrorCode()).isEqualTo(ErrorCode.AUTHORITY_USER_IS_NOT_VERIFIER);
        verify(authorityRepository, times(1)).findByUserIdAndVerificationBodyIdNotNull(userId);
        verifyNoMoreInteractions(authorityRepository, eventPublisher, verifierAdminExistenceValidator);
    }

    @Test
    void deleteVerifierAuthority_verifier_auth_user_authority_not_found() {
        final String user = "user";
        final Long verificationBodyId = 1L;
        PmrvAuthority pmrvAuthority = PmrvAuthority.builder().verificationBodyId(verificationBodyId).build();
        final PmrvUser authUser = PmrvUser.builder()
            .userId("authUserId")
            .roleType(RoleType.VERIFIER)
            .authorities(List.of(pmrvAuthority))
            .build();

        when(authorityRepository.findByUserIdAndVerificationBodyId(user, verificationBodyId))
            .thenReturn(Optional.empty());

        // Invoke
        BusinessException businessException = assertThrows(BusinessException.class,
                () -> verifierAuthorityDeletionService.deleteVerifierAuthority(user, authUser));

        // Verify
        assertThat(businessException.getErrorCode()).isEqualTo(ErrorCode.AUTHORITY_USER_NOT_RELATED_TO_VERIFICATION_BODY);
        verify(authorityRepository, never()).existsOtherVerificationBodyAdmin(anyString(), anyLong());
        verify(authorityRepository, never()).delete(any());
        verify(eventPublisher, never()).publishEvent(any());
    }

    @Test
    void deleteVerifierAuthority_only_verifier_admin() {
        final String user = "user";
        final Long verificationBodyId = 1L;
        final Authority authority = Authority.builder().build();

        when(authorityRepository.findByUserIdAndVerificationBodyId(user, verificationBodyId))
            .thenReturn(Optional.of(authority));
        doThrow(new BusinessException(AUTHORITY_VERIFIER_ADMIN_SHOULD_EXIST))
            .when(verifierAdminExistenceValidator).validateDeletion(authority);

        // Invoke
        BusinessException businessException =
            assertThrows(BusinessException.class, () -> verifierAuthorityDeletionService.deleteVerifierAuthority(user, verificationBodyId));

        // Verify
        assertThat(businessException.getErrorCode()).isEqualTo(AUTHORITY_VERIFIER_ADMIN_SHOULD_EXIST);
        verify(authorityRepository, never()).delete(any());
        verify(verifierAdminExistenceValidator, times(1)).validateDeletion(authority);
        verify(eventPublisher, never()).publishEvent(any());
    }

    @Test
    void deleteVerifierAuthorities() {
        final Long verificationBodyId = 1L;
        final List<String> userIds = List.of("userId1", "userId2");
        final List<Authority> authorities = List.of(Authority.builder().userId(userIds.get(0)).status(AuthorityStatus.ACTIVE).build(),
                Authority.builder().userId(userIds.get(1)).status(AuthorityStatus.PENDING).build());

        when(authorityRepository.findAllByVerificationBodyId(verificationBodyId)).thenReturn(authorities);

        // Invoke
        verifierAuthorityDeletionService.deleteVerifierAuthorities(verificationBodyId);

        // Verify
        verify(authorityRepository, times(1)).findAllByVerificationBodyId(verificationBodyId);
        verify(authorityRepository, times(1)).deleteAll(authorities);
        verify(eventPublisher, times(1))
                .publishEvent(VerifierAuthorityDeletionEvent.builder().userId(userIds.get(0)).build());
        verify(eventPublisher, times(1))
                .publishEvent(VerifierAuthorityDeletionEvent.builder().userId(userIds.get(1)).build());
        verifyNoMoreInteractions(eventPublisher);
    }

    @Test
    void deleteVerifierAuthorities_empty() {
        final Long verificationBodyId = 1L;

        when(authorityRepository.findAllByVerificationBodyId(verificationBodyId)).thenReturn(List.of());

        // Invoke
        verifierAuthorityDeletionService.deleteVerifierAuthorities(verificationBodyId);

        // Verify
        verify(authorityRepository, times(1)).findAllByVerificationBodyId(verificationBodyId);
        verify(authorityRepository, times(1)).deleteAll(List.of());
        verify(eventPublisher, never()).publishEvent(any());
    }
}