package uk.gov.pmrv.api.authorization.rules.services.resource;

import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.authorization.core.domain.Permission;
import uk.gov.pmrv.api.authorization.rules.domain.AuthorizationRuleScopePermission;
import uk.gov.pmrv.api.authorization.rules.domain.ResourceType;
import uk.gov.pmrv.api.authorization.rules.domain.Scope;
import uk.gov.pmrv.api.authorization.rules.repository.AuthorizationRuleRepository;
import uk.gov.pmrv.api.authorization.rules.services.authorization.AuthorizationCriteria;
import uk.gov.pmrv.api.authorization.rules.services.authorization.PmrvResourceAuthorizationServiceDelegator;
import uk.gov.pmrv.api.common.domain.enumeration.RoleType;
import uk.gov.pmrv.api.common.domain.model.PmrvUser;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestCreateActionType;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccountRequestAuthorizationResourceServiceTest {

    @InjectMocks
    private AccountRequestAuthorizationResourceService service;
    
    @Mock
    private AuthorizationRuleRepository authorizationRuleRepository;
    
    @Mock
    private PmrvResourceAuthorizationServiceDelegator resourceAuthorizationServiceDelegator;
    
    @Test
    void findRequestCreateActionsByAccountId() {
        PmrvUser user = PmrvUser.builder().roleType(RoleType.OPERATOR).build();
        Long accountId = 1L;
        
        List<AuthorizationRuleScopePermission> rules = List.of(
                AuthorizationRuleScopePermission.builder().resourceSubType(RequestCreateActionType.INSTALLATION_ACCOUNT_OPENING_SUBMIT_APPLICATION.name()).handler("handler").permission(null).build(),
                AuthorizationRuleScopePermission.builder().resourceSubType(RequestCreateActionType.PERMIT_SURRENDER.name()).handler("handler").permission(Permission.PERM_PERMIT_SURRENDER_APPLICATION_SUBMIT_EXECUTE_TASK).build()
                );
        
        when(authorizationRuleRepository.findRulePermissionsByResourceTypeScopeAndRoleType(ResourceType.ACCOUNT, Scope.REQUEST_CREATE, RoleType.OPERATOR)).thenReturn(rules);
        
        when(resourceAuthorizationServiceDelegator.isAuthorized(ResourceType.ACCOUNT, user, AuthorizationCriteria.builder().accountId(accountId).permission(null).build())).thenReturn(true);
        when(resourceAuthorizationServiceDelegator.isAuthorized(ResourceType.ACCOUNT, user, AuthorizationCriteria.builder().accountId(accountId).permission(Permission.PERM_PERMIT_SURRENDER_APPLICATION_SUBMIT_EXECUTE_TASK).build())).thenReturn(false);
        
        Set<String> results = service.findRequestCreateActionsByAccountId(user, accountId);
        
        assertThat(results)
            .hasSize(1)
            .containsOnly(RequestCreateActionType.INSTALLATION_ACCOUNT_OPENING_SUBMIT_APPLICATION.name());
    }
}
