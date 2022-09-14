package uk.gov.pmrv.api.authorization.rules.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.testcontainers.junit.jupiter.Testcontainers;
import uk.gov.pmrv.api.AbstractContainerBaseTest;
import uk.gov.pmrv.api.authorization.core.domain.Permission;
import uk.gov.pmrv.api.authorization.rules.domain.AuthorizationRule;
import uk.gov.pmrv.api.authorization.rules.domain.AuthorizationRuleScopePermission;
import uk.gov.pmrv.api.authorization.rules.domain.AuthorizedService;
import uk.gov.pmrv.api.authorization.rules.domain.ResourceScopePermission;
import uk.gov.pmrv.api.authorization.rules.domain.ResourceType;
import uk.gov.pmrv.api.authorization.rules.domain.Scope;
import uk.gov.pmrv.api.common.domain.enumeration.RoleType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestCreateActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;

import javax.persistence.EntityManager;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@Testcontainers
@DataJpaTest
@Import(ObjectMapper.class)
class AuthorizationRuleRepositoryIT extends AbstractContainerBaseTest {

    @Autowired
    private AuthorizationRuleRepository repo;

    @Autowired
    private EntityManager entityManager;

    @Test
    void findRulePermissionsByServiceAndRoleType() {
        //prepare data
        AuthorizedService service1 = buildService("service1");
        AuthorizedService service2 = buildService("service2");

        AuthorizationRule rule1 = buildRule(ResourceType.REQUEST_TASK, "taskType1", "handler1", Scope.REQUEST_TASK_EXECUTE, RoleType.REGULATOR);
        AuthorizationRule rule2 = buildRule(ResourceType.CA, null, "handler2", null, RoleType.REGULATOR);
        AuthorizationRule rule3 = buildRule(ResourceType.ACCOUNT, null, "handler3", null, RoleType.OPERATOR);
        AuthorizationRule rule4 = buildRule(ResourceType.REQUEST_ACTION, null, "handler3", Scope.EDIT_USER, RoleType.REGULATOR);

        //add rule1, rule2 and rule3 to service 1
        service1.getRules().add(rule1);
        rule1.getServices().add(service1);

        service1.getRules().add(rule2);
        rule2.getServices().add(service1);

        service1.getRules().add(rule3);
        rule3.getServices().add(service1);


        //add rule1, rule3 and rule4 to service2
        service2.getRules().add(rule1);
        rule1.getServices().add(service2);

        service2.getRules().add(rule3);
        rule3.getServices().add(service2);

        service2.getRules().add(rule4);
        rule4.getServices().add(service2);

        entityManager.persist(rule1);
        entityManager.persist(rule2);
        entityManager.persist(rule3);
        entityManager.persist(rule4);
        entityManager.persist(service1);
        entityManager.persist(service2);

		ResourceScopePermission scope1 = buildPermissionScope(ResourceType.REQUEST_TASK, "taskType1",
				Permission.PERM_INSTALLATION_ACCOUNT_OPENING_ARCHIVE_EXECUTE_TASK, Scope.REQUEST_TASK_EXECUTE,
				RoleType.REGULATOR);
        entityManager.persist(scope1);
		ResourceScopePermission scope3 = buildPermissionScope(ResourceType.REQUEST_ACTION, null,
				Permission.PERM_ACCOUNT_USERS_EDIT, Scope.EDIT_USER, RoleType.REGULATOR);
        entityManager.persist(scope3);

        //invoke
        List<AuthorizationRuleScopePermission> result = repo.findRulePermissionsByServiceAndRoleType("service1", RoleType.REGULATOR);

        //assert
        assertThat(result)
                .hasSize(2)
                .containsExactlyInAnyOrder(
                        AuthorizationRuleScopePermission.builder()
                                .resourceSubType("taskType1")
                                .handler("handler1")
                                .permission(Permission.PERM_INSTALLATION_ACCOUNT_OPENING_ARCHIVE_EXECUTE_TASK)
                                .build(),
                        AuthorizationRuleScopePermission.builder()
                                .resourceSubType(null)
                                .handler("handler2")
                                .permission(null)
                                .build()
                );
    }
    
    @Test
    void findRulePermissionsByServiceAndRoleType_AuthorizationRuleScopePermission_with_different_role_type() {
        //prepare data
        AuthorizedService service1 = buildService("service1");

        AuthorizationRule rule1 = buildRule(ResourceType.REQUEST_TASK, "taskType1", "handler1", Scope.REQUEST_TASK_EXECUTE, RoleType.REGULATOR);

        service1.getRules().add(rule1);
        rule1.getServices().add(service1);

        entityManager.persist(rule1);
        entityManager.persist(service1);

		ResourceScopePermission scope1 = buildPermissionScope(ResourceType.REQUEST_TASK, "taskType1",
				Permission.PERM_INSTALLATION_ACCOUNT_OPENING_ARCHIVE_EXECUTE_TASK, Scope.REQUEST_TASK_EXECUTE,
				RoleType.OPERATOR);
        entityManager.persist(scope1);

        //invoke
        List<AuthorizationRuleScopePermission> result = repo.findRulePermissionsByServiceAndRoleType("service1", RoleType.REGULATOR);

        //assert
        assertThat(result).hasSize(1)
        .containsExactlyInAnyOrder(
                AuthorizationRuleScopePermission.builder()
                        .resourceSubType("taskType1")
                        .handler("handler1")
                        .permission(null)
                        .build()
        );
    }
    
    @Test
    void findRulePermissionsByServiceAndRoleType_AuthorizationRuleScopePermission_with_same_role_type() {
        //prepare data
        AuthorizedService service1 = buildService("service1");

        AuthorizationRule rule1 = buildRule(ResourceType.REQUEST_TASK, "taskType1", "handler1", Scope.REQUEST_TASK_EXECUTE, RoleType.REGULATOR);

        service1.getRules().add(rule1);
        rule1.getServices().add(service1);

        entityManager.persist(rule1);
        entityManager.persist(service1);

		ResourceScopePermission scope1 = buildPermissionScope(ResourceType.REQUEST_TASK, "taskType1",
				Permission.PERM_INSTALLATION_ACCOUNT_OPENING_ARCHIVE_EXECUTE_TASK, Scope.REQUEST_TASK_EXECUTE, RoleType.REGULATOR);
        entityManager.persist(scope1);

        //invoke
        List<AuthorizationRuleScopePermission> result = repo.findRulePermissionsByServiceAndRoleType("service1", RoleType.REGULATOR);

        //assert
        assertThat(result)
        	.hasSize(1)
        	.containsExactly(
                    AuthorizationRuleScopePermission.builder()
                            .resourceSubType("taskType1")
                            .handler("handler1")
                            .permission(Permission.PERM_INSTALLATION_ACCOUNT_OPENING_ARCHIVE_EXECUTE_TASK)
                            .build()
            );
    }

    @Test
    void findRulePermissionsByServiceAndRoleType_rule_subType_not_null_scope_permission_subType_null() {
        //prepare data
        AuthorizedService service = buildService("service");
        AuthorizationRule rule = buildRule(ResourceType.REQUEST_TASK, "taskType", "handler", Scope.REQUEST_TASK_EXECUTE, RoleType.REGULATOR);

        service.getRules().add(rule);
        rule.getServices().add(service);

        entityManager.persist(rule);
        entityManager.persist(service);

		ResourceScopePermission scope = buildPermissionScope(ResourceType.REQUEST_TASK, null,
				Permission.PERM_INSTALLATION_ACCOUNT_OPENING_ARCHIVE_EXECUTE_TASK, Scope.REQUEST_TASK_EXECUTE,
				RoleType.REGULATOR);
        entityManager.persist(scope);

        //invoke
        List<AuthorizationRuleScopePermission> result = repo.findRulePermissionsByServiceAndRoleType("service", RoleType.REGULATOR);

        //assert
        assertThat(result)
                .hasSize(1)
                .containsExactly(
                        AuthorizationRuleScopePermission.builder()
                                .resourceSubType("taskType")
                                .handler("handler")
                                .permission(null)
                                .build()
                );
    }

    @Test
    void findRulePermissionsByServiceAndRoleType_rule_subType_null_scope_permission_subType_null() {
        //prepare data
        AuthorizedService service = buildService("service");
        AuthorizationRule rule = buildRule(ResourceType.REQUEST_TASK, null, "handler", Scope.REQUEST_TASK_EXECUTE, RoleType.REGULATOR);

        service.getRules().add(rule);
        rule.getServices().add(service);

        entityManager.persist(rule);
        entityManager.persist(service);

		ResourceScopePermission scope = buildPermissionScope(ResourceType.REQUEST_TASK, null,
				Permission.PERM_INSTALLATION_ACCOUNT_OPENING_ARCHIVE_EXECUTE_TASK, Scope.REQUEST_TASK_EXECUTE,
				RoleType.REGULATOR);
        entityManager.persist(scope);

        //invoke
        List<AuthorizationRuleScopePermission> result = repo.findRulePermissionsByServiceAndRoleType("service", RoleType.REGULATOR);

        //assert
        assertThat(result)
                .hasSize(1)
                .containsExactly(
                        AuthorizationRuleScopePermission.builder()
                                .resourceSubType(null)
                                .handler("handler")
                                .permission(Permission.PERM_INSTALLATION_ACCOUNT_OPENING_ARCHIVE_EXECUTE_TASK)
                                .build()
                );
    }
    
    @Test
    void findRulePermissionsByResourceTypeScopeAndRoleType() {
        //prepare data
        ResourceScopePermission accountPermitSurrenderInitScope = buildPermissionScope(ResourceType.ACCOUNT,
                RequestCreateActionType.PERMIT_SURRENDER.name(),
                Permission.PERM_PERMIT_SURRENDER_APPLICATION_SUBMIT_EXECUTE_TASK, Scope.REQUEST_CREATE, RoleType.OPERATOR);
        entityManager.persist(accountPermitSurrenderInitScope);
        
        entityManager.persist(buildPermissionScope(ResourceType.REQUEST_TASK,
                RequestTaskType.INSTALLATION_ACCOUNT_OPENING_ARCHIVE.name(),
                Permission.PERM_INSTALLATION_ACCOUNT_OPENING_ARCHIVE_VIEW_TASK, Scope.REQUEST_TASK_VIEW, RoleType.OPERATOR));
        
        AuthorizationRule accountOperatorPermitSurrenderInitRule = buildRule(ResourceType.ACCOUNT,
                RequestCreateActionType.PERMIT_SURRENDER.name(), "handler", Scope.REQUEST_CREATE,
                RoleType.OPERATOR);
        entityManager.persist(accountOperatorPermitSurrenderInitRule);
        
        AuthorizationRule accountRegulatorPermitSurrenderInitRule = buildRule(ResourceType.ACCOUNT,
                RequestCreateActionType.PERMIT_SURRENDER.name(), "handler", Scope.REQUEST_CREATE,
                RoleType.REGULATOR);
        entityManager.persist(accountRegulatorPermitSurrenderInitRule);
        
        AuthorizationRule accountOperatorInstOpeningRule = buildRule(ResourceType.ACCOUNT,
                RequestCreateActionType.INSTALLATION_ACCOUNT_OPENING_SUBMIT_APPLICATION.name(), "handler", null,
                RoleType.OPERATOR);
        entityManager.persist(accountOperatorInstOpeningRule);
        
        AuthorizationRule dummyRule = buildRule(ResourceType.ACCOUNT,
                RequestTaskType.ACCOUNT_USERS_SETUP.name(), "handler", null,
                RoleType.OPERATOR);
        entityManager.persist(dummyRule);
        
        
        List<AuthorizationRuleScopePermission> results = repo.findRulePermissionsByResourceTypeScopeAndRoleType(ResourceType.ACCOUNT, Scope.REQUEST_CREATE, RoleType.OPERATOR);
        
        assertThat(results)
            .hasSize(1)
            .containsExactlyInAnyOrder( 
                    AuthorizationRuleScopePermission.builder()
                    .resourceSubType(RequestCreateActionType.PERMIT_SURRENDER.name())
                    .handler("handler")
                    .permission(Permission.PERM_PERMIT_SURRENDER_APPLICATION_SUBMIT_EXECUTE_TASK)
                    .build());
    }
    
    @Test
    void findRulePermissionsByResourceTypeScopeAndRoleType_with_different_scope_permission_role_type() {
        //prepare data
        ResourceScopePermission accountPermitSurrenderInitScope = buildPermissionScope(ResourceType.ACCOUNT,
                RequestCreateActionType.PERMIT_SURRENDER.name(),
                Permission.PERM_PERMIT_SURRENDER_APPLICATION_SUBMIT_EXECUTE_TASK, 
                Scope.REQUEST_CREATE,
                RoleType.REGULATOR);
        entityManager.persist(accountPermitSurrenderInitScope);
        
        entityManager.persist(buildPermissionScope(ResourceType.REQUEST_TASK,
                RequestTaskType.INSTALLATION_ACCOUNT_OPENING_ARCHIVE.name(),
                Permission.PERM_INSTALLATION_ACCOUNT_OPENING_ARCHIVE_VIEW_TASK, 
                Scope.REQUEST_TASK_VIEW,
                RoleType.OPERATOR));
        
        AuthorizationRule accountOperatorPermitSurrenderInitRule = buildRule(ResourceType.ACCOUNT,
                RequestCreateActionType.PERMIT_SURRENDER.name(), 
                "handler", 
                Scope.REQUEST_CREATE,
                RoleType.OPERATOR);
        entityManager.persist(accountOperatorPermitSurrenderInitRule);
        
        AuthorizationRule accountRegulatorPermitSurrenderInitRule = buildRule(ResourceType.ACCOUNT,
                RequestCreateActionType.PERMIT_SURRENDER.name(), 
                "handler", 
                Scope.REQUEST_CREATE,
                RoleType.REGULATOR);
        entityManager.persist(accountRegulatorPermitSurrenderInitRule);
        
        List<AuthorizationRuleScopePermission> results = repo.findRulePermissionsByResourceTypeScopeAndRoleType(ResourceType.ACCOUNT, Scope.REQUEST_CREATE, RoleType.OPERATOR);
        
        assertThat(results).hasSize(1)
        .containsExactly(
                AuthorizationRuleScopePermission.builder()
                        .resourceSubType(RequestCreateActionType.PERMIT_SURRENDER.name())
                        .handler("handler")
                        .permission(null)
                        .build()
        );
    }
    
    @Test
    void findRulePermissionsByResourceTypeScopeAndRoleType_with_same_scope_permission_role_type() {
        //prepare data
        ResourceScopePermission accountPermitSurrenderInitScope = buildPermissionScope(ResourceType.ACCOUNT,
                RequestCreateActionType.PERMIT_SURRENDER.name(),
                Permission.PERM_PERMIT_SURRENDER_APPLICATION_SUBMIT_EXECUTE_TASK, 
                Scope.REQUEST_CREATE,
                RoleType.OPERATOR);
        entityManager.persist(accountPermitSurrenderInitScope);
        
        entityManager.persist(buildPermissionScope(ResourceType.REQUEST_TASK,
                RequestTaskType.INSTALLATION_ACCOUNT_OPENING_ARCHIVE.name(),
                Permission.PERM_INSTALLATION_ACCOUNT_OPENING_ARCHIVE_VIEW_TASK, 
                Scope.REQUEST_TASK_VIEW, 
                RoleType.OPERATOR));
        
        AuthorizationRule accountOperatorPermitSurrenderInitRule = buildRule(ResourceType.ACCOUNT,
                RequestCreateActionType.PERMIT_SURRENDER.name(), 
                "handler", 
                Scope.REQUEST_CREATE,
                RoleType.OPERATOR);
        entityManager.persist(accountOperatorPermitSurrenderInitRule);
        
        AuthorizationRule accountRegulatorPermitSurrenderInitRule = buildRule(ResourceType.ACCOUNT,
                RequestCreateActionType.PERMIT_SURRENDER.name(), 
                "handler", 
                Scope.REQUEST_CREATE,
                RoleType.REGULATOR);
        entityManager.persist(accountRegulatorPermitSurrenderInitRule);
        
        List<AuthorizationRuleScopePermission> results = repo.findRulePermissionsByResourceTypeScopeAndRoleType(ResourceType.ACCOUNT, Scope.REQUEST_CREATE, RoleType.OPERATOR);
        
        assertThat(results)
        .hasSize(1)
        .containsExactlyInAnyOrder( 
                AuthorizationRuleScopePermission.builder()
                .resourceSubType(RequestCreateActionType.PERMIT_SURRENDER.name())
                .handler("handler")
                .permission(Permission.PERM_PERMIT_SURRENDER_APPLICATION_SUBMIT_EXECUTE_TASK)
                .build());
    }

    private AuthorizationRule buildRule(ResourceType resourceType, String resourceSubType, String handler, Scope scope, RoleType roleType) {
        return AuthorizationRule.builder()
                .resourceType(resourceType)
                .resourceSubType(resourceSubType)
                .handler(handler)
                .scope(scope)
                .roleType(roleType)
                .build();
    }

    private AuthorizedService buildService(String name) {
        AuthorizedService service = new AuthorizedService();
        service.setName(name);
        return service;
    }

    private ResourceScopePermission buildPermissionScope(ResourceType resourceType, String resourceSubType, Permission permission, Scope scope, RoleType roleType) {
        return ResourceScopePermission.builder()
                .resourceType(resourceType)
                .resourceSubType(resourceSubType)
                .permission(permission)
                .scope(scope)
                .roleType(roleType)
                .build();
    }
}
