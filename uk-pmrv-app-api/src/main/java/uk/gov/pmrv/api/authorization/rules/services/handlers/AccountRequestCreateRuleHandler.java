package uk.gov.pmrv.api.authorization.rules.services.handlers;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.authorization.rules.domain.AuthorizationRuleScopePermission;
import uk.gov.pmrv.api.authorization.rules.services.AuthorizationResourceSubTypeRuleHandler;
import uk.gov.pmrv.api.authorization.rules.services.authorization.AuthorizationCriteria;
import uk.gov.pmrv.api.authorization.rules.services.authorization.PmrvAuthorizationService;
import uk.gov.pmrv.api.common.domain.model.PmrvUser;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service("accountRequestCreateHandler")
@RequiredArgsConstructor
public class AccountRequestCreateRuleHandler implements AuthorizationResourceSubTypeRuleHandler {
    
    private final PmrvAuthorizationService pmrvAuthorizationService;
    
    @Override
    public void evaluateRules(Set<AuthorizationRuleScopePermission> authorizationRules, PmrvUser user,
            String resourceId, String resourceSubType) {

        List<AuthorizationRuleScopePermission> subTypeAppliedRules = 
                authorizationRules.stream()
                .filter(rule -> resourceSubType.equals(rule.getResourceSubType()))
                .collect(Collectors.toList());
        
        if (subTypeAppliedRules.isEmpty()) {
            throw new BusinessException(ErrorCode.FORBIDDEN);
        }

        if (resourceId != null) {
            subTypeAppliedRules.forEach(rule -> pmrvAuthorizationService.authorize(user,
                    AuthorizationCriteria.builder().accountId(Long.valueOf(resourceId)).permission(rule.getPermission()).build()));
        }
    }
}
