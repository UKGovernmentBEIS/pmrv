package uk.gov.pmrv.api.authorization.rules.services.handlers;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.authorization.core.domain.Permission;
import uk.gov.pmrv.api.authorization.rules.domain.AuthorizationRuleScopePermission;
import uk.gov.pmrv.api.authorization.rules.services.authorityinfo.dto.RequestTaskAuthorityInfoDTO;
import uk.gov.pmrv.api.authorization.rules.services.authorityinfo.dto.ResourceAuthorityInfo;
import uk.gov.pmrv.api.authorization.rules.services.authorityinfo.providers.RequestTaskAuthorityInfoProvider;
import uk.gov.pmrv.api.authorization.rules.services.authorization.AuthorizationCriteria;
import uk.gov.pmrv.api.authorization.rules.services.authorization.PmrvAuthorizationService;
import uk.gov.pmrv.api.common.domain.enumeration.RoleType;
import uk.gov.pmrv.api.common.domain.model.PmrvUser;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static uk.gov.pmrv.api.common.domain.enumeration.CompetentAuthority.ENGLAND;
import static uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType.ACCOUNT_USERS_SETUP;

@ExtendWith(MockitoExtension.class)
class RequestTaskAssignAccessRuleHandlerTest {

    @InjectMocks
    private RequestTaskAssignAccessRuleHandler requestTaskAssignAccessRuleHandler;

    @Mock
    private PmrvAuthorizationService pmrvAuthorizationService;

    @Mock
    private RequestTaskAuthorityInfoProvider requestTaskAuthorityInfoProvider;

    @Test
    void evaluateRules() {
        PmrvUser user = PmrvUser.builder().userId("userId").roleType(RoleType.OPERATOR).build();
        Long requestTaskId = 1L;
        AuthorizationRuleScopePermission authorizationRule1 = 
                AuthorizationRuleScopePermission.builder()
            .permission(Permission.PERM_TASK_ASSIGNMENT)
            .build();

        RequestTaskAuthorityInfoDTO requestTaskInfoDTO = RequestTaskAuthorityInfoDTO.builder()
            .type(ACCOUNT_USERS_SETUP.name())
            .assignee(user.getUserId())
            .authorityInfo(ResourceAuthorityInfo.builder()
                    .accountId(1L)
                    .competentAuthority(ENGLAND)
                    .verificationBodyId(1L).build())
            .build();
        when(requestTaskAuthorityInfoProvider.getRequestTaskInfo(requestTaskId)).thenReturn(requestTaskInfoDTO);

        Set<AuthorizationRuleScopePermission> rules = Set.of(authorizationRule1);
        AuthorizationCriteria authorizationCriteria1 = AuthorizationCriteria.builder()
            .accountId(requestTaskInfoDTO.getAuthorityInfo().getAccountId())
            .competentAuthority(requestTaskInfoDTO.getAuthorityInfo().getCompetentAuthority())
            .verificationBodyId(requestTaskInfoDTO.getAuthorityInfo().getVerificationBodyId())
            .permission(Permission.PERM_TASK_ASSIGNMENT)
            .build();
        requestTaskAssignAccessRuleHandler.evaluateRules(rules, user, requestTaskId.toString());

        verify(pmrvAuthorizationService, times(1)).authorize(user, authorizationCriteria1);

        verifyNoMoreInteractions(pmrvAuthorizationService);
    }

    @Test
    void evaluateRules_wrong_resourceId_type() {
        PmrvUser user = PmrvUser.builder().userId("userId").roleType(RoleType.OPERATOR).build();
        AuthorizationRuleScopePermission authorizationRule1 = 
                AuthorizationRuleScopePermission.builder()
            .permission(Permission.PERM_TASK_ASSIGNMENT)
            .build();

        Set<AuthorizationRuleScopePermission> rules = Set.of(authorizationRule1);
        assertThrows(NumberFormatException.class,
            () -> requestTaskAssignAccessRuleHandler.evaluateRules(rules, user, "wrong"));

        verifyNoMoreInteractions(pmrvAuthorizationService);
    }

    @Test
    void evaluateRules_requestTask_does_not_exist() {
        PmrvUser user = PmrvUser.builder().userId("userId").roleType(RoleType.OPERATOR).build();
        Long requestTaskId = 1L;
        AuthorizationRuleScopePermission authorizationRule1 = 
                AuthorizationRuleScopePermission.builder()
            .permission(Permission.PERM_TASK_ASSIGNMENT)
            .build();

        when(requestTaskAuthorityInfoProvider.getRequestTaskInfo(requestTaskId)).thenThrow(new BusinessException(ErrorCode.RESOURCE_NOT_FOUND));

        Set<AuthorizationRuleScopePermission> rules = Set.of(authorizationRule1);
        BusinessException exception = assertThrows(BusinessException.class,
            () -> requestTaskAssignAccessRuleHandler.evaluateRules(rules, user, requestTaskId.toString()));

        assertEquals(ErrorCode.RESOURCE_NOT_FOUND, exception.getErrorCode());
        verifyNoMoreInteractions(pmrvAuthorizationService);
    }

}