package uk.gov.pmrv.api.authorization.operator.service;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Component;
import uk.gov.pmrv.api.authorization.core.domain.Authority;
import uk.gov.pmrv.api.authorization.operator.domain.AccountOperatorAuthorityUpdateDTO;
import uk.gov.pmrv.api.authorization.core.repository.AuthorityRepository;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static uk.gov.pmrv.api.authorization.AuthorityConstants.CONSULTANT_AGENT;
import static uk.gov.pmrv.api.authorization.AuthorityConstants.EMITTER_CONTACT;

@Component
@RequiredArgsConstructor
public class OperatorRoleModificationAllowanceValidator implements OperatorAuthorityUpdateValidator {

    private final AuthorityRepository authorityRepository;

    private static final List<String> IMMUTABLE_ROLE_CODES = List.of(CONSULTANT_AGENT, EMITTER_CONTACT);

    /**
     * Checks that existing operator users of the {@code accountId } with specific role codes do not change roles.
     * @param accountOperatorAuthorities accountOperatorAuthorities {@link List} of {@link AccountOperatorAuthorityUpdateDTO}
     * @param accountId the account id
     */
    @Override
    public void validateUpdate(List<AccountOperatorAuthorityUpdateDTO> accountOperatorAuthorities, Long accountId) {

        List<AccountOperatorAuthorityUpdateDTO> authorityUpdatesForImmutableRoleCodes = accountOperatorAuthorities.stream()
            .filter(accountOperatorAuthority -> IMMUTABLE_ROLE_CODES.contains(accountOperatorAuthority.getRoleCode()))
            .collect(Collectors.toList());

        if(!ObjectUtils.isEmpty(authorityUpdatesForImmutableRoleCodes)) {
            Map<String, String> immutableRoleCodes = getImmutableUsers(accountId);

            authorityUpdatesForImmutableRoleCodes.forEach(authorityUpdate -> {
                String userId = authorityUpdate.getUserId();
                String updatedRoleCode = authorityUpdate.getRoleCode();

                if(immutableRoleCodes.containsKey(userId) &&
                    !immutableRoleCodes.get(userId).equals(updatedRoleCode)) {
                    throw new BusinessException(ErrorCode.AUTHORITY_USER_ROLE_MODIFICATION_NOT_ALLOWED, userId, updatedRoleCode);
                }
            });
        }
    }

    private Map<String, String> getImmutableUsers(Long accountId) {
        return authorityRepository.findByAccountIdAndCodeIn(accountId, IMMUTABLE_ROLE_CODES).stream()
            .collect(Collectors.toMap(Authority::getUserId, Authority::getCode));
    }
}
