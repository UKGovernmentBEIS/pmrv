package uk.gov.pmrv.api.authorization.rules.services.handlers;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.authorization.core.domain.Permission;
import uk.gov.pmrv.api.authorization.rules.domain.AuthorizationRuleScopePermission;
import uk.gov.pmrv.api.authorization.rules.services.authorization.AuthorizationCriteria;
import uk.gov.pmrv.api.authorization.rules.services.authorization.PmrvAuthorizationService;
import uk.gov.pmrv.api.common.domain.enumeration.RoleType;
import uk.gov.pmrv.api.common.domain.model.PmrvUser;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestCreateActionType;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@ExtendWith(MockitoExtension.class)
class AccountRequestCreateRuleHandlerTest {

    @InjectMocks
    private AccountRequestCreateRuleHandler handler;

    @Mock
    private PmrvAuthorizationService pmrvAuthorizationService;
    
    private final PmrvUser USER = PmrvUser.builder().roleType(RoleType.OPERATOR).build();
    
    @Test
    void evaluateRules_empty_applied_rules() {
        String resourceId = "1";
        AuthorizationRuleScopePermission rule1 = 
                AuthorizationRuleScopePermission.builder()
            .resourceSubType(RequestCreateActionType.INSTALLATION_ACCOUNT_OPENING_SUBMIT_APPLICATION.name())
            .build();
        
        BusinessException be = assertThrows(BusinessException.class, () -> handler.evaluateRules(Set.of(rule1), USER, resourceId,
                RequestCreateActionType.PERMIT_SURRENDER.name()));

        assertThat(be.getErrorCode()).isEqualTo(ErrorCode.FORBIDDEN);
        verifyNoInteractions(pmrvAuthorizationService);
    }
    
    @Test
    void evaluateRules_applied_rules() {
        String resourceId = "1";
        AuthorizationRuleScopePermission authorizationRulePermissionScope1 = 
                AuthorizationRuleScopePermission.builder()
            .permission(Permission.PERM_PERMIT_SURRENDER_APPLICATION_SUBMIT_EXECUTE_TASK)
            .resourceSubType(RequestCreateActionType.PERMIT_SURRENDER.name())
            .build();
        
        AuthorizationRuleScopePermission authorizationRulePermissionScope2 = 
                AuthorizationRuleScopePermission.builder()
            .resourceSubType(RequestCreateActionType.INSTALLATION_ACCOUNT_OPENING_SUBMIT_APPLICATION.name())
            .build();
        
        handler.evaluateRules(Set.of(authorizationRulePermissionScope1, authorizationRulePermissionScope2), USER, resourceId,
                RequestCreateActionType.PERMIT_SURRENDER.name());

        verify(pmrvAuthorizationService, times(1)).authorize(USER, AuthorizationCriteria.builder().accountId(Long.valueOf(resourceId)).permission(Permission.PERM_PERMIT_SURRENDER_APPLICATION_SUBMIT_EXECUTE_TASK).build());
        verifyNoMoreInteractions(pmrvAuthorizationService);
    }
    
    @Test
    void evaluateRules_no_account_resource_id() {
        AuthorizationRuleScopePermission authorizationRulePermissionScope1 = 
                AuthorizationRuleScopePermission.builder()
            .permission(Permission.PERM_PERMIT_SURRENDER_APPLICATION_SUBMIT_EXECUTE_TASK)
            .resourceSubType(RequestCreateActionType.PERMIT_SURRENDER.name())
            .build();
        
        AuthorizationRuleScopePermission authorizationRulePermissionScope2 = 
                AuthorizationRuleScopePermission.builder()
            .resourceSubType(RequestCreateActionType.INSTALLATION_ACCOUNT_OPENING_SUBMIT_APPLICATION.name())
            .build();
        
        handler.evaluateRules(Set.of(authorizationRulePermissionScope1, authorizationRulePermissionScope2), USER, null,
                RequestCreateActionType.INSTALLATION_ACCOUNT_OPENING_SUBMIT_APPLICATION.name());

        verifyNoInteractions(pmrvAuthorizationService);
    }
}
