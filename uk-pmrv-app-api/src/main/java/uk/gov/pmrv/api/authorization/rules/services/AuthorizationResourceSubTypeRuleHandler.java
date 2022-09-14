package uk.gov.pmrv.api.authorization.rules.services;

import uk.gov.pmrv.api.authorization.rules.domain.AuthorizationRuleScopePermission;
import uk.gov.pmrv.api.common.domain.model.PmrvUser;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;

import java.util.Set;

public interface AuthorizationResourceSubTypeRuleHandler {
    /**
     * Evaluates the {@code authorizationRules}.
     *
     * @param authorizationRules the list of {@link AuthorizationRuleScopePermission}
     * @param user the authenticated user
     * @param resourceId the resourceId for which the rules apply.
     * @param resourceSubType the resource's object sub type.
     * @throws BusinessException {@link ErrorCode} FORBIDDEN if authorization fails.
     */
    void evaluateRules(Set<AuthorizationRuleScopePermission> authorizationRules, PmrvUser user, String resourceId, String resourceSubType);
}
