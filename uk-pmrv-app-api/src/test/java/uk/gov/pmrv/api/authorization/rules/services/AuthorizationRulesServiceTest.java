package uk.gov.pmrv.api.authorization.rules.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.authorization.rules.domain.AuthorizationRuleScopePermission;
import uk.gov.pmrv.api.authorization.rules.repository.AuthorizationRuleRepository;
import uk.gov.pmrv.api.common.domain.enumeration.RoleType;
import uk.gov.pmrv.api.common.domain.model.PmrvUser;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthorizationRulesServiceTest {
    private AuthorizationRulesService authorizationRulesService;

    private AuthorizationRuleRepository authorizationRuleRepository;
    private AuthorizationResourceRuleHandler authorizationResourceRuleHandler1;
    private AuthorizationResourceRuleHandler authorizationResourceRuleHandler2;
    private AuthorizationResourceSubTypeRuleHandler authorizationResourceSubTypeRuleHandler1;
    private AuthorizationResourceSubTypeRuleHandler authorizationResourceSubTypeRuleHandler2;
    private AuthorizationRuleHandler authorizationRuleHandler1;
    private AuthorizationRuleHandler authorizationRuleHandler2;

    private final PmrvUser OPERATOR = PmrvUser.builder().roleType(RoleType.OPERATOR).build();

    @BeforeEach
    void setup() {
        authorizationRuleRepository = Mockito.mock(AuthorizationRuleRepository.class);
        authorizationResourceRuleHandler1 = Mockito.mock(AuthorizationResourceRuleHandler.class);
        authorizationResourceRuleHandler2 = Mockito.mock(AuthorizationResourceRuleHandler.class);
        authorizationResourceSubTypeRuleHandler1 = Mockito.mock(AuthorizationResourceSubTypeRuleHandler.class);
        authorizationResourceSubTypeRuleHandler2 = Mockito.mock(AuthorizationResourceSubTypeRuleHandler.class);
        authorizationRuleHandler1 = Mockito.mock(AuthorizationRuleHandler.class);
        authorizationRuleHandler2 = Mockito.mock(AuthorizationRuleHandler.class);

        authorizationRulesService = new AuthorizationRulesService(authorizationRuleRepository,
                Map.of("authorizationResourceRuleHandler1", authorizationResourceRuleHandler1,
                "authorizationResourceRuleHandler2", authorizationResourceRuleHandler2),
                Map.of("authorizationResourceSubTypeRuleHandler1", authorizationResourceSubTypeRuleHandler1,
                        "authorizationResourceSubTypeRuleHandler2", authorizationResourceSubTypeRuleHandler2),
                Map.of("authorizationRuleHandler1", authorizationRuleHandler1,
                "authorizationRuleHandler2", authorizationRuleHandler2));
    }

    @Test
    void evaluateRules_with_resource_param_not_authorized_service() {
        when(authorizationRuleRepository.findRulePermissionsByServiceAndRoleType("service", RoleType.OPERATOR))
            .thenReturn(List.of());

        BusinessException businessException = assertThrows(BusinessException.class,
                () -> authorizationRulesService.evaluateRules(OPERATOR, "service", "resource"));
        assertEquals(ErrorCode.FORBIDDEN, businessException.getErrorCode());

        verifyNoMoreInteractions(authorizationResourceRuleHandler1);
        verifyNoMoreInteractions(authorizationResourceRuleHandler2);
    }

    @Test
    void evaluateRules_with_resource_param_single_handler() {
        AuthorizationRuleScopePermission authorizationRulePermissionScope1 = 
                AuthorizationRuleScopePermission.builder()
            .handler("authorizationResourceRuleHandler1")
            .build();
        
        AuthorizationRuleScopePermission authorizationRulePermissionScope2 = 
                AuthorizationRuleScopePermission.builder()
            .handler("authorizationResourceRuleHandler1")
            .build();
        
        List<AuthorizationRuleScopePermission> rules = 
                List.of(authorizationRulePermissionScope1, authorizationRulePermissionScope2);
        
        
        when(authorizationRuleRepository.findRulePermissionsByServiceAndRoleType("service", OPERATOR.getRoleType()))
            .thenReturn(rules);
        
        authorizationRulesService.evaluateRules(OPERATOR, "service", "resource");

        verify(authorizationResourceRuleHandler1, times(1))
                .evaluateRules(new HashSet<>(rules), OPERATOR, "resource");
        verifyNoMoreInteractions(authorizationResourceRuleHandler2);
    }

    @Test
    void evaluateRules_with_resource_param_multiple_handlers() {
        AuthorizationRuleScopePermission authorizationRulePermissionScope1 = 
                AuthorizationRuleScopePermission.builder()
            .handler("authorizationResourceRuleHandler1")
            .build();
        
        AuthorizationRuleScopePermission authorizationRulePermissionScope2 = 
                AuthorizationRuleScopePermission.builder()
            .handler("authorizationResourceRuleHandler2")
            .build();
        
        List<AuthorizationRuleScopePermission> rules = 
                List.of(authorizationRulePermissionScope1, authorizationRulePermissionScope2);
        

        when(authorizationRuleRepository.findRulePermissionsByServiceAndRoleType("service", OPERATOR.getRoleType()))
            .thenReturn(rules);

        authorizationRulesService.evaluateRules(OPERATOR, "service", "resource");

        verify(authorizationResourceRuleHandler1, times(1))
                .evaluateRules(Set.of(authorizationRulePermissionScope1), OPERATOR, "resource");
        verify(authorizationResourceRuleHandler2, times(1))
                .evaluateRules(Set.of(authorizationRulePermissionScope2), OPERATOR, "resource");
    }

    @Test
    void evaluateRules_no_resource_not_authorized_service() {
        when(authorizationRuleRepository.findRulePermissionsByServiceAndRoleType("service", RoleType.OPERATOR))
            .thenReturn(List.of());

        BusinessException businessException = assertThrows(BusinessException.class,
                () -> authorizationRulesService.evaluateRules(OPERATOR, "service"));
        assertEquals(ErrorCode.FORBIDDEN, businessException.getErrorCode());

        verifyNoMoreInteractions(authorizationRuleHandler1);
        verifyNoMoreInteractions(authorizationRuleHandler2);
    }

    @Test
    void evaluateRules_no_resource_single_handler() {
        AuthorizationRuleScopePermission authorizationRulePermissionScope1 = 
                AuthorizationRuleScopePermission.builder()
            .handler("authorizationRuleHandler1")
            .build();
        
        AuthorizationRuleScopePermission authorizationRulePermissionScope2 = 
                AuthorizationRuleScopePermission.builder()
            .handler("authorizationRuleHandler1")
            .build();
        
        List<AuthorizationRuleScopePermission> rules = 
                List.of(authorizationRulePermissionScope1, authorizationRulePermissionScope2);
        
        
        when(authorizationRuleRepository.findRulePermissionsByServiceAndRoleType("service", OPERATOR.getRoleType()))
            .thenReturn(rules);
        
        authorizationRulesService.evaluateRules(OPERATOR, "service");

        verify(authorizationRuleHandler1, times(1))
                .evaluateRules(new HashSet<>(rules), OPERATOR);
        verifyNoMoreInteractions(authorizationResourceRuleHandler2);
    }

    @Test
    void evaluateRules_no_resource_multiple_handlers() {
        AuthorizationRuleScopePermission authorizationRulePermissionScope1 = 
                AuthorizationRuleScopePermission.builder()
            .handler("authorizationRuleHandler1")
            .build();
        
        AuthorizationRuleScopePermission authorizationRulePermissionScope2 = 
                AuthorizationRuleScopePermission.builder()
            .handler("authorizationRuleHandler2")
            .build();
        
        List<AuthorizationRuleScopePermission> rules = 
                List.of(authorizationRulePermissionScope1, authorizationRulePermissionScope2);
        
        
        when(authorizationRuleRepository.findRulePermissionsByServiceAndRoleType("service", OPERATOR.getRoleType()))
            .thenReturn(rules);
        
        authorizationRulesService.evaluateRules(OPERATOR, "service");

        verify(authorizationRuleHandler1, times(1))
            .evaluateRules(Set.of(authorizationRulePermissionScope1), OPERATOR);
        verify(authorizationRuleHandler2, times(1))
            .evaluateRules(Set.of(authorizationRulePermissionScope2), OPERATOR);
    }

    @Test
    void evaluateRules_with_resource_sub_type_param_not_authorized_service() {
        when(authorizationRuleRepository.findRulePermissionsByServiceAndRoleType("service", RoleType.OPERATOR))
                .thenReturn(List.of());

        BusinessException businessException = assertThrows(BusinessException.class,
                () -> authorizationRulesService.evaluateRules(OPERATOR, "service", "resource", "resourceSybType"));
        assertEquals(ErrorCode.FORBIDDEN, businessException.getErrorCode());

        verifyNoMoreInteractions(authorizationResourceSubTypeRuleHandler1);
        verifyNoMoreInteractions(authorizationResourceSubTypeRuleHandler2);
    }

    @Test
    void evaluateRules_with_resource_sub_type_param_single_handler() {
        AuthorizationRuleScopePermission authorizationRulePermissionScope1 =
                AuthorizationRuleScopePermission.builder()
                        .handler("authorizationResourceSubTypeRuleHandler1")
                        .build();

        AuthorizationRuleScopePermission authorizationRulePermissionScope2 =
                AuthorizationRuleScopePermission.builder()
                        .handler("authorizationResourceSubTypeRuleHandler1")
                        .build();

        List<AuthorizationRuleScopePermission> rules =
                List.of(authorizationRulePermissionScope1, authorizationRulePermissionScope2);

        when(authorizationRuleRepository.findRulePermissionsByServiceAndRoleType("service", OPERATOR.getRoleType()))
                .thenReturn(rules);

        authorizationRulesService.evaluateRules(OPERATOR, "service", "resource", "resourceSybType");

        verify(authorizationResourceSubTypeRuleHandler1, times(1))
                .evaluateRules(new HashSet<>(rules), OPERATOR, "resource", "resourceSybType");
        verifyNoMoreInteractions(authorizationResourceSubTypeRuleHandler2);
    }

    @Test
    void evaluateRules_with_resource_sub_type_param_multiple_handlers() {
        AuthorizationRuleScopePermission authorizationRulePermissionScope1 =
                AuthorizationRuleScopePermission.builder()
                        .handler("authorizationResourceSubTypeRuleHandler1")
                        .build();

        AuthorizationRuleScopePermission authorizationRulePermissionScope2 =
                AuthorizationRuleScopePermission.builder()
                        .handler("authorizationResourceSubTypeRuleHandler2")
                        .build();

        List<AuthorizationRuleScopePermission> rules =
                List.of(authorizationRulePermissionScope1, authorizationRulePermissionScope2);

        when(authorizationRuleRepository.findRulePermissionsByServiceAndRoleType("service", OPERATOR.getRoleType()))
                .thenReturn(rules);

        authorizationRulesService.evaluateRules(OPERATOR, "service", "resource", "resourceSybType");

        verify(authorizationResourceSubTypeRuleHandler1, times(1))
                .evaluateRules(Set.of(authorizationRulePermissionScope1), OPERATOR, "resource", "resourceSybType");
        verify(authorizationResourceSubTypeRuleHandler2, times(1))
                .evaluateRules(Set.of(authorizationRulePermissionScope2), OPERATOR, "resource", "resourceSybType");
    }

}
