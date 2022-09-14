package uk.gov.pmrv.api.authorization.operator.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.authorization.core.domain.Authority;
import uk.gov.pmrv.api.authorization.core.repository.AuthorityRepository;
import uk.gov.pmrv.api.authorization.operator.domain.AccountOperatorAuthorityUpdateDTO;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static uk.gov.pmrv.api.authorization.AuthorityConstants.CONSULTANT_AGENT;
import static uk.gov.pmrv.api.authorization.AuthorityConstants.EMITTER_CONTACT;
import static uk.gov.pmrv.api.authorization.AuthorityConstants.OPERATOR_ADMIN_ROLE_CODE;
import static uk.gov.pmrv.api.authorization.core.domain.AuthorityStatus.ACTIVE;
import static uk.gov.pmrv.api.authorization.core.domain.AuthorityStatus.DISABLED;

@ExtendWith(MockitoExtension.class)
class OperatorRoleModificationAllowanceValidatorTest {

    @InjectMocks
    private OperatorRoleModificationAllowanceValidator operatorRoleModificationAllowanceValidator;

    @Mock
    private AuthorityRepository authorityRepository;

    @Test
    void validate() {
        Long accountId = 1L;
        String operatorAdminUser = "operatorAdminUser";
        String operatorConsultantUser = "operatorConsultantUser";

        List<AccountOperatorAuthorityUpdateDTO> accountOperatorAuthorityUpdates = List.of(
            AccountOperatorAuthorityUpdateDTO.builder().userId(operatorAdminUser).roleCode(OPERATOR_ADMIN_ROLE_CODE).authorityStatus(ACTIVE)
                .build(),
            AccountOperatorAuthorityUpdateDTO.builder().userId(operatorConsultantUser).roleCode(CONSULTANT_AGENT).authorityStatus(ACTIVE)
                .build()
        );

        List<Authority> existingAccountOperatorAuthorities = List.of(
            Authority.builder().userId(operatorConsultantUser).code(CONSULTANT_AGENT).status(DISABLED).build()
        );

        when(authorityRepository.findByAccountIdAndCodeIn(accountId, List.of(CONSULTANT_AGENT, EMITTER_CONTACT)))
            .thenReturn(existingAccountOperatorAuthorities);

        operatorRoleModificationAllowanceValidator.validateUpdate(accountOperatorAuthorityUpdates, accountId);

        verify(authorityRepository, times(1)).findByAccountIdAndCodeIn(accountId, List.of(CONSULTANT_AGENT,
            EMITTER_CONTACT));
    }

    @Test
    void validate_no_agents_or_consultants_to_be_updated() {
        Long accountId = 1L;
        List<AccountOperatorAuthorityUpdateDTO> accountOperatorAuthorityUpdates = List.of(
            AccountOperatorAuthorityUpdateDTO.builder().userId("user1").roleCode(OPERATOR_ADMIN_ROLE_CODE).authorityStatus(ACTIVE)
                .build(),
            AccountOperatorAuthorityUpdateDTO.builder().userId("user2").roleCode(OPERATOR_ADMIN_ROLE_CODE).authorityStatus(ACTIVE)
                .build()
        );

        operatorRoleModificationAllowanceValidator.validateUpdate(accountOperatorAuthorityUpdates, accountId);

        verifyNoInteractions(authorityRepository);
    }

    @Test
    void validate_throws_business_exception() {
        Long accountId = 1L;
        String operatorAdminUser = "operatorAdminUser";
        String operatorConsultantUser = "operatorConsultantUser";

        List<AccountOperatorAuthorityUpdateDTO> accountOperatorAuthorityUpdates = List.of(
            AccountOperatorAuthorityUpdateDTO.builder().userId(operatorAdminUser).roleCode(OPERATOR_ADMIN_ROLE_CODE).authorityStatus(ACTIVE)
                .build(),
            AccountOperatorAuthorityUpdateDTO.builder().userId(operatorConsultantUser).roleCode(CONSULTANT_AGENT).authorityStatus(ACTIVE)
                .build()
        );

        List<Authority> existingAccountOperatorAuthorities = List.of(
            Authority.builder().userId(operatorConsultantUser).code(EMITTER_CONTACT).status(ACTIVE).build()
        );

        when(authorityRepository.findByAccountIdAndCodeIn(accountId, List.of(CONSULTANT_AGENT, EMITTER_CONTACT)))
            .thenReturn(existingAccountOperatorAuthorities);

        BusinessException businessException =
            assertThrows(BusinessException.class, () -> operatorRoleModificationAllowanceValidator
                .validateUpdate(accountOperatorAuthorityUpdates, accountId));

        assertThat(businessException.getErrorCode()).isEqualTo(ErrorCode.AUTHORITY_USER_ROLE_MODIFICATION_NOT_ALLOWED);

        verify(authorityRepository, times(1)).findByAccountIdAndCodeIn(accountId, List.of(CONSULTANT_AGENT,
            EMITTER_CONTACT));
    }
}