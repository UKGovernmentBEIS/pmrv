package uk.gov.pmrv.api.account.service.validator;

import java.util.Map;
import org.springframework.stereotype.Component;
import uk.gov.pmrv.api.account.domain.enumeration.AccountContactType;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;

@Component
public class ServiceContactValidator implements AccountContactTypeUpdateValidator, AccountContactTypeDeleteValidator {

    @Override
    public void validateUpdate(Map<AccountContactType, String> contactTypes, Long accountId) {
        validateServiceContactExists(contactTypes);
    }

    @Override
    public void validateDelete(Map<AccountContactType, String> contactTypes) {
        validateServiceContactExists(contactTypes);
    }

    private void validateServiceContactExists(Map<AccountContactType, String> contactTypes) {
        String userId = contactTypes.get(AccountContactType.SERVICE);
        if(userId == null) {
            throw new BusinessException(ErrorCode.ACCOUNT_CONTACT_TYPE_SERVICE_CONTACT_IS_REQUIRED);
        }
    }
}
