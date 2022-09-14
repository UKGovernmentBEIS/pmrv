package uk.gov.pmrv.api.authorization.operator.service;

import uk.gov.pmrv.api.authorization.operator.domain.AccountOperatorAuthorityUpdateDTO;
import uk.gov.pmrv.api.common.exception.BusinessException;

import java.util.List;

public interface OperatorAuthorityUpdateValidator {

    /**
     * Validates the {@code accountOperatorUsers} for the given {@code accountId}.
     * @param accountOperatorAuthorities accountOperatorAuthorities {@link List} of {@link AccountOperatorAuthorityUpdateDTO}
     * @param accountId the account id
     * @throws BusinessException throws {@link BusinessException}
     */
    void validateUpdate(List<AccountOperatorAuthorityUpdateDTO> accountOperatorAuthorities, Long accountId);
}
