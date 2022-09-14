package uk.gov.pmrv.api.authorization.rules.services.handlers;

import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.authorization.rules.domain.AuthorizationRuleScopePermission;
import uk.gov.pmrv.api.authorization.rules.services.AuthorizationResourceRuleHandler;
import uk.gov.pmrv.api.authorization.rules.services.authorityinfo.providers.DocumentTemplateAuthorityInfoProvider;
import uk.gov.pmrv.api.authorization.rules.services.authorization.AuthorizationCriteria;
import uk.gov.pmrv.api.authorization.rules.services.authorization.PmrvAuthorizationService;
import uk.gov.pmrv.api.common.domain.enumeration.CompetentAuthority;
import uk.gov.pmrv.api.common.domain.model.PmrvUser;

@Service("documentTemplateAccessHandler")
@RequiredArgsConstructor
public class DocumentTemplateAccessRuleHandler implements AuthorizationResourceRuleHandler {

    private final DocumentTemplateAuthorityInfoProvider templateAuthorityInfoProvider;
    private final PmrvAuthorizationService pmrvAuthorizationService;

    @Override
    public void evaluateRules(Set<AuthorizationRuleScopePermission> authorizationRules, PmrvUser user,
                              String resourceId) {

        CompetentAuthority competentAuthority = templateAuthorityInfoProvider.getDocumentTemplateCaById(Long.parseLong(resourceId));

        authorizationRules.forEach(rule -> {
            AuthorizationCriteria authorizationCriteria = AuthorizationCriteria.builder()
                .competentAuthority(competentAuthority)
                .build();
            pmrvAuthorizationService.authorize(user, authorizationCriteria);
        });
    }
}
