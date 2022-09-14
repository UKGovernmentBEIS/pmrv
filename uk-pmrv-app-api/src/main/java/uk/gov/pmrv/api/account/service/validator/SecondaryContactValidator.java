package uk.gov.pmrv.api.account.service.validator;

import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.account.domain.enumeration.AccountContactType;
import uk.gov.pmrv.api.authorization.AuthorityConstants;
import uk.gov.pmrv.api.authorization.core.domain.dto.AuthorityInfoDTO;
import uk.gov.pmrv.api.authorization.core.service.AuthorityService;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;

@Service
@RequiredArgsConstructor
public class SecondaryContactValidator implements AccountContactTypeUpdateValidator {

    private final AuthorityService authorityService;

    @Override
    public void validateUpdate(Map<AccountContactType, String> contactTypes, Long accountId) {
        String userId = contactTypes.get(AccountContactType.SECONDARY);
        if (userId != null) {
            Optional<AuthorityInfoDTO> userAccountAuthorityOptional =
                authorityService.findAuthorityByUserIdAndAccountId(userId, accountId);

            userAccountAuthorityOptional.ifPresent(userAccountAuthority -> {
                if(AuthorityConstants.EMITTER_CONTACT.equals(userAccountAuthority.getCode())) {
                    throw new BusinessException(ErrorCode.ACCOUNT_CONTACT_TYPE_SECONDARY_CONTACT_NOT_EMITTER_CONTACT);
                }
            });
        }
    }
}
