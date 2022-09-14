package uk.gov.pmrv.api.authorization.rules.services.handlers;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.authorization.rules.domain.AuthorizationRuleScopePermission;
import uk.gov.pmrv.api.authorization.rules.services.AuthorizationResourceRuleHandler;
import uk.gov.pmrv.api.authorization.rules.services.authorityinfo.providers.PermitAuthorityInfoProvider;
import uk.gov.pmrv.api.authorization.rules.services.authorization.AuthorizationCriteria;
import uk.gov.pmrv.api.authorization.rules.services.authorization.PmrvAuthorizationService;
import uk.gov.pmrv.api.common.domain.model.PmrvUser;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;

import javax.validation.Valid;
import java.util.Set;

@Service("permitAccessHandler")
@RequiredArgsConstructor
public class PermitAccessRuleHandler implements AuthorizationResourceRuleHandler {
    private final PmrvAuthorizationService pmrvAuthorizationService;
    private final PermitAuthorityInfoProvider permitAuthorityInfoProvider;

    /**
     * @param user the authenticated user
     * @param authorizationRules the list of
     * @param resourceId the resourceId for which the rules apply.
     * @throws BusinessException {@link ErrorCode} FORBIDDEN if authorization fails.
     *
     * Authorizes access on {@link uk.gov.pmrv.api.permit.domain.PermitEntity}
     * with id the {@code resourceId} and permission the {@link uk.gov.pmrv.api.authorization.core.domain.Permission} of the rule
     */
    @Override
    public void evaluateRules(@Valid Set<AuthorizationRuleScopePermission> authorizationRules, PmrvUser user, String resourceId) {
        Long accountId = permitAuthorityInfoProvider.getPermitAccountById(resourceId);

        authorizationRules.forEach(rule -> {
            AuthorizationCriteria authorizationCriteria = AuthorizationCriteria.builder()
                    .accountId(accountId)
                    .permission(rule.getPermission())
                    .build();
            pmrvAuthorizationService.authorize(user, authorizationCriteria);
        });
    }

}
