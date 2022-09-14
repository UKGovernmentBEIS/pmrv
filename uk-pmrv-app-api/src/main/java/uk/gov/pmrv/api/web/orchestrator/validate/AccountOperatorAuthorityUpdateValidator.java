package uk.gov.pmrv.api.web.orchestrator.validate;

import org.springframework.util.ObjectUtils;
import uk.gov.pmrv.api.web.orchestrator.dto.AccountOperatorAuthorityUpdateWrapperDTO;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class AccountOperatorAuthorityUpdateValidator 
            implements ConstraintValidator<AccountOperatorAuthorityUpdate, AccountOperatorAuthorityUpdateWrapperDTO>{

    @Override
    public boolean isValid(AccountOperatorAuthorityUpdateWrapperDTO dto, ConstraintValidatorContext context) {
        return ! (
                ObjectUtils.isEmpty(dto.getAccountOperatorAuthorityUpdateList()) &&
                ObjectUtils.isEmpty(dto.getContactTypes())
                );
    }

}
