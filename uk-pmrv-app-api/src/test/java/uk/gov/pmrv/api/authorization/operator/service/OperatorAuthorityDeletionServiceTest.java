package uk.gov.pmrv.api.authorization.operator.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import uk.gov.pmrv.api.authorization.core.domain.Authority;
import uk.gov.pmrv.api.authorization.core.domain.AuthorityStatus;
import uk.gov.pmrv.api.authorization.core.repository.AuthorityRepository;
import uk.gov.pmrv.api.authorization.operator.event.OperatorAuthorityDeletionEvent;
import uk.gov.pmrv.api.common.exception.BusinessException;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static uk.gov.pmrv.api.common.exception.ErrorCode.AUTHORITY_MIN_ONE_OPERATOR_ADMIN_SHOULD_EXIST;
import static uk.gov.pmrv.api.common.exception.ErrorCode.AUTHORITY_USER_NOT_RELATED_TO_ACCOUNT;

@ExtendWith(MockitoExtension.class)
class OperatorAuthorityDeletionServiceTest {

    @InjectMocks
    private OperatorAuthorityDeletionService operatorAuthorityDeletionService;

    @Mock
    private AuthorityRepository authorityRepository;

    @Mock
    private OperatorAdminExistenceValidator operatorAdminExistenceValidator;

    @Mock
    private ApplicationEventPublisher eventPublisher;


    @BeforeEach
    void setUp() {
        List<OperatorAuthorityDeleteValidator> operatorAuthorityDeleteValidators =
            List.of(operatorAdminExistenceValidator);
        operatorAuthorityDeletionService =
            new OperatorAuthorityDeletionService(authorityRepository, operatorAuthorityDeleteValidators, eventPublisher);
    }

    @Test
    void deleteAccountOperatorAuthority() {
        String userId = "userId";
        Long accountId = 1L;

        Authority authority = Authority.builder()
                .userId(userId)
                .accountId(accountId)
                .code("operator_user")
                .status(AuthorityStatus.ACTIVE)
                .build();

        when(authorityRepository.findByUserId(userId)).thenReturn(List.of(authority));

        operatorAuthorityDeletionService.deleteAccountOperatorAuthority(userId, accountId);

        verify(operatorAdminExistenceValidator, times(1)).validateDeletion(authority);
        verify(authorityRepository, times(1)).delete(authority);
        verify(eventPublisher, times(1)).publishEvent(OperatorAuthorityDeletionEvent.builder()
                .userId(userId).accountId(accountId).build());
    }

    @Test
    void deleteAccountOperatorAuthority_user_not_related_to_account() {
        String userId = "userId";
        Long accountId = 1L;
        Authority authority = Authority.builder()
                .userId(userId)
                .accountId(2L)
                .code("operator_user")
                .status(AuthorityStatus.ACTIVE)
                .build();

        when(authorityRepository.findByUserId(userId)).thenReturn(List.of(authority));

        BusinessException businessException = assertThrows(BusinessException.class,
            () -> operatorAuthorityDeletionService.deleteAccountOperatorAuthority(userId, accountId));

        assertThat(businessException.getErrorCode()).isEqualTo(AUTHORITY_USER_NOT_RELATED_TO_ACCOUNT);
        verifyNoInteractions(operatorAdminExistenceValidator);
        verifyNoMoreInteractions(authorityRepository);
        verifyNoInteractions(eventPublisher);
    }

    @Test
    void deleteAccountOperatorAuthority_delete_the_only_operator_admin() {
        String userId = "userId";
        Long accountId = 1L;

        Authority authority = Authority.builder()
                .userId(userId)
                .accountId(accountId)
                .code("operator_admin")
                .status(AuthorityStatus.ACTIVE)
                .build();

        when(authorityRepository.findByUserId(userId)).thenReturn(List.of(authority));

        doThrow(new BusinessException(AUTHORITY_MIN_ONE_OPERATOR_ADMIN_SHOULD_EXIST))
            .when(operatorAdminExistenceValidator).validateDeletion(authority);

        BusinessException businessException = assertThrows(BusinessException.class,
            () -> operatorAuthorityDeletionService.deleteAccountOperatorAuthority(userId, accountId));

        assertThat(businessException.getErrorCode()).isEqualTo(AUTHORITY_MIN_ONE_OPERATOR_ADMIN_SHOULD_EXIST);
        verify(operatorAdminExistenceValidator, times(1)).validateDeletion(authority);
        verifyNoMoreInteractions(authorityRepository);
        verifyNoInteractions(eventPublisher);
    }

    @Test
    void deleteAccountOperatorAuthority_with_only_one_account_under_accepted() {
        String userId = "userId";
        Long accountId = 1L;

        Authority authority = Authority.builder()
                .userId(userId)
                .accountId(accountId)
                .code("operator_user")
                .status(AuthorityStatus.ACCEPTED)
                .build();

        when(authorityRepository.findByUserId(userId)).thenReturn(List.of(authority));

        operatorAuthorityDeletionService.deleteAccountOperatorAuthority(userId, accountId);

        verify(operatorAdminExistenceValidator, times(1)).validateDeletion(authority);
        verify(authorityRepository, times(1)).delete(authority);
        verify(eventPublisher, times(1)).publishEvent(OperatorAuthorityDeletionEvent.builder()
                .userId(userId).accountId(accountId).build());
    }

    @Test
    void deleteAccountOperatorAuthority_with_only_one_account_under_pending() {
        String userId = "userId";
        Long accountId = 1L;

        Authority authority = Authority.builder()
                .userId(userId)
                .accountId(accountId)
                .code("operator_user")
                .status(AuthorityStatus.PENDING)
                .build();

        when(authorityRepository.findByUserId(userId)).thenReturn(List.of(authority));

        operatorAuthorityDeletionService.deleteAccountOperatorAuthority(userId, accountId);

        verify(operatorAdminExistenceValidator, times(1)).validateDeletion(authority);
        verify(authorityRepository, times(1)).delete(authority);
        verify(eventPublisher, times(1)).publishEvent(OperatorAuthorityDeletionEvent.builder()
                .userId(userId).accountId(accountId).build());
    }

    @Test
    void deleteAccountOperatorAuthority_do_not_delete_user() {
        String userId = "userId";
        Long accountId = 1L;

        Authority authority1 = Authority.builder()
                .userId(userId)
                .accountId(accountId)
                .code("operator_user")
                .status(AuthorityStatus.ACCEPTED)
                .build();
        Authority authority2 = Authority.builder()
                .userId(userId)
                .accountId(2L)
                .code("operator_user")
                .status(AuthorityStatus.ACCEPTED)
                .build();

        when(authorityRepository.findByUserId(userId)).thenReturn(List.of(authority1, authority2));

        operatorAuthorityDeletionService.deleteAccountOperatorAuthority(userId, accountId);

        verify(operatorAdminExistenceValidator, times(1)).validateDeletion(authority1);
        verify(authorityRepository, times(1)).delete(authority1);
        verify(eventPublisher, times(1)).publishEvent(OperatorAuthorityDeletionEvent.builder()
                .userId(userId).accountId(accountId).build());
    }
}
