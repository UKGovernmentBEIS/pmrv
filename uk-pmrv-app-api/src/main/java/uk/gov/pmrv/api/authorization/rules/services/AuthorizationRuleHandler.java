package uk.gov.pmrv.api.authorization.rules.services;

import java.util.Set;
import uk.gov.pmrv.api.authorization.rules.domain.AuthorizationRuleScopePermission;
import uk.gov.pmrv.api.common.domain.model.PmrvUser;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;

public interface AuthorizationRuleHandler {

    /**
     * Evaluates the {@code authorizationRules}.
     * @param authorizationRules the list of {@link AuthorizationRuleScopePermission}
     * @param user the authenticated user
     * @throws BusinessException {@link ErrorCode} FORBIDDEN if authorization fails.
     */
    void evaluateRules(Set<AuthorizationRuleScopePermission> authorizationRules, PmrvUser user);
}
