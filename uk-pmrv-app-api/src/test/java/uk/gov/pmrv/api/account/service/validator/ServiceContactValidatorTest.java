package uk.gov.pmrv.api.account.service.validator;

import org.junit.jupiter.api.Test;
import uk.gov.pmrv.api.account.domain.enumeration.AccountContactType;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ServiceContactValidatorTest {

    private final ServiceContactValidator validator = new ServiceContactValidator();
    
    @Test
    void validateDelete_no_exception() {
        Map<AccountContactType, String> contactTypes = Map.of(
                AccountContactType.SERVICE, "user"
                );
        
        validator.validateDelete(contactTypes);
    }
    
    @Test
    void validateDelete_service_type_not_exist() {
        Map<AccountContactType, String> contactTypes = Map.of(
                AccountContactType.PRIMARY, "user"
                );
        
        BusinessException ex = assertThrows(BusinessException.class, () -> {
            validator.validateDelete(contactTypes);
        });
        assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.ACCOUNT_CONTACT_TYPE_SERVICE_CONTACT_IS_REQUIRED);
    }

    @Test
    void validateUpdate() {
        Long accountId = 1L;
        Map<AccountContactType, String> contactTypes = Map.of(
            AccountContactType.SERVICE, "user"
        );

        validator.validateUpdate(contactTypes, accountId);
    }
    
}
