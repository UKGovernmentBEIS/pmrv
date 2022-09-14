package uk.gov.pmrv.api.authorization.rules.services.handlers;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.authorization.rules.domain.AuthorizationRuleScopePermission;
import uk.gov.pmrv.api.authorization.rules.services.AuthorizationResourceRuleHandler;
import uk.gov.pmrv.api.authorization.rules.services.authorityinfo.dto.RequestTaskAuthorityInfoDTO;
import uk.gov.pmrv.api.authorization.rules.services.authorityinfo.providers.RequestTaskAuthorityInfoProvider;
import uk.gov.pmrv.api.authorization.rules.services.authorization.AuthorizationCriteria;
import uk.gov.pmrv.api.authorization.rules.services.authorization.PmrvAuthorizationService;
import uk.gov.pmrv.api.common.domain.model.PmrvUser;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;

import javax.validation.Valid;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service("requestTaskAccessHandler")
@RequiredArgsConstructor
public class RequestTaskAccessRuleHandler implements AuthorizationResourceRuleHandler {
    private final PmrvAuthorizationService pmrvAuthorizationService;
    private final RequestTaskAuthorityInfoProvider requestTaskAuthorityInfoProvider;

    /**
     * @param user the authenticated user
     * @param authorizationRules the list of
     * @param resourceId the resourceId for which the rules apply.
     * @throws BusinessException {@link ErrorCode} FORBIDDEN if authorization fails.
     *
     * Authorizes access on {@link uk.gov.pmrv.api.account.domain.Account} or {@link uk.gov.pmrv.api.common.domain.enumeration.CompetentAuthority},
     * the {@link uk.gov.pmrv.api.workflow.request.core.domain.Request} of {@link uk.gov.pmrv.api.workflow.request.core.domain.RequestTask} with id the {@code resourceId}
     * and permission the {@link uk.gov.pmrv.api.authorization.core.domain.Permission} of the rule
     */
    @Override
    public void evaluateRules(@Valid Set<AuthorizationRuleScopePermission> authorizationRules, PmrvUser user, String resourceId) {
        RequestTaskAuthorityInfoDTO requestTaskInfoDTO = requestTaskAuthorityInfoProvider.getRequestTaskInfo(Long.parseLong(resourceId));

        List<AuthorizationRuleScopePermission> filteredRules = authorizationRules.stream()
                .filter(rule -> requestTaskInfoDTO.getType().equals(rule.getResourceSubType()))
                .collect(Collectors.toList());

        if (filteredRules.isEmpty()) {
            throw new BusinessException(ErrorCode.FORBIDDEN);
        }

        if("SYSTEM_MESSAGE_NOTIFICATION".equals(requestTaskInfoDTO.getRequestType())
            && !requestTaskInfoDTO.getAssignee().equals(user.getUserId())){
            throw new BusinessException(ErrorCode.FORBIDDEN);
        }

        filteredRules.forEach(rule -> {
            AuthorizationCriteria authorizationCriteria = AuthorizationCriteria.builder()
                    .accountId(requestTaskInfoDTO.getAuthorityInfo().getAccountId())
                    .competentAuthority(requestTaskInfoDTO.getAuthorityInfo().getCompetentAuthority())
                    .verificationBodyId(requestTaskInfoDTO.getAuthorityInfo().getVerificationBodyId())
                    .permission(rule.getPermission()).build();
            pmrvAuthorizationService.authorize(user, authorizationCriteria);
        });
    }
}
