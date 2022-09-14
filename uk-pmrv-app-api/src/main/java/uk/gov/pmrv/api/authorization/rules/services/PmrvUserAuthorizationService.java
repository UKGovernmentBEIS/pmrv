package uk.gov.pmrv.api.authorization.rules.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.common.domain.model.PmrvUser;

@Service
@RequiredArgsConstructor
public class PmrvUserAuthorizationService {

    private final AuthorizationRulesService authorizationRulesService;

    public void authorize(PmrvUser pmrvUser, String serviceName) {
        authorizationRulesService.evaluateRules(pmrvUser, serviceName);
    }

    public void authorize(PmrvUser pmrvUser, String serviceName, String resourceId) {
        authorizationRulesService.evaluateRules(pmrvUser, serviceName, resourceId);
    }

    public void authorize(PmrvUser pmrvUser, String serviceName, String resourceId, String resourceSubType) {
        authorizationRulesService.evaluateRules(pmrvUser, serviceName, resourceId, resourceSubType);
    }
}
