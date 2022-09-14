package uk.gov.pmrv.api.account.service.validator;

import java.util.Map;
import uk.gov.pmrv.api.account.domain.enumeration.AccountContactType;

public interface AccountContactTypeDeleteValidator {

    void validateDelete(Map<AccountContactType, String> contactTypes);
}
