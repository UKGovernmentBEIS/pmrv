package uk.gov.pmrv.api.account.service.validator;

import java.util.Map;

import uk.gov.pmrv.api.account.domain.enumeration.AccountContactType;

public interface AccountContactTypeUpdateValidator {
    
    void validateUpdate(Map<AccountContactType, String> contactTypes, Long accountId);
}
