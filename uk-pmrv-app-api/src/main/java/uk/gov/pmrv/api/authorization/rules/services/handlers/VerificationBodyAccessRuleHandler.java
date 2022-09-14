package uk.gov.pmrv.api.authorization.rules.services.handlers;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.authorization.rules.domain.AuthorizationRuleScopePermission;
import uk.gov.pmrv.api.authorization.rules.services.AuthorizationRuleHandler;
import uk.gov.pmrv.api.authorization.rules.services.authorization.AuthorizationCriteria;
import uk.gov.pmrv.api.authorization.rules.services.authorization.PmrvAuthorizationService;
import uk.gov.pmrv.api.common.domain.model.PmrvUser;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;

import java.util.Optional;
import java.util.Set;

@Service("verificationBodyAccessHandler")
@RequiredArgsConstructor
public class VerificationBodyAccessRuleHandler implements AuthorizationRuleHandler {

    private final PmrvAuthorizationService pmrvAuthorizationService;

    @Override
    public void evaluateRules(Set<AuthorizationRuleScopePermission> authorizationRules, PmrvUser user) {
        final Long userVerificationBodyId = Optional.ofNullable(user.getVerificationBodyId())
                .orElseThrow(() -> new BusinessException(ErrorCode.FORBIDDEN));

        authorizationRules.forEach(rule -> {
            AuthorizationCriteria authorizationCriteria = AuthorizationCriteria.builder()
                    .verificationBodyId(userVerificationBodyId)
                    .permission(rule.getPermission())
                    .build();
            pmrvAuthorizationService.authorize(user, authorizationCriteria);
        });
    }
}
