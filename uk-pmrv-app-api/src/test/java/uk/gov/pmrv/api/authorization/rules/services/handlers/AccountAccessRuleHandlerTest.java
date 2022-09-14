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

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AccountAccessRuleHandlerTest {

    @InjectMocks
    private AccountAccessRuleHandler accountAccessRuleHandler;

    @Mock
    private PmrvAuthorizationService pmrvAuthorizationService;

    private final PmrvUser USER = PmrvUser.builder().roleType(RoleType.OPERATOR).build();

    @Test
    void single_rule() {
        AuthorizationRuleScopePermission authorizationRulePermissionScope1 = 
                AuthorizationRuleScopePermission.builder()
            .permission(Permission.PERM_ACCOUNT_USERS_EDIT)
            .build();
        
        accountAccessRuleHandler.evaluateRules(Set.of(authorizationRulePermissionScope1), USER, "1");

        AuthorizationCriteria authorizationCriteria = AuthorizationCriteria.builder()
                .accountId(1L)
                .permission(Permission.PERM_ACCOUNT_USERS_EDIT)
                .build();
        verify(pmrvAuthorizationService, times(1)).authorize(USER, authorizationCriteria);
    }

    @Test
    void multiple_rules() {
        AuthorizationRuleScopePermission authorizationRulePermissionScope1 = 
                AuthorizationRuleScopePermission.builder()
            .permission(Permission.PERM_ACCOUNT_USERS_EDIT)
            .build();
        
        AuthorizationRuleScopePermission authorizationRulePermissionScope2 = 
                AuthorizationRuleScopePermission.builder()
            .permission(Permission.PERM_INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW_VIEW_TASK)
            .build();
        
        accountAccessRuleHandler.evaluateRules(Set.of(authorizationRulePermissionScope1, authorizationRulePermissionScope2), USER, "1");

        AuthorizationCriteria authorizationCriteria1 = AuthorizationCriteria.builder()
                .accountId(1L)
                .permission(Permission.PERM_ACCOUNT_USERS_EDIT)
                .build();
        verify(pmrvAuthorizationService, times(1)).authorize(USER, authorizationCriteria1);

        AuthorizationCriteria authorizationCriteria2 = AuthorizationCriteria.builder()
                .accountId(1L)
                .permission(Permission.PERM_INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW_VIEW_TASK)
                .build();
        verify(pmrvAuthorizationService, times(1)).authorize(USER, authorizationCriteria2);
    }

    @Test
    void wrong_resourceId_type() {
        AuthorizationRuleScopePermission authorizationRulePermissionScope1 = 
                AuthorizationRuleScopePermission.builder()
            .permission(Permission.PERM_ACCOUNT_USERS_EDIT)
            .build();

        Set<AuthorizationRuleScopePermission> rules = Set.of(authorizationRulePermissionScope1);
        assertThrows(NumberFormatException.class,
                () -> accountAccessRuleHandler.evaluateRules(rules, USER, "wrong"));

    }
}
