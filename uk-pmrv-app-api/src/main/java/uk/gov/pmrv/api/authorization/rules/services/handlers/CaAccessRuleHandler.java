package uk.gov.pmrv.api.authorization.rules.services.handlers;

import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.authorization.rules.domain.AuthorizationRuleScopePermission;
import uk.gov.pmrv.api.authorization.rules.services.AuthorizationRuleHandler;
import uk.gov.pmrv.api.authorization.rules.services.authorization.AuthorizationCriteria;
import uk.gov.pmrv.api.authorization.rules.services.authorization.PmrvAuthorizationService;
import uk.gov.pmrv.api.common.domain.model.PmrvUser;

@Service("caAccessHandler")
@RequiredArgsConstructor
public class CaAccessRuleHandler implements AuthorizationRuleHandler {

    private final PmrvAuthorizationService pmrvAuthorizationService;

    @Override
    public void evaluateRules(Set<AuthorizationRuleScopePermission> authorizationRules, PmrvUser user) {

        authorizationRules.forEach(rule -> {
            AuthorizationCriteria authorizationCriteria = AuthorizationCriteria.builder()
                    .permission(rule.getPermission())
                    .competentAuthority(user.getCompetentAuthority())
                    .build();
            pmrvAuthorizationService.authorize(user, authorizationCriteria);
        });
    }
}
