package uk.gov.pmrv.api.authorization.rules.services.handlers;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.authorization.rules.domain.AuthorizationRuleScopePermission;
import uk.gov.pmrv.api.authorization.rules.services.AuthorizationResourceRuleHandler;
import uk.gov.pmrv.api.authorization.rules.services.authorityinfo.dto.RequestAuthorityInfoDTO;
import uk.gov.pmrv.api.authorization.rules.services.authorityinfo.providers.RequestAuthorityInfoProvider;
import uk.gov.pmrv.api.authorization.rules.services.authorization.AuthorizationCriteria;
import uk.gov.pmrv.api.authorization.rules.services.authorization.PmrvAuthorizationService;
import uk.gov.pmrv.api.common.domain.model.PmrvUser;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;

import javax.validation.Valid;
import java.util.Set;

@Service("requestAccessHandler")
@RequiredArgsConstructor
public class RequestAccessRuleHandler implements AuthorizationResourceRuleHandler {
    private final PmrvAuthorizationService pmrvAuthorizationService;
    private final RequestAuthorityInfoProvider requestAuthorityInfoProvider;

    /**
     * @param user the authenticated user
     * @param authorizationRules the list of
     * @param resourceId the resourceId for which the rules apply.
     * @throws BusinessException {@link ErrorCode} FORBIDDEN if authorization fails.
     *
     * Authorizes access on {@link uk.gov.pmrv.api.account.domain.Account} or {@link uk.gov.pmrv.api.common.domain.enumeration.CompetentAuthority}
     * of {@link uk.gov.pmrv.api.workflow.request.core.domain.Request} with id the {@code resourceId}
     * and permission the {@link uk.gov.pmrv.api.authorization.core.domain.Permission} of the rule
     */
    @Override
    public void evaluateRules(@Valid Set<AuthorizationRuleScopePermission> authorizationRules, PmrvUser user, String resourceId) {
        RequestAuthorityInfoDTO requestInfoDTO = requestAuthorityInfoProvider.getRequestInfo(resourceId);
        authorizationRules.forEach(rule -> {
            AuthorizationCriteria authorizationCriteria = AuthorizationCriteria.builder()
                    .accountId(requestInfoDTO.getAuthorityInfo().getAccountId())
                    .competentAuthority(requestInfoDTO.getAuthorityInfo().getCompetentAuthority())
                    .verificationBodyId(requestInfoDTO.getAuthorityInfo().getVerificationBodyId())
                    .permission(rule.getPermission())
                    .build();
            pmrvAuthorizationService.authorize(user, authorizationCriteria);
        });
    }
}
