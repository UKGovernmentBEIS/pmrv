package uk.gov.pmrv.api.authorization.core.repository.impl;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.EntityManager;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.fasterxml.jackson.databind.ObjectMapper;

import uk.gov.pmrv.api.AbstractContainerBaseTest;
import uk.gov.pmrv.api.authorization.core.domain.Authority;
import uk.gov.pmrv.api.authorization.core.domain.AuthorityPermission;
import uk.gov.pmrv.api.authorization.core.domain.AuthorityStatus;
import uk.gov.pmrv.api.authorization.core.domain.Permission;
import uk.gov.pmrv.api.authorization.rules.domain.ResourceScopePermission;
import uk.gov.pmrv.api.authorization.rules.domain.ResourceType;
import uk.gov.pmrv.api.authorization.rules.domain.Scope;
import uk.gov.pmrv.api.common.domain.enumeration.CompetentAuthority;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@Testcontainers
@DataJpaTest
@Import(ObjectMapper.class)
class AuthorityCustomRepositoryIT extends AbstractContainerBaseTest {

	@Autowired
	private AuthorityCustomRepositoryImpl repo;
	
	@Autowired
	private EntityManager entityManager;
	
	private static final String USER1 = "user1";
	private static final String USER2 = "user2";
    private static final String USER3 = "user3";
	
	@Test
    void findResourceSubTypesRegulatorUserHasScope() {
        //prepare data
        Authority authorityUser1England = 
                Authority.builder()
                    .userId(USER1)
                    .code("regularator_admin")
                    .status(AuthorityStatus.ACTIVE)
                    .competentAuthority(CompetentAuthority.ENGLAND)
                    .createdBy(USER1)
                    .build();
        authorityUser1England
            .addPermission(
                    AuthorityPermission.builder()
                                    .permission(Permission.PERM_INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW_VIEW_TASK)
                                    .build());
        authorityUser1England
            .addPermission(
                    AuthorityPermission.builder()
                                    .permission(Permission.PERM_VB_USERS_EDIT)
                                    .build());
        
        Authority authorityUser1Wales = 
                Authority.builder()
                    .userId(USER1)
                    .code("regularator_admin")
                    .status(AuthorityStatus.ACTIVE)
                    .competentAuthority(CompetentAuthority.WALES)
                    .createdBy(USER1)
                    .build();
        authorityUser1Wales
            .addPermission(
                    AuthorityPermission.builder()
                                    .permission(Permission.PERM_INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW_VIEW_TASK)
                                    .build());
        
        Authority authorityUser2England = 
                Authority.builder()
                    .userId(USER2)
                    .code("regularator_admin")
                    .status(AuthorityStatus.ACTIVE)
                    .competentAuthority(CompetentAuthority.ENGLAND)
                    .createdBy(USER2)
                    .build();
        authorityUser2England
            .addPermission(
                    AuthorityPermission.builder()
                                    .permission(Permission.PERM_INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW_VIEW_TASK)
                                    .build());
        authorityUser2England
            .addPermission(
                    AuthorityPermission.builder()
                                    .permission(Permission.PERM_INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW_EXECUTE_TASK)
                                    .build());

        entityManager.persist(authorityUser1England);
        entityManager.persist(authorityUser2England);
        entityManager.persist(authorityUser1Wales);
        
        createResourceScopePermission(ResourceType.REQUEST_TASK, RequestTaskType.INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW.name(), Scope.REQUEST_TASK_VIEW, Permission.PERM_INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW_VIEW_TASK);
        createResourceScopePermission(ResourceType.REQUEST_TASK, RequestTaskType.INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW.name(), Scope.REQUEST_TASK_EXECUTE, Permission.PERM_INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW_EXECUTE_TASK);
        createResourceScopePermission(ResourceType.REQUEST_TASK, RequestTaskType.ACCOUNT_USERS_SETUP.name(), Scope.REQUEST_TASK_VIEW, Permission.PERM_VB_USERS_EDIT);
        
        flushAndClear();
        
        //execute
        Map<CompetentAuthority, Set<String>> result = repo.findResourceSubTypesRegulatorUserHasScope(USER1, ResourceType.REQUEST_TASK, Scope.REQUEST_TASK_VIEW);
        
        //assert
        assertThat(result)
            .containsExactlyInAnyOrderEntriesOf(Map.of(
                    authorityUser1England.getCompetentAuthority(), Set.of(RequestTaskType.INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW.name(), RequestTaskType.ACCOUNT_USERS_SETUP.name()),
                    authorityUser1Wales.getCompetentAuthority(), Set.of(RequestTaskType.INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW.name())));
    }

    @Test
    void findResourceSubTypesVerifierUserHasScope() {
        // Verifier to test
        Authority authorityUser1 = Authority.builder()
                .userId(USER1)
                .code("verifier_admin")
                .status(AuthorityStatus.ACTIVE)
                .verificationBodyId(1L)
                .createdBy(USER1)
                .build();
        authorityUser1.addPermission(AuthorityPermission.builder()
                .permission(Permission.PERM_INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW_VIEW_TASK).build());
        authorityUser1.addPermission(AuthorityPermission.builder()
                .permission(Permission.PERM_VB_USERS_EDIT).build());

        // Verifier on same vb
        Authority authorityUser2 = Authority.builder()
                .userId(USER2)
                .code("verifier")
                .status(AuthorityStatus.ACTIVE)
                .createdBy(USER2)
                .build();
        authorityUser2.addPermission(AuthorityPermission.builder()
                .permission(Permission.PERM_INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW_EXECUTE_TASK).build());

        // Verifier on different vb
        Authority authorityUser3 = Authority.builder()
                .userId(USER3)
                .code("verifier_admin")
                .status(AuthorityStatus.ACTIVE)
                .createdBy(USER3)
                .build();
        authorityUser3.addPermission(AuthorityPermission.builder()
                .permission(Permission.PERM_INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW_VIEW_TASK).build());
        authorityUser3.addPermission(AuthorityPermission.builder()
                .permission(Permission.PERM_INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW_EXECUTE_TASK).build());

        entityManager.persist(authorityUser1);
        entityManager.persist(authorityUser2);
        entityManager.persist(authorityUser3);

        createResourceScopePermission(ResourceType.REQUEST_TASK, RequestTaskType.INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW.name(), Scope.REQUEST_TASK_VIEW, Permission.PERM_INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW_VIEW_TASK);
        createResourceScopePermission(ResourceType.REQUEST_TASK, RequestTaskType.INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW.name(), Scope.REQUEST_TASK_EXECUTE, Permission.PERM_INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW_EXECUTE_TASK);
        createResourceScopePermission(ResourceType.REQUEST_TASK, RequestTaskType.ACCOUNT_USERS_SETUP.name(), Scope.REQUEST_TASK_VIEW, Permission.PERM_VB_USERS_EDIT);

        flushAndClear();

        //execute
        Map<Long, Set<String>> result = repo.findResourceSubTypesVerifierUserHasScope(USER1, ResourceType.REQUEST_TASK, Scope.REQUEST_TASK_VIEW);

        //assert
        assertThat(result).containsExactlyInAnyOrderEntriesOf(Map.of(
                1L, Set.of(RequestTaskType.INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW.name(), RequestTaskType.ACCOUNT_USERS_SETUP.name())));
    }
	
	@Test
    void findOperatorUsersByAccountId() {
        //prepare data
        Long account1 = 1L;
        Long account2 = 2L;
        Authority authorityUser1Account1 = 
                Authority.builder()
                    .userId(USER1)
                    .code("operator_admin")
                    .status(AuthorityStatus.ACTIVE)
                    .accountId(account1)
                    .createdBy(USER1)
                    .build();
        Authority authorityUser2Account2 = 
                Authority.builder()
                    .userId(USER2)
                    .code("operator_admin")
                    .status(AuthorityStatus.ACTIVE)
                    .accountId(account2)
                    .createdBy(USER2)
                    .build();

        entityManager.persist(authorityUser1Account1);
        entityManager.persist(authorityUser2Account2);
        
        flushAndClear();
        
        //execute
        List<String> result = repo.findOperatorUsersByAccountId(account1);
        
        //assert
        assertThat(result)
            .containsExactly(USER1);
    }
	
	@Test
    void findRegulatorUsersByCompetentAuthority() {
        //prepare data
        Authority authorityUser1England = 
                Authority.builder()
                    .userId(USER1)
                    .code("regularator_admin")
                    .status(AuthorityStatus.ACTIVE)
                    .competentAuthority(CompetentAuthority.ENGLAND)
                    .createdBy(USER1)
                    .build();
        
        Authority authorityUser1Scotland = 
                Authority.builder()
                    .userId(USER2)
                    .code("regularator_admin")
                    .status(AuthorityStatus.ACTIVE)
                    .competentAuthority(CompetentAuthority.SCOTLAND)
                    .createdBy(USER2)
                    .build();

        entityManager.persist(authorityUser1England);
        entityManager.persist(authorityUser1Scotland);
        
        flushAndClear();
        
        //execute
        List<String> result = repo.findRegulatorUsersByCompetentAuthority(CompetentAuthority.ENGLAND);
        
        //assert
        assertThat(result)
            .containsExactly(USER1);
    }

    @Test
    void findVerifierUsersByVerificationBodyId() {
        //prepare data
        Authority authorityUser1Vb1 = 
                Authority.builder()
                    .userId(USER1)
                    .code("verifier_admin")
                    .status(AuthorityStatus.ACTIVE)
                    .verificationBodyId(1L)
                    .createdBy(USER1)
                    .build();
        
        Authority authorityUser2Vb2 = 
                Authority.builder()
                    .userId(USER2)
                    .code("verifier_admin")
                    .status(AuthorityStatus.ACTIVE)
                    .verificationBodyId(2L)
                    .createdBy(USER2)
                    .build();

        entityManager.persist(authorityUser1Vb1);
        entityManager.persist(authorityUser2Vb2);
        
        flushAndClear();
        
        //execute
        List<String> result = repo.findVerifierUsersByVerificationBodyId(1L);
        
        //assert
        assertThat(result)
            .containsExactly(USER1);
    }
	
	@Test
	void findStatusByUsersAndAccountId() {
	    Long account = 1L;
	    Long anotherAccount = 2L;
	    
	    Authority authorityUser1 =
                Authority.builder()
                    .userId("user1")
                    .code("code1")
                    .status(AuthorityStatus.ACTIVE)
                    .accountId(account)
                    .competentAuthority(CompetentAuthority.ENGLAND)
                    .createdBy("ff")
                    .build();
	    entityManager.persist(authorityUser1);
        
        Authority authorityUser2 = 
                Authority.builder()
                    .userId("user2")
                    .code("code2")
                    .status(AuthorityStatus.DISABLED)
                    .accountId(account)
                    .competentAuthority(CompetentAuthority.SCOTLAND)
                    .createdBy("hh")
                    .build();
        entityManager.persist(authorityUser2);
        
        Authority authorityUser3_1 = 
                Authority.builder()
                    .userId("user3")
                    .code("code3")
                    .status(AuthorityStatus.ACTIVE)
                    .accountId(account)
                    .competentAuthority(CompetentAuthority.SCOTLAND)
                    .createdBy("hh")
                    .build();
        entityManager.persist(authorityUser3_1);
        
        Authority authorityUser3_2 = 
                Authority.builder()
                    .userId("user3")
                    .code("code3")
                    .status(AuthorityStatus.DISABLED)
                    .accountId(anotherAccount)
                    .competentAuthority(CompetentAuthority.SCOTLAND)
                    .createdBy("hh")
                    .build();
        entityManager.persist(authorityUser3_2);
        
        flushAndClear();
        
        Map<String, AuthorityStatus> result = repo.findStatusByUsersAndAccountId(List.of("user1", "user2", "user3"), account);
        
        assertThat(result).containsExactlyInAnyOrderEntriesOf(Map.of(
                "user1", AuthorityStatus.ACTIVE,
                "user2", AuthorityStatus.DISABLED,
                "user3", AuthorityStatus.ACTIVE
                ));
	}

	@Test
	void findResourceSubTypesOperatorUserHasScope() {
	    String user1 = "user1";
	    String user2 = "user2";
	    Long account1 = 1L;
	    Long account2 = 2L;
	    Long account3 = 3L;

	    Authority authorityUser1Account1 = Authority.builder()
            .accountId(account1)
            .userId(user1)
            .status(AuthorityStatus.ACTIVE)
            .createdBy(user1)
            .creationDate(LocalDateTime.now())
            .build();
	    authorityUser1Account1.addPermission(
	        AuthorityPermission.builder()
                .permission(Permission.PERM_PERMIT_ISSUANCE_APPLICATION_SUBMIT_VIEW_TASK)
                .build()
        );
        authorityUser1Account1.addPermission(
            AuthorityPermission.builder()
                .permission(Permission.PERM_INSTALLATION_ACCOUNT_OPENING_ARCHIVE_VIEW_TASK)
                .build()
        );

        entityManager.persist(authorityUser1Account1);

        Authority authorityUser1Account2 = Authority.builder()
            .accountId(account2)
            .userId(user1)
            .status(AuthorityStatus.ACTIVE)
            .createdBy(user1)
            .creationDate(LocalDateTime.now())
            .build();
        authorityUser1Account2.addPermission(
            AuthorityPermission.builder()
                .permission(Permission.PERM_PERMIT_ISSUANCE_APPLICATION_SUBMIT_VIEW_TASK)
                .build()
        );
        authorityUser1Account2.addPermission(
            AuthorityPermission.builder()
                .permission(Permission.PERM_PERMIT_ISSUANCE_APPLICATION_SUBMIT_EXECUTE_TASK)
                .build()
        );

        entityManager.persist(authorityUser1Account2);

        Authority authorityUser2Account2 = Authority.builder()
            .accountId(account2)
            .userId(user2)
            .status(AuthorityStatus.ACTIVE)
            .createdBy(user1)
            .creationDate(LocalDateTime.now())
            .build();
        authorityUser2Account2.addPermission(
            AuthorityPermission.builder()
                .permission(Permission.PERM_INSTALLATION_ACCOUNT_OPENING_ARCHIVE_VIEW_TASK)
                .build()
        );

        entityManager.persist(authorityUser2Account2);

        Authority authorityUser1Account3 = Authority.builder()
            .accountId(account3)
            .userId(user1)
            .status(AuthorityStatus.PENDING)
            .createdBy(user1)
            .creationDate(LocalDateTime.now())
            .build();
        authorityUser1Account3.addPermission(
            AuthorityPermission.builder()
                .permission(Permission.PERM_PERMIT_ISSUANCE_APPLICATION_SUBMIT_VIEW_TASK)
                .build()
        );
        authorityUser1Account3.addPermission(
            AuthorityPermission.builder()
                .permission(Permission.PERM_INSTALLATION_ACCOUNT_OPENING_ARCHIVE_VIEW_TASK)
                .build()
        );

        entityManager.persist(authorityUser1Account3);

        createResourceScopePermission(ResourceType.REQUEST_TASK, RequestTaskType.PERMIT_ISSUANCE_APPLICATION_SUBMIT.name(), Scope.REQUEST_TASK_VIEW, Permission.PERM_PERMIT_ISSUANCE_APPLICATION_SUBMIT_VIEW_TASK);
        createResourceScopePermission(ResourceType.REQUEST_TASK, RequestTaskType.PERMIT_ISSUANCE_APPLICATION_SUBMIT.name(), Scope.REQUEST_TASK_EXECUTE, Permission.PERM_PERMIT_ISSUANCE_APPLICATION_SUBMIT_EXECUTE_TASK);
        createResourceScopePermission(ResourceType.REQUEST_TASK, RequestTaskType.INSTALLATION_ACCOUNT_OPENING_ARCHIVE.name(), Scope.REQUEST_TASK_VIEW, Permission.PERM_INSTALLATION_ACCOUNT_OPENING_ARCHIVE_VIEW_TASK);
        createResourceScopePermission(ResourceType.REQUEST_TASK, RequestTaskType.INSTALLATION_ACCOUNT_OPENING_ARCHIVE.name(), Scope.REQUEST_TASK_EXECUTE, Permission.PERM_INSTALLATION_ACCOUNT_OPENING_ARCHIVE_EXECUTE_TASK);

        flushAndClear();

        Map<Long, Set<String>> result =
            repo.findResourceSubTypesOperatorUserHasScopeByAccounts(USER1, Set.of(account1, account2, account3),
                ResourceType.REQUEST_TASK, Scope.REQUEST_TASK_VIEW);

        assertThat(result)
            .containsExactlyInAnyOrderEntriesOf(
                Map.of(
                    account1, Set.of(RequestTaskType.PERMIT_ISSUANCE_APPLICATION_SUBMIT.name(), RequestTaskType.INSTALLATION_ACCOUNT_OPENING_ARCHIVE.name()),
                    account2, Set.of(RequestTaskType.PERMIT_ISSUANCE_APPLICATION_SUBMIT.name())
                )
            );
    }
	
    private void createResourceScopePermission(ResourceType resourceType, String resourceSubType, Scope scope, Permission permission) {
        ResourceScopePermission resource = ResourceScopePermission.builder()
            .resourceType(resourceType)    
            .resourceSubType(resourceSubType)
            .scope(scope)
            .permission(permission)
            .build();
        entityManager.persist(resource);
    }
	
	private void flushAndClear() {
		entityManager.flush();
		entityManager.clear();
	}
}
