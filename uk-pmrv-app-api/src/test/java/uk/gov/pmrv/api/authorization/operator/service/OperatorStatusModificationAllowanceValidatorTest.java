package uk.gov.pmrv.api.authorization.operator.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.pmrv.api.authorization.core.domain.AuthorityStatus;
import uk.gov.pmrv.api.authorization.core.repository.AuthorityRepository;
import uk.gov.pmrv.api.authorization.operator.domain.AccountOperatorAuthorityUpdateDTO;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.pmrv.api.authorization.core.domain.AuthorityStatus.ACCEPTED;
import static uk.gov.pmrv.api.authorization.core.domain.AuthorityStatus.ACTIVE;
import static uk.gov.pmrv.api.authorization.core.domain.AuthorityStatus.DISABLED;

@ExtendWith(MockitoExtension.class)
class OperatorStatusModificationAllowanceValidatorTest {

    @InjectMocks
    private OperatorStatusModificationAllowanceValidator validator;

    @Mock
    private AuthorityRepository authorityRepository;

    @Test
    void validate() {
        Long accountId = 1L;
        String uerId1 = "uerId1";
        String uerId2 = "uerId2";
        List<AccountOperatorAuthorityUpdateDTO> accountOperatorAuthorityUpdates = List.of(
                AccountOperatorAuthorityUpdateDTO.builder().userId(uerId1).authorityStatus(ACTIVE).build(),
                AccountOperatorAuthorityUpdateDTO.builder().userId(uerId2).authorityStatus(ACTIVE).build()
        );
        Map<String, AuthorityStatus> existingUserStatuses = Map.of(
                uerId1, DISABLED,
                uerId2, ACCEPTED
        );

        when(authorityRepository.findStatusByUsersAndAccountId(List.of(uerId1, uerId2), accountId))
                .thenReturn(existingUserStatuses);

        validator.validateUpdate(accountOperatorAuthorityUpdates, accountId);

        verify(authorityRepository, times(1))
                .findStatusByUsersAndAccountId(List.of(uerId1, uerId2), accountId);
    }

    @Test
    void validate_status_remain_ACCEPTED() {
        Long accountId = 1L;
        String uerId1 = "uerId1";
        String uerId2 = "uerId2";
        List<AccountOperatorAuthorityUpdateDTO> accountOperatorAuthorityUpdates = List.of(
                AccountOperatorAuthorityUpdateDTO.builder().userId(uerId1).authorityStatus(ACTIVE).build(),
                AccountOperatorAuthorityUpdateDTO.builder().userId(uerId2).authorityStatus(ACCEPTED).build()
        );
        Map<String, AuthorityStatus> existingUserStatuses = Map.of(
                uerId1, DISABLED,
                uerId2, ACCEPTED
        );

        when(authorityRepository.findStatusByUsersAndAccountId(List.of(uerId1, uerId2), accountId))
                .thenReturn(existingUserStatuses);

        validator.validateUpdate(accountOperatorAuthorityUpdates, accountId);

        verify(authorityRepository, times(1))
                .findStatusByUsersAndAccountId(List.of(uerId1, uerId2), accountId);
    }

    @Test
    void validate_status_cannot_change_from_ACCEPTED() {
        Long accountId = 1L;
        String uerId1 = "uerId1";
        String uerId2 = "uerId2";
        List<AccountOperatorAuthorityUpdateDTO> accountOperatorAuthorityUpdates = List.of(
                AccountOperatorAuthorityUpdateDTO.builder().userId(uerId1).authorityStatus(ACTIVE).build(),
                AccountOperatorAuthorityUpdateDTO.builder().userId(uerId2).authorityStatus(DISABLED).build()
        );
        Map<String, AuthorityStatus> existingUserStatuses = Map.of(
                uerId1, DISABLED,
                uerId2, ACCEPTED
        );

        when(authorityRepository.findStatusByUsersAndAccountId(List.of(uerId1, uerId2), accountId))
                .thenReturn(existingUserStatuses);

        BusinessException businessException = assertThrows(BusinessException.class, () ->
                validator.validateUpdate(accountOperatorAuthorityUpdates, accountId));

        assertThat(businessException.getErrorCode()).isEqualTo(ErrorCode.AUTHORITY_INVALID_STATUS);

        verify(authorityRepository, times(1))
                .findStatusByUsersAndAccountId(List.of(uerId1, uerId2), accountId);
    }
}
