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
import uk.gov.pmrv.api.common.domain.enumeration.CompetentAuthority;
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
import static uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType.ACCOUNT_USERS_SETUP;

@ExtendWith(MockitoExtension.class)
class RequestTaskAccessRuleHandlerTest {
    @InjectMocks
    private RequestTaskAccessRuleHandler requestTaskAccessRuleHandler;

    @Mock
    private PmrvAuthorizationService pmrvAuthorizationService;

    @Mock
    private RequestTaskAuthorityInfoProvider requestTaskAuthorityInfoProvider;

    private final PmrvUser USER = PmrvUser.builder().userId("userId").roleType(RoleType.OPERATOR).build();

    @Test
    void wrong_resourceId_type() {
        AuthorizationRuleScopePermission authorizationRule1 = 
                AuthorizationRuleScopePermission.builder()
            .permission(Permission.PERM_ACCOUNT_USERS_EDIT)
            .build();

        Set<AuthorizationRuleScopePermission> rules = Set.of(authorizationRule1);
        assertThrows(NumberFormatException.class,
                () -> requestTaskAccessRuleHandler.evaluateRules(rules, USER, "wrong"));

        verifyNoMoreInteractions(pmrvAuthorizationService);
    }

    @Test
    void requestTask_does_not_exist() {
        AuthorizationRuleScopePermission authorizationRule1 = 
                AuthorizationRuleScopePermission.builder()
            .permission(Permission.PERM_ACCOUNT_USERS_EDIT)
            .build();

        when(requestTaskAuthorityInfoProvider.getRequestTaskInfo(1L)).thenThrow(new BusinessException(ErrorCode.RESOURCE_NOT_FOUND));

        Set<AuthorizationRuleScopePermission> rules = Set.of(authorizationRule1);
        BusinessException exception = assertThrows(BusinessException.class,
                () -> requestTaskAccessRuleHandler.evaluateRules(rules, USER, "1"));

        assertEquals(ErrorCode.RESOURCE_NOT_FOUND, exception.getErrorCode());
        verifyNoMoreInteractions(pmrvAuthorizationService);
    }

    @Test
    void single_rule() {
        AuthorizationRuleScopePermission authorizationRule1 = 
                AuthorizationRuleScopePermission.builder()
                    .permission(Permission.PERM_ACCOUNT_USERS_EDIT)
                    .resourceSubType("ACCOUNT_USERS_SETUP")
                    .build();

        RequestTaskAuthorityInfoDTO requestTaskInfoDTO = RequestTaskAuthorityInfoDTO.builder()
                .type(ACCOUNT_USERS_SETUP.name())
                .assignee(USER.getUserId())
                .authorityInfo(ResourceAuthorityInfo.builder()
                        .accountId(2L)
                        .competentAuthority(CompetentAuthority.ENGLAND)
                        .verificationBodyId(1L).build())
                .build();
        when(requestTaskAuthorityInfoProvider.getRequestTaskInfo(1L)).thenReturn(requestTaskInfoDTO);

        Set<AuthorizationRuleScopePermission> rules = Set.of(authorizationRule1);
        requestTaskAccessRuleHandler.evaluateRules(rules, USER, "1");

        AuthorizationCriteria authorizationCriteria = AuthorizationCriteria.builder()
                .accountId(requestTaskInfoDTO.getAuthorityInfo().getAccountId())
                .competentAuthority(requestTaskInfoDTO.getAuthorityInfo().getCompetentAuthority())
                .verificationBodyId(requestTaskInfoDTO.getAuthorityInfo().getVerificationBodyId())
                .permission(Permission.PERM_ACCOUNT_USERS_EDIT)
                .build();
        verify(pmrvAuthorizationService, times(1)).authorize(USER, authorizationCriteria);
        verifyNoMoreInteractions(pmrvAuthorizationService);
    }

    @Test
    void single_rule_no_matching() {
        AuthorizationRuleScopePermission authorizationRule1 = 
                AuthorizationRuleScopePermission.builder()
            .permission(Permission.PERM_ACCOUNT_USERS_EDIT)
            .resourceSubType("TYPE")
            .build();

        RequestTaskAuthorityInfoDTO requestTaskInfoDTO = RequestTaskAuthorityInfoDTO.builder()
                .type(ACCOUNT_USERS_SETUP.name())
                .assignee(USER.getUserId())
                .build();
        when(requestTaskAuthorityInfoProvider.getRequestTaskInfo(1L)).thenReturn(requestTaskInfoDTO);

        Set<AuthorizationRuleScopePermission> rules = Set.of(authorizationRule1);

        //invoke
        BusinessException exception = assertThrows(BusinessException.class,
                () -> requestTaskAccessRuleHandler.evaluateRules(rules, USER, "1"));

        assertEquals(ErrorCode.FORBIDDEN, exception.getErrorCode());

        verifyNoMoreInteractions(pmrvAuthorizationService);
    }

    @Test
    void multiple_rules() {
        AuthorizationRuleScopePermission authorizationRule1 = 
                AuthorizationRuleScopePermission.builder()
            .permission(Permission.PERM_ACCOUNT_USERS_EDIT)
            .resourceSubType("ACCOUNT_USERS_SETUP")
            .build();

        AuthorizationRuleScopePermission authorizationRule2 = 
                AuthorizationRuleScopePermission.builder()
            .permission(Permission.PERM_INSTALLATION_ACCOUNT_OPENING_ARCHIVE_EXECUTE_TASK)
            .resourceSubType("ACCOUNT_USERS_SETUP")
            .build();

        RequestTaskAuthorityInfoDTO requestTaskInfoDTO = RequestTaskAuthorityInfoDTO.builder()
                .type(ACCOUNT_USERS_SETUP.name())
                .assignee(USER.getUserId())
                .authorityInfo(ResourceAuthorityInfo.builder()
                        .accountId(2L)
                        .competentAuthority(CompetentAuthority.ENGLAND)
                        .verificationBodyId(2L).build())
                .build();
        when(requestTaskAuthorityInfoProvider.getRequestTaskInfo(1L)).thenReturn(requestTaskInfoDTO);

        Set<AuthorizationRuleScopePermission> rules = Set.of(authorizationRule1, authorizationRule2);
        requestTaskAccessRuleHandler.evaluateRules(rules, USER, "1");

        AuthorizationCriteria authorizationCriteria1 = AuthorizationCriteria.builder()
                .accountId(requestTaskInfoDTO.getAuthorityInfo().getAccountId())
                .competentAuthority(requestTaskInfoDTO.getAuthorityInfo().getCompetentAuthority())
                .verificationBodyId(requestTaskInfoDTO.getAuthorityInfo().getVerificationBodyId())
                .permission(Permission.PERM_ACCOUNT_USERS_EDIT)
                .build();
        verify(pmrvAuthorizationService, times(1)).authorize(USER, authorizationCriteria1);

        AuthorizationCriteria authorizationCriteria2 = AuthorizationCriteria.builder()
                .accountId(requestTaskInfoDTO.getAuthorityInfo().getAccountId())
                .competentAuthority(requestTaskInfoDTO.getAuthorityInfo().getCompetentAuthority())
                .verificationBodyId(requestTaskInfoDTO.getAuthorityInfo().getVerificationBodyId())
                .permission(Permission.PERM_INSTALLATION_ACCOUNT_OPENING_ARCHIVE_EXECUTE_TASK)
                .build();
        verify(pmrvAuthorizationService, times(1)).authorize(USER, authorizationCriteria2);
        verifyNoMoreInteractions(pmrvAuthorizationService);
    }

    @Test
    void multiple_rules_one_applicable() {
        AuthorizationRuleScopePermission authorizationRule1 = 
                AuthorizationRuleScopePermission.builder()
            .permission(Permission.PERM_ACCOUNT_USERS_EDIT)
            .resourceSubType("ACCOUNT_USERS_SETUP")
            .build();

        AuthorizationRuleScopePermission authorizationRule2 = 
                AuthorizationRuleScopePermission.builder()
            .permission(Permission.PERM_INSTALLATION_ACCOUNT_OPENING_ARCHIVE_EXECUTE_TASK)
            .resourceSubType("TYPE")
            .build();

        RequestTaskAuthorityInfoDTO requestTaskInfoDTO = RequestTaskAuthorityInfoDTO.builder()
                .type(ACCOUNT_USERS_SETUP.name())
                .assignee(USER.getUserId())
                .authorityInfo(ResourceAuthorityInfo.builder()
                        .accountId(2L)
                        .competentAuthority(CompetentAuthority.ENGLAND)
                        .verificationBodyId(2L).build())
                .build();
        when(requestTaskAuthorityInfoProvider.getRequestTaskInfo(1L)).thenReturn(requestTaskInfoDTO);

        Set<AuthorizationRuleScopePermission> rules = Set.of(authorizationRule1, authorizationRule2);
        requestTaskAccessRuleHandler.evaluateRules(rules, USER, "1");

        AuthorizationCriteria authorizationCriteria1 = AuthorizationCriteria.builder()
                .accountId(requestTaskInfoDTO.getAuthorityInfo().getAccountId())
                .competentAuthority(requestTaskInfoDTO.getAuthorityInfo().getCompetentAuthority())
                .verificationBodyId(requestTaskInfoDTO.getAuthorityInfo().getVerificationBodyId())
                .permission(Permission.PERM_ACCOUNT_USERS_EDIT)
                .build();
        verify(pmrvAuthorizationService, times(1)).authorize(USER, authorizationCriteria1);
        verifyNoMoreInteractions(pmrvAuthorizationService);
    }
}
