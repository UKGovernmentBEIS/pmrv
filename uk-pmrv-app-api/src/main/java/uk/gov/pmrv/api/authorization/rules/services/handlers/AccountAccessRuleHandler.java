package uk.gov.pmrv.api.authorization.rules.services.handlers;

import java.util.Set;

import javax.validation.Valid;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import uk.gov.pmrv.api.authorization.rules.domain.AuthorizationRuleScopePermission;
import uk.gov.pmrv.api.authorization.rules.services.AuthorizationResourceRuleHandler;
import uk.gov.pmrv.api.authorization.rules.services.authorization.AuthorizationCriteria;
import uk.gov.pmrv.api.authorization.rules.services.authorization.PmrvAuthorizationService;
import uk.gov.pmrv.api.common.domain.model.PmrvUser;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;

@Service("accountAccessHandler")
@RequiredArgsConstructor
public class AccountAccessRuleHandler implements AuthorizationResourceRuleHandler {
    private final PmrvAuthorizationService pmrvAuthorizationService;

    /**
     * @param user the authenticated user
     * @param authorizationRules the list of
     * @param resourceId the resourceId for which the rules apply.
     * @throws BusinessException {@link ErrorCode} FORBIDDEN if authorization fails.
     *
     * Authorizes access on {@link uk.gov.pmrv.api.account.domain.Account}
     * with id the {@code resourceId} and permission the {@link uk.gov.pmrv.api.authorization.core.domain.Permission} of the rule
     */
    @Override
    public void evaluateRules(@Valid Set<AuthorizationRuleScopePermission> authorizationRules, PmrvUser user, String resourceId) {
        authorizationRules.forEach(rule -> {
            AuthorizationCriteria authorizationCriteria = AuthorizationCriteria.builder()
                    .accountId(Long.parseLong(resourceId))
                    .permission(rule.getPermission())
                    .build();
            pmrvAuthorizationService.authorize(user, authorizationCriteria);
        });
    }

}
