package uk.gov.pmrv.api.authorization.core.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static uk.gov.pmrv.api.authorization.core.domain.AuthorityStatus.ACTIVE;
import static uk.gov.pmrv.api.authorization.core.domain.AuthorityStatus.DISABLED;
import static uk.gov.pmrv.api.authorization.core.domain.AuthorityStatus.PENDING;
import static uk.gov.pmrv.api.authorization.core.domain.Permission.PERM_INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW_EXECUTE_TASK;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.Optional;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;
import org.testcontainers.junit.jupiter.Testcontainers;
import uk.gov.pmrv.api.AbstractContainerBaseTest;
import uk.gov.pmrv.api.authorization.AuthorityConstants;
import uk.gov.pmrv.api.authorization.core.domain.Authority;
import uk.gov.pmrv.api.authorization.core.domain.AuthorityPermission;
import uk.gov.pmrv.api.authorization.core.domain.AuthorityStatus;
import uk.gov.pmrv.api.authorization.core.domain.Permission;
import uk.gov.pmrv.api.authorization.core.domain.Role;
import uk.gov.pmrv.api.authorization.core.domain.dto.AuthorityRoleDTO;
import uk.gov.pmrv.api.authorization.rules.domain.ResourceScopePermission;
import uk.gov.pmrv.api.authorization.rules.domain.ResourceType;
import uk.gov.pmrv.api.authorization.rules.domain.Scope;
import uk.gov.pmrv.api.common.domain.enumeration.CompetentAuthority;
import uk.gov.pmrv.api.common.domain.enumeration.RoleType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@Testcontainers
@DataJpaTest
@Import(ObjectMapper.class)
class AuthorityRepositoryIT extends AbstractContainerBaseTest {

	@Autowired
	private AuthorityRepository repo;
	
	@Autowired
	private EntityManager entityManager;
	
    @Sql(statements = {
        "INSERT INTO au_authority (id, user_id, code, status, account_id, competent_authority, creation_date, created_by) VALUES (1, 'regulator_user_1', 'regulator', 'ACTIVE', null, 'ENGLAND', NOW(), 'regulator_user_1')",
        "INSERT INTO au_authority (id, user_id, code, status, account_id, competent_authority, creation_date, created_by) VALUES (2, 'regulator_user_1', 'regulator_admin', 'ACTIVE', null, 'ENGLAND', NOW(), 'regulator_user_1')",
        "INSERT INTO au_authority_permission (id, authority_id, permission) VALUES (1, 1, 'PERM_INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW_VIEW_TASK')",
        "INSERT INTO au_authority_permission (id, authority_id, permission) VALUES (2, 1, 'PERM_INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW_EXECUTE_TASK')",
        "INSERT INTO au_authority_permission (id, authority_id, permission) VALUES (3, 2, 'PERM_INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW_VIEW_TASK')",
        "INSERT INTO au_authority_permission (id, authority_id, permission) VALUES (4, 2, 'PERM_INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW_EXECUTE_TASK')"
    })
    @Test
    void findPermissionsByUserId() {
        final String userId = "regulator_user_1";
        List<Permission> permissions = repo.findAssignedPermissionsByUserId(userId);
        assertThat(permissions).hasSize(2).containsOnly(
            Permission.PERM_INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW_VIEW_TASK,
            PERM_INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW_EXECUTE_TASK);
    }
    
    @Sql(statements = {
            "INSERT INTO au_authority (id, user_id, code, status, account_id, competent_authority, creation_date, created_by) VALUES (-11, 'regulator_user_1', 'regulator', 'ACTIVE', null, 'ENGLAND', NOW(), 'regulator_user_1')",
            "INSERT INTO au_authority_permission (id, authority_id, permission) VALUES (-11, -11, 'PERM_ACCOUNT_USERS_EDIT')",
            "INSERT INTO au_authority_permission (id, authority_id, permission) VALUES (-12, -11, 'PERM_CA_USERS_EDIT')",
            "INSERT INTO au_authority_permission (id, authority_id, permission) VALUES (-13, -11, 'PERM_VB_MANAGE')",
            "INSERT INTO au_authority_permission (id, authority_id, permission) VALUES (-14, -11, 'PERM_TASK_ASSIGNMENT')"
        })
    @Test
    void findByCompetentAuthorityAndPmrvSuperUserRolePermissions() {
        List<Authority> authorities = repo.findByCompetentAuthorityAndPmrvSuperUserRolePermissions(CompetentAuthority.ENGLAND);
        assertThat(authorities).hasSize(1);
    }

    @Sql(statements = {
        "INSERT INTO au_authority (id, user_id, code, status, account_id, competent_authority, creation_date, created_by) VALUES (1, 'operator_user_1', 'operator_admin', 'ACTIVE', 1, null, NOW(), 'operator_user_1')",
        "INSERT INTO au_authority (id, user_id, code, status, account_id, competent_authority, creation_date, created_by) VALUES (2, 'operator_user_1', 'operator', 'ACTIVE', 1, null, NOW(), 'operator_user_1')",
        "INSERT INTO au_authority (id, user_id, code, status, account_id, competent_authority, creation_date, created_by) VALUES (3, 'operator_user_1', 'operator_assistant', 'DISABLED', 1, null, NOW(), 'operator_user_1')",
        "INSERT INTO au_authority_permission (id, authority_id, permission) VALUES (1, 1, 'PERM_PERMIT_ISSUANCE_APPLICATION_SUBMIT_EXECUTE_TASK')",
        "INSERT INTO au_authority_permission (id, authority_id, permission) VALUES (2, 1, 'PERM_INSTALLATION_ACCOUNT_OPENING_ARCHIVE_VIEW_TASK')",
        "INSERT INTO au_authority_permission (id, authority_id, permission) VALUES (3, 2, 'PERM_INSTALLATION_ACCOUNT_OPENING_ARCHIVE_EXECUTE_TASK')",
        "INSERT INTO au_authority_permission (id, authority_id, permission) VALUES (4, 3, 'PERM_INSTALLATION_ACCOUNT_OPENING_ARCHIVE_VIEW_TASK')",
    })
    @Test
    void findByUserIdAndStatusWhenStatusActive() {
        final String userId = "operator_user_1";
        List<Authority> authorities = repo.findByUserIdAndStatus(userId, ACTIVE);

        assertThat(authorities).hasSize(2);
    }

    @Sql(statements = {
        "INSERT INTO au_authority (id, user_id, code, status, account_id, competent_authority, creation_date, created_by) VALUES (1, 'operator_user_1', 'operator_admin', 'ACTIVE', 1, null, NOW(), 'operator_user_1')",
        "INSERT INTO au_authority (id, user_id, code, status, account_id, competent_authority, creation_date, created_by) VALUES (2, 'operator_user_1', 'operator', 'DISABLED', 1, null, NOW(), 'operator_user_1')",
        "INSERT INTO au_authority_permission (id, authority_id, permission) VALUES (1, 1, 'PERM_PERMIT_ISSUANCE_APPLICATION_SUBMIT_EXECUTE_TASK')",
        "INSERT INTO au_authority_permission (id, authority_id, permission) VALUES (2, 2, 'PERM_INSTALLATION_ACCOUNT_OPENING_ARCHIVE_VIEW_TASK')",
        "INSERT INTO au_authority_permission (id, authority_id, permission) VALUES (3, 2, 'PERM_INSTALLATION_ACCOUNT_OPENING_ARCHIVE_EXECUTE_TASK')",

    })
    @Test
    void findByUserIdAndStatusAndAuthorityPermissionsAssignedTrueWhenStatusDisabled() {
        final String userId = "operator_user_1";
        List<Authority> authorities = repo.findByUserIdAndStatus(userId, DISABLED);

        assertThat(authorities).hasSize(1);
    }

    @Test
    void findByUserIdAndStatusAndAuthorityPermissionsAssignedTrueForInvalidUser() {
        List<Authority> authorities =
            repo.findByUserIdAndStatus("invalid_user", DISABLED);

        assertTrue(authorities.isEmpty());
    }
    
    @Test
    void findOperatorUserAuthorityRoleListByAccount() {
    	//prepare data
    	String roleOperatorAdminCode = "operator_admin";
    	Role roleOperatorAdmin = createRole(roleOperatorAdminCode, RoleType.OPERATOR, "Operator admin");
    	
    	Long account1 = 1L;
    	Long account2 = 2L;
    	
    	createOperatorAuthority("user1", roleOperatorAdminCode, AuthorityStatus.ACTIVE, account1);
    	createOperatorAuthority("user2", roleOperatorAdminCode, AuthorityStatus.ACTIVE, account2);
    	createRegulatorAuthority("user3", "regulator", AuthorityStatus.ACTIVE, CompetentAuthority.ENGLAND);
    	
    	flushAndClear();
    	
    	// invoke
    	List<AuthorityRoleDTO> authorityRoleList = repo.findOperatorUserAuthorityRoleListByAccount(account1);
    	
    	//assert
    	assertThat(authorityRoleList).hasSize(1);
    	assertThat(authorityRoleList.get(0).getUserId()).isEqualTo("user1");
    	assertThat(authorityRoleList.get(0).getRoleName()).isEqualTo(roleOperatorAdmin.getName());
    	assertThat(authorityRoleList.get(0).getRoleCode()).isEqualTo(roleOperatorAdmin.getCode());
    	assertThat(authorityRoleList.get(0).getAuthorityStatus()).isEqualTo(ACTIVE);
    	assertThat(authorityRoleList.get(0).getCreationDate()).isNotNull();
    }
    
    @Test
    void findByUserIdAndAccountId() {
    	//prepare data
    	final String user = "user";
    	String roleOperatorAdminCode = "operator_admin";
    	String roleOperatorCode = "operator";
    	
    	Long account1 = 1L;
    	Long account2 = 2L;
    	
    	Authority operatorAdminAuthority = 
    			createOperatorAuthority(user, roleOperatorAdminCode, AuthorityStatus.ACTIVE, account1);
    	createOperatorAuthority(user, roleOperatorCode, AuthorityStatus.ACTIVE, account2);
    	
    	flushAndClear();
    	
    	// invoke
    	Optional<Authority> resultOptional = repo.findByUserIdAndAccountId(user, account1);
    	
    	//assert
    	assertThat(operatorAdminAuthority).isEqualTo(resultOptional.get());
    }
    
    @Test
    void existsByUserIdAndCompetentAuthority() {
    	final String user = "user";
    	final CompetentAuthority ca = CompetentAuthority.ENGLAND;
    	//assert before
    	boolean result = repo.existsByUserIdAndCompetentAuthority(user, ca);
    	assertThat(result).isFalse();
    	
    	//create authority
    	createRegulatorAuthority(user, "regulator", AuthorityStatus.ACTIVE, ca);
    	
    	flushAndClear();
    	
    	result = repo.existsByUserIdAndCompetentAuthority(user, ca);
    	
    	//assert after
    	assertThat(result).isTrue();
    }
    
    @Test
    void existsByUserId_true() {
    	final String user = "user";

    	//create authority
    	createRegulatorAuthority(user, "regulator", AuthorityStatus.ACTIVE, CompetentAuthority.ENGLAND);
    	
    	flushAndClear();
    	
    	boolean result = repo.existsByUserId(user);
    	
    	//assert after
    	assertThat(result).isTrue();
    }
    
    @Test
    void existsByUserId_false() {
    	final String user = "user";

    	boolean result = repo.existsByUserId(user);
    	
    	//assert after
    	assertThat(result).isFalse();
    }
    
    @Test
    void deleteByUserIdAndCompetentAuthority() {
    	final String user = "user";
    	final String anotherUser = "anotheruser";
    	Authority auth1 = createRegulatorAuthority(user, "regulator", AuthorityStatus.ACTIVE, CompetentAuthority.ENGLAND);
    	Authority auth2 = createRegulatorAuthority(anotherUser, "regulator", AuthorityStatus.ACTIVE, CompetentAuthority.ENGLAND);
    	
    	repo.deleteByUserId(user);
    	
    	flushAndClear();
    	
    	//assert
    	assertTrue(repo.findById(auth1.getId()).isEmpty());
		assertTrue(repo.findById(auth2.getId()).isPresent());
    }
    
    private Role createRole(String code, RoleType type, String name) {
    	Role role =
    			Role.builder()
    				.code(code)
    				.type(type)
    				.name(name)
    				.build();
    	entityManager.persist(role);
    	return role;
    }

	@Test
	void existsOtherAccountOperatorAdmin_false() {
		String userId = "oper1";
		Long accountId = 1L;
		createOperatorAuthority(userId, "operator_admin", ACTIVE, accountId);

		flushAndClear();

		assertThat(repo.existsOtherAccountOperatorAdmin(userId, accountId)).isFalse();
	}

	@Test
	void existsOtherAccountOperatorAdmin_true() {
		String userId1 = "oper1";
		String userId2 = "oper2";
		Long accountId = 1L;
		createOperatorAuthority(userId1, "operator_admin", ACTIVE, accountId);
		createOperatorAuthority(userId2, "operator_admin", ACTIVE, accountId);

		flushAndClear();

		assertThat(repo.existsOtherAccountOperatorAdmin(userId1, accountId)).isTrue();
	}

    @Test
    void existsOtherVerificationBodyAdmin_false() {
        String userId = "verifier1";
        Long verificationBodyId = 1L;
        createVerifierAuthority(userId, "verifier_admin", ACTIVE, verificationBodyId);

        flushAndClear();

        assertThat(repo.existsOtherVerificationBodyAdmin(userId, verificationBodyId)).isFalse();
    }

    @Test
    void existsOtherVerificationBodyAdmin_true() {
        String userId1 = "oper1";
        String userId2 = "oper2";
        Long verificationBodyId = 1L;
        createVerifierAuthority(userId1, "verifier_admin", ACTIVE, verificationBodyId);
        createVerifierAuthority(userId2, "verifier_admin", ACTIVE, verificationBodyId);

        flushAndClear();

        assertThat(repo.existsOtherVerificationBodyAdmin(userId1, verificationBodyId)).isTrue();
    }
	
	@Test
	void findVerifierUserAuthorityRoleListByVerificationBody() {
		Long vb = 1L;
		String roleVerifierAdminCode = "verifier_admin";
    	createRole(roleVerifierAdminCode, RoleType.VERIFIER, "Verifier admin");
		
		createVerifierAuthority("user1", roleVerifierAdminCode, AuthorityStatus.ACTIVE, vb);
		createVerifierAuthority("user2", roleVerifierAdminCode, AuthorityStatus.ACTIVE, vb);
		createVerifierAuthority("user3", roleVerifierAdminCode, AuthorityStatus.ACTIVE, 2L);
		
		flushAndClear();
		
		//invoke
		List<AuthorityRoleDTO> result = repo.findVerifierUserAuthorityRoleListByVerificationBody(vb);
		
		//assert
		assertThat(result).hasSize(2);
		assertThat(result)
				.extracting(AuthorityRoleDTO::getUserId)
				.containsExactlyInAnyOrder("user1", "user2")
				;
	}
	
	@Test
	void findActiveOperatorUsersByAccountAndRoleCode() {
		//prepare data
		String roleCode = AuthorityConstants.OPERATOR_ADMIN_ROLE_CODE;
		Long account1 = 1L;
		Long anotherAccount = 2L;
		
		createOperatorAuthority("user1", roleCode, AuthorityStatus.ACTIVE, account1);
		createOperatorAuthority("user2", roleCode, AuthorityStatus.PENDING, account1);
        createOperatorAuthority("user3", roleCode, AuthorityStatus.ACTIVE, account1);
		createOperatorAuthority("user3", "another_role", AuthorityStatus.ACTIVE, account1);
		createOperatorAuthority("user4", "roleCode", AuthorityStatus.ACTIVE, anotherAccount);
		
		//invoke
		List<String> usersFound = repo.findActiveOperatorUsersByAccountAndRoleCode(account1, roleCode);
		
		//assert
		assertThat(usersFound).containsExactlyInAnyOrder("user1", "user3");
	}

	@Test
	void findByAccountIdAndCodeIn() {
	    String operator = "operator";
	    String consultant = "consultant";
	    String agent = "agent";

	    
        createOperatorAuthority("user1", operator, AuthorityStatus.ACTIVE, 1L);
        createOperatorAuthority("user2", consultant, AuthorityStatus.DISABLED, 1L);
        createOperatorAuthority("user3", agent, AuthorityStatus.ACTIVE, 1L);
        createOperatorAuthority("user4", consultant, AuthorityStatus.ACTIVE, 2L);

        List<Authority> expectedAuthorities = repo.findByAccountIdAndCodeIn(1L, List.of(consultant, agent));

        assertThat(expectedAuthorities).hasSize(2);
        assertThat(expectedAuthorities)
            .extracting(Authority::getUserId)
            .containsExactlyInAnyOrder("user2", "user3");

    }
	
	@Test
    void findRegulatorUsersWithScopeOnResourceTypeAndResourceSubTypeAndCA() {
        createResourceScopePermission(
                ResourceType.REQUEST_TASK, 
                RequestTaskType.INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW.name(), 
                Scope.REQUEST_TASK_EXECUTE, 
                Permission.PERM_INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW_EXECUTE_TASK);
        
        String user1 = "user1";
        Authority authorityUser1England = 
                Authority.builder()
                    .userId(user1)
                    .code("regularator_admin")
                    .status(AuthorityStatus.ACTIVE)
                    .competentAuthority(CompetentAuthority.ENGLAND)
                    .createdBy(user1)
                    .build();
        authorityUser1England
            .addPermission(
                    AuthorityPermission.builder()
                                    .permission(Permission.PERM_INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW_EXECUTE_TASK)
                                    .build());
        
        Authority authorityUser1Wales = 
                Authority.builder()
                    .userId(user1)
                    .code("regularator_admin")
                    .status(AuthorityStatus.ACTIVE)
                    .competentAuthority(CompetentAuthority.WALES)
                    .createdBy(user1)
                    .build();
        authorityUser1Wales
            .addPermission(
                    AuthorityPermission.builder()
                                    .permission(Permission.PERM_INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW_EXECUTE_TASK)
                                    .build());

        String user2 = "user2";
        Authority authorityUser2England = 
                Authority.builder()
                    .userId(user2)
                    .code("regularator_admin")
                    .status(AuthorityStatus.ACTIVE)
                    .competentAuthority(CompetentAuthority.ENGLAND)
                    .createdBy(user2)
                    .build();
        authorityUser2England
            .addPermission(
                    AuthorityPermission.builder()
                                    .permission(Permission.PERM_VB_USERS_EDIT)
                                    .build());
        
        String user3 = "user3";
        Authority authorityUser3Wales = 
                Authority.builder()
                    .userId(user3)
                    .code("regularator_admin")
                    .status(AuthorityStatus.ACTIVE)
                    .competentAuthority(CompetentAuthority.WALES)
                    .createdBy(user3)
                    .build();
        authorityUser3Wales
            .addPermission(
                    AuthorityPermission.builder()
                                    .permission(Permission.PERM_INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW_EXECUTE_TASK)
                                    .build());

        entityManager.persist(authorityUser1England);
        entityManager.persist(authorityUser1Wales);
        entityManager.persist(authorityUser2England);
        entityManager.persist(authorityUser3Wales);
        
        
        ResourceType resourceType = ResourceType.REQUEST_TASK; 
        String resourceSubType = RequestTaskType.INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW.name(); 
        Scope scope = Scope.REQUEST_TASK_EXECUTE; 
        CompetentAuthority competentAuthority = CompetentAuthority.ENGLAND;
        List<String> usersFound = repo.findRegulatorUsersWithScopeOnResourceTypeAndResourceSubTypeAndCA(resourceType, resourceSubType, scope, competentAuthority);
        assertThat(usersFound).containsExactly(user1);
    }
	
	@Test
    void findVerifierUsersWithScopeOnResourceTypeAndResourceSubTypeAndVerificationBodyId() {
        createResourceScopePermission(
                ResourceType.REQUEST_TASK, 
                RequestTaskType.INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW.name(), 
                Scope.REQUEST_TASK_EXECUTE, 
                Permission.PERM_VB_USERS_EDIT);
        
        String user1 = "user1";
        Long verificationBodyId1 = 1L;
        Authority authorityUser1Vb1 = 
                Authority.builder()
                    .userId(user1)
                    .code("verifier_admin")
                    .status(AuthorityStatus.ACTIVE)
                    .verificationBodyId(verificationBodyId1)
                    .createdBy(user1)
                    .build();
        authorityUser1Vb1
            .addPermission(
                    AuthorityPermission.builder()
                                    .permission(Permission.PERM_VB_USERS_EDIT)
                                    .build());
        
        String user2 = "user2";
        Long verificationBodyId2 = 2L;
        Authority authorityUser2Vb2 = 
                Authority.builder()
                    .userId(user2)
                    .code("verifier_admin")
                    .status(AuthorityStatus.ACTIVE)
                    .verificationBodyId(verificationBodyId2)
                    .createdBy(user2)
                    .build();
        authorityUser2Vb2
            .addPermission(
                    AuthorityPermission.builder()
                                    .permission(Permission.PERM_VB_USERS_EDIT)
                                    .build());
        
        entityManager.persist(authorityUser1Vb1);
        entityManager.persist(authorityUser2Vb2);
        
        
        ResourceType resourceType = ResourceType.REQUEST_TASK; 
        String resourceSubType = RequestTaskType.INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW.name(); 
        Scope scope = Scope.REQUEST_TASK_EXECUTE; 
        List<String> usersFound = repo.findVerifierUsersWithScopeOnResourceTypeAndResourceSubTypeAndVerificationBodyId(resourceType, resourceSubType, scope, verificationBodyId1);
        assertThat(usersFound).containsExactly(user1);
    }
	
	@Test
    void findOperatorUsersWithScopeOnResourceTypeAndResourceSubTypeAndAccountId() {
        //prepare data
	    createResourceScopePermission(
                ResourceType.REQUEST_TASK, 
                RequestTaskType.INSTALLATION_ACCOUNT_OPENING_ARCHIVE.name(), 
                Scope.REQUEST_TASK_EXECUTE, 
                Permission.PERM_INSTALLATION_ACCOUNT_OPENING_ARCHIVE_EXECUTE_TASK);
	    
	    Long account1 = 1L;
	    Long account2 = 2L;
	    
        String user1 = "user1";
        Authority authorityUser1Account1 = 
                Authority.builder()
                    .userId(user1)
                    .code("operator_admin")
                    .status(AuthorityStatus.ACTIVE)
                    .accountId(account1)
                    .createdBy(user1)
                    .build();
        authorityUser1Account1
            .addPermission(
                    AuthorityPermission.builder()
                                    .permission(Permission.PERM_INSTALLATION_ACCOUNT_OPENING_ARCHIVE_EXECUTE_TASK)
                                    .build());
        
        String user2 = "user2";
        Authority authorityUser2Account1 = 
                Authority.builder()
                    .userId(user2)
                    .code("operator_admin")
                    .status(AuthorityStatus.ACTIVE)
                    .accountId(account2)
                    .createdBy(user2)
                    .build();
        authorityUser2Account1
            .addPermission(
                    AuthorityPermission.builder()
                                    .permission(Permission.PERM_TASK_ASSIGNMENT)
                                    .build());
        
        String user3 = "user3";
        Authority authorityUser3Account2 = 
                Authority.builder()
                    .userId(user2)
                    .code("operator_admin")
                    .status(AuthorityStatus.ACTIVE)
                    .accountId(account2)
                    .createdBy(user3)
                    .build();
        authorityUser3Account2
            .addPermission(
                    AuthorityPermission.builder()
                                    .permission(Permission.PERM_INSTALLATION_ACCOUNT_OPENING_ARCHIVE_EXECUTE_TASK)
                                    .build());

        entityManager.persist(authorityUser1Account1);
        entityManager.persist(authorityUser2Account1);
        entityManager.persist(authorityUser3Account2);
        
        flushAndClear();
        
        //execute
        ResourceType resourceType = ResourceType.REQUEST_TASK; 
        String resourceSubType = RequestTaskType.INSTALLATION_ACCOUNT_OPENING_ARCHIVE.name(); 
        Scope scope = Scope.REQUEST_TASK_EXECUTE; 
        List<String> usersFound = repo.findOperatorUsersWithScopeOnResourceTypeAndResourceSubTypeAndAccountId(resourceType, resourceSubType, scope, account1);

        //assert
        assertThat(usersFound).containsExactly(user1);
    }

    @Test
    void findByUuidAndStatus() {
        final String uuid = "uuid";
        final String userId = "userId";

        Authority expectedAuthority = Authority.builder().userId(userId).uuid(uuid).status(PENDING).createdBy(userId).build();
        Authority anotherAuthority = Authority.builder().userId(userId).uuid("anotherUuid").status(ACTIVE).createdBy(userId).build();

        entityManager.persist(expectedAuthority);
        entityManager.persist(anotherAuthority);
        flushAndClear();

        Optional<Authority> optionalActualAuthority = repo.findByUuidAndStatus(uuid, PENDING);

        assertThat(optionalActualAuthority).isNotEmpty();
        assertEquals(expectedAuthority, optionalActualAuthority.get());
    }

    @Test
    void findByUuidAndStatus_returns_empty_optional() {
        final String uuid = "uuid";
        final String userId = "userId";

        Authority authority = Authority.builder().userId(userId).uuid("anotherUuid").status(ACTIVE).createdBy(userId).build();

        entityManager.persist(authority);
        flushAndClear();

        Optional<Authority> optionalActualAuthority = repo.findByUuidAndStatus(uuid, PENDING);

        assertThat(optionalActualAuthority).isEmpty();
    }

    @Test
    void findByCompetentAuthority() {
        Authority expectedRegulatorAuthority =
            createRegulatorAuthority("reg1", "regulator", ACTIVE, CompetentAuthority.ENGLAND);
        createRegulatorAuthority("reg2", "regulator", ACTIVE, CompetentAuthority.WALES);
        flushAndClear();

        // invoke
        List<Authority> actualAuthorities = repo.findByCompetentAuthority(CompetentAuthority.ENGLAND);

        //assert
        assertThat(actualAuthorities).hasSize(1);
        assertEquals(expectedRegulatorAuthority, actualAuthorities.get(0));
    }

    @Test
    void findByCompetentAuthorityAndStatusNot() {
        Authority expectedRegulatorAuthority = createRegulatorAuthority("reg1", "regulator", ACTIVE, CompetentAuthority.ENGLAND);
        createRegulatorAuthority("reg2", "regulator", PENDING, CompetentAuthority.ENGLAND);
        createRegulatorAuthority("reg3", "regulator", ACTIVE, CompetentAuthority.WALES);
        flushAndClear();

        // invoke
        List<Authority> actualAuthorities = repo.findByCompetentAuthorityAndStatusNot(CompetentAuthority.ENGLAND, PENDING);

        //assert
        assertThat(actualAuthorities).hasSize(1);
        assertEquals(expectedRegulatorAuthority, actualAuthorities.get(0));
    }

    @Test
    void findNonPendingOperatorUserAuthorityRoleListByAccount() {
        String roleOperatorAdminCode = "operator_admin";
        Role roleOperatorAdmin = createRole(roleOperatorAdminCode, RoleType.OPERATOR, "Operator admin");

        createOperatorAuthority("user1", roleOperatorAdminCode, ACTIVE, 1L);
        createOperatorAuthority("user2", roleOperatorAdminCode, PENDING, 2L);
        createOperatorAuthority("user3", roleOperatorAdminCode, ACTIVE, 3L);

        flushAndClear();

        // invoke
        List<AuthorityRoleDTO> actualAuthorityRoleList =
            repo.findNonPendingOperatorUserAuthorityRoleListByAccount(1L);

        //assert
        assertThat(actualAuthorityRoleList).hasSize(1);
        assertThat(actualAuthorityRoleList.get(0).getUserId()).isEqualTo("user1");
        assertThat(actualAuthorityRoleList.get(0).getRoleName()).isEqualTo(roleOperatorAdmin.getName());
        assertThat(actualAuthorityRoleList.get(0).getRoleCode()).isEqualTo(roleOperatorAdmin.getCode());
        assertThat(actualAuthorityRoleList.get(0).getAuthorityStatus()).isEqualTo(ACTIVE);
        assertThat(actualAuthorityRoleList.get(0).getCreationDate()).isNotNull();
    }

    @Test
    void findNonPendingVerifierUserAuthorityRoleListByVerificationBody() {
        String roleVerifierAdminCode = "verifier_admin";
        Role verifierAdminRole = createRole(roleVerifierAdminCode, RoleType.VERIFIER, "Verifier admin");

        createVerifierAuthority("user1", roleVerifierAdminCode, ACTIVE, 1L);
        createVerifierAuthority("user2", roleVerifierAdminCode, PENDING, 1L);
        createVerifierAuthority("user3", roleVerifierAdminCode,ACTIVE, 2L);

        flushAndClear();

        //invoke
        List<AuthorityRoleDTO> actualAuthorityRoleList =
            repo.findNonPendingVerifierUserAuthorityRoleListByVerificationBody(1L);

        //assert
        assertThat(actualAuthorityRoleList).hasSize(1);
        assertThat(actualAuthorityRoleList.get(0).getUserId()).isEqualTo("user1");
        assertThat(actualAuthorityRoleList.get(0).getRoleName()).isEqualTo(verifierAdminRole.getName());
        assertThat(actualAuthorityRoleList.get(0).getRoleCode()).isEqualTo(verifierAdminRole.getCode());
        assertThat(actualAuthorityRoleList.get(0).getAuthorityStatus()).isEqualTo(ACTIVE);
        assertThat(actualAuthorityRoleList.get(0).getCreationDate()).isNotNull();
    }

    @Test
    void findByAccountIds() {
        String roleOperatorAdminCode = "operator_admin";
        createRole(roleOperatorAdminCode, RoleType.OPERATOR, "Operator admin");

        createOperatorAuthority("user1", roleOperatorAdminCode, ACTIVE, 1L);
        createOperatorAuthority("user2", roleOperatorAdminCode, PENDING, 2L);
        createOperatorAuthority("user3", roleOperatorAdminCode, ACTIVE, 3L);

        flushAndClear();

        // invoke
        List<Authority> authorities =
            repo.findByAccountIdIn(List.of(1L, 2L, 3L));

        //assert
        assertThat(authorities).hasSize(3);
        assertThat(authorities.get(0).getUserId()).isEqualTo("user1");
        assertThat(authorities.get(0).getAuthority()).isEqualTo(AuthorityConstants.OPERATOR_ADMIN_ROLE_CODE);
        assertThat(authorities.get(0).getCreationDate()).isNotNull();
        assertThat(authorities.get(0).getStatus()).isEqualTo(ACTIVE);
        assertThat(authorities.get(0).getAccountId()).isNotNull();
        assertThat(authorities.get(0).getCompetentAuthority()).isNull();
    }
    
	private ResourceScopePermission createResourceScopePermission(
            ResourceType resourceType, String resourceSubType, Scope scope, Permission permission) {
        ResourceScopePermission resource = ResourceScopePermission.builder()
            .resourceType(resourceType)    
            .resourceSubType(resourceSubType)
            .scope(scope)
            .permission(permission)
            .build();
        entityManager.persist(resource);
        return resource;
    }
	
    private Authority createOperatorAuthority(String userId, String code, AuthorityStatus authorityStatus, Long accountId) {
		return createAuthority(userId, code, authorityStatus, accountId, null, null);
	}
    
    private Authority createRegulatorAuthority(String userId, String code, AuthorityStatus authorityStatus, CompetentAuthority ca) {
		return createAuthority(userId, code, authorityStatus, null, ca, null);
	}
    
    private Authority createVerifierAuthority(String userId, String code, AuthorityStatus authorityStatus, Long verificationBodyId) {
		return createAuthority(userId, code, authorityStatus, null, null, verificationBodyId);
	}
	
	private Authority createAuthority(String userId, String code, AuthorityStatus authorityStatus, 
			Long accountId, 
			CompetentAuthority ca,
			Long verificationBodyId) {
		Authority authority = Authority.builder()
            .userId(userId)
            .code(code)
            .status(authorityStatus)
            .accountId(accountId)
            .competentAuthority(ca)
            .verificationBodyId(verificationBodyId)
            .createdBy(userId)
            .build();

		entityManager.persist(authority);
		return authority;
	}
	
	private void flushAndClear() {
		entityManager.flush();
		entityManager.clear();
	}
}
