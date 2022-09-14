package uk.gov.pmrv.api.account.service.validator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Map;
import org.junit.jupiter.api.Test;
import uk.gov.pmrv.api.account.domain.enumeration.AccountContactType;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;

class PrimaryAndSecondaryContactNotIdenticalValidatorTest {

    private final PrimaryAndSecondaryContactNotIdenticalValidator validator = new PrimaryAndSecondaryContactNotIdenticalValidator();
    
    @Test
    void validateUpdate_no_exception() {
        Long accountId = 1L;
        Map<AccountContactType, String> contactTypes = Map.of(
                AccountContactType.PRIMARY, "user1",
                AccountContactType.SECONDARY, "user2"
                );
        
        validator.validateUpdate(contactTypes, accountId);
    }
    
    @Test
    void validateUpdate_not_provided() {
        Long accountId = 1L;
        Map<AccountContactType, String> contactTypes = Map.of(
                AccountContactType.FINANCIAL, "user"
                );
        
        validator.validateUpdate(contactTypes, accountId);
    }
    
    @Test
    void validateUpdate_not_both_provided() {
        Long accountId = 1L;
        Map<AccountContactType, String> contactTypes = Map.of(
                AccountContactType.PRIMARY, "user"
                );
        
        validator.validateUpdate(contactTypes, accountId);
    }
    
    @Test
    void validateUpdate_identical() {
        Long accountId = 1L;
        Map<AccountContactType, String> contactTypes = Map.of(
                AccountContactType.PRIMARY, "user",
                AccountContactType.SECONDARY, "user"
                );
        
        BusinessException ex = assertThrows(BusinessException.class, () -> {
            validator.validateUpdate(contactTypes, accountId);
        });
        assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.ACCOUNT_CONTACT_TYPE_PRIMARY_AND_SECONDARY_CONTACT_ARE_IDENTICAL);
    }
}
