package uk.gov.pmrv.api.authorization.rules.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.authorization.rules.domain.AuthorizationRuleScopePermission;
import uk.gov.pmrv.api.authorization.rules.repository.AuthorizationRuleRepository;
import uk.gov.pmrv.api.common.domain.model.PmrvUser;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
class AuthorizationRulesService {

    private final AuthorizationRuleRepository authorizationRuleRepository;
    private final Map<String, AuthorizationResourceRuleHandler> authorizationResourceRuleHandlers;
    private final Map<String, AuthorizationResourceSubTypeRuleHandler> authorizationResourceSubTypeRuleHandlers;
    private final Map<String, AuthorizationRuleHandler> authorizationRuleHandlers;

    /**
     * Fetches the rules of the service,
     * filters based on the user's {@link uk.gov.pmrv.api.common.domain.enumeration.RoleType}
     * groups the rules based on the {@link AuthorizationResourceRuleHandler} name
     * and triggers evaluation of rules for all groups.
     *
     * @param user the authenticated user
     * @param service the service name to run rules for.
     * @param resourceId the resourceId for which the rules apply.
     * @throws BusinessException {@link ErrorCode} FORBIDDEN if authorization fails.
     */
    public void evaluateRules(PmrvUser user, String service, String resourceId) {
        Map<String, Set<AuthorizationRuleScopePermission>> rules = getAuthorizationServiceRules(user, service);
        rules.forEach((key, value) -> authorizationResourceRuleHandlers.get(key).evaluateRules(value, user, resourceId));
    }

    /**
     * Fetches the rules of the service,
     * filters based on the user's {@link uk.gov.pmrv.api.common.domain.enumeration.RoleType}
     * groups the rules based on the {@link AuthorizationRuleHandler} name
     * and triggers evaluation of rules for all groups.
     *
     * @param user the authenticated user
     * @param service the service name to run rules for.
     * @throws BusinessException {@link ErrorCode} FORBIDDEN if authorization fails.
     */
    public void evaluateRules(PmrvUser user, String service) {
        Map<String, Set<AuthorizationRuleScopePermission>> rules = getAuthorizationServiceRules(user, service);
        rules.forEach((key, value) -> authorizationRuleHandlers.get(key).evaluateRules(value, user));
    }

    public void evaluateRules(PmrvUser user, String service, String resourceId, String resourceSubType) {
        Map<String, Set<AuthorizationRuleScopePermission>> rules = getAuthorizationServiceRules(user, service);
        rules.forEach((key, value) -> authorizationResourceSubTypeRuleHandlers.get(key).evaluateRules(value, user, resourceId, resourceSubType));
    }

    private Map<String, Set<AuthorizationRuleScopePermission>> getAuthorizationServiceRules(PmrvUser user, String service) {
        List<AuthorizationRuleScopePermission> rules = 
                authorizationRuleRepository.findRulePermissionsByServiceAndRoleType(service, user.getRoleType());

        if(rules.isEmpty()) {
            throw new BusinessException(ErrorCode.FORBIDDEN);
        }

        return rules
                .stream()
                .collect(Collectors.groupingBy(AuthorizationRuleScopePermission::getHandler, Collectors.toSet()));
    }

}
