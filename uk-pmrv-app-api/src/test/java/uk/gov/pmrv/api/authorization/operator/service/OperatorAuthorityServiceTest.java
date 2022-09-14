package uk.gov.pmrv.api.authorization.operator.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.pmrv.api.common.domain.enumeration.RoleType.OPERATOR;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.account.domain.dto.AccountDTO;
import uk.gov.pmrv.api.account.service.AccountQueryService;
import uk.gov.pmrv.api.authorization.AuthorityConstants;
import uk.gov.pmrv.api.authorization.core.domain.Authority;
import uk.gov.pmrv.api.authorization.core.domain.AuthorityStatus;
import uk.gov.pmrv.api.authorization.core.domain.Permission;
import uk.gov.pmrv.api.authorization.core.domain.Role;
import uk.gov.pmrv.api.authorization.core.domain.RolePermission;
import uk.gov.pmrv.api.authorization.core.domain.dto.AuthorityRoleDTO;
import uk.gov.pmrv.api.authorization.core.domain.dto.UserRoleTypeDTO;
import uk.gov.pmrv.api.authorization.core.service.AuthorityAssignmentService;
import uk.gov.pmrv.api.authorization.core.service.UserRoleTypeService;
import uk.gov.pmrv.api.authorization.core.repository.AuthorityRepository;
import uk.gov.pmrv.api.authorization.core.repository.RoleRepository;
import uk.gov.pmrv.api.common.domain.enumeration.RoleType;
import uk.gov.pmrv.api.common.domain.model.PmrvUser;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;

@ExtendWith(MockitoExtension.class)
class OperatorAuthorityServiceTest {
	
	@InjectMocks
	private OperatorAuthorityService operatorAuthorityService;
	
	@Mock
	private AuthorityAssignmentService authorityAssignmentService;
	
	@Mock
	private AuthorityRepository authorityRepository;
	
	@Mock
	private RoleRepository roleRepository;

	@Mock
	private UserRoleTypeService userRoleTypeService;

	@Test
	void createOperatorAdminAuthority() {
		String code = "code";
		String userId = "userId";
		final Permission permission = Permission.PERM_INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW_VIEW_TASK;

		Long accountId = 1L;
		Role role = createRole(code, OPERATOR, permission);
		Optional<Role> roleOptional = Optional.of(role);


		when(roleRepository.findByCode(AuthorityConstants.OPERATOR_ADMIN_ROLE_CODE)).thenReturn(roleOptional);
        when(userRoleTypeService.getUserRoleTypeByUserId(userId)).thenReturn(createUserRoleTypeDTO(userId, OPERATOR));
		
		//invoke
		operatorAuthorityService.createOperatorAdminAuthority(accountId, userId);

		//verify
		verify(roleRepository, times(1)).findByCode(AuthorityConstants.OPERATOR_ADMIN_ROLE_CODE);

        ArgumentCaptor<Authority> authorityCaptor = ArgumentCaptor.forClass(Authority.class);
        verify(authorityAssignmentService, times(1)).createAuthorityPermissionsForRole(authorityCaptor.capture(), eq(role));
        Authority authorityThatSaved = authorityCaptor.getValue();
        assertThat(authorityThatSaved).isNotNull();
        assertThat(authorityThatSaved.getCode()).isEqualTo(code);
        assertThat(authorityThatSaved.getUserId()).isEqualTo(userId);
        assertThat(authorityThatSaved.getAccountId()).isEqualTo(accountId);
        assertThat(authorityThatSaved.getCompetentAuthority()).isNull();
        assertThat(authorityThatSaved.getVerificationBodyId()).isNull();
	}
	
	@Test
	void createOperatorAdminAuthority_role_not_found() {
        String userId = "userId";
        Long accountId = 1L;
		Optional<Role> roleOptional = Optional.empty();

		when(roleRepository.findByCode(AuthorityConstants.OPERATOR_ADMIN_ROLE_CODE)).thenReturn(roleOptional);
		
		// assertions
		assertThrows(BusinessException.class, 
				() -> operatorAuthorityService.createOperatorAdminAuthority(accountId, userId));
		verify(authorityRepository, never()).save(Mockito.any(Authority.class));
	}

	@Test
	void createOperatorAdminAuthorityThrowsExceptionWhenAuthCreationNotAllowed() {
	    String userId = "userId";
		String code = "code";
		Long accountId = 1L;
		Role regulatorRole = createRole(code, RoleType.REGULATOR);
		Optional<Role> optionalRegulatorRole = Optional.of(regulatorRole);

		when(roleRepository.findByCode(AuthorityConstants.OPERATOR_ADMIN_ROLE_CODE)).thenReturn(optionalRegulatorRole);
		when(userRoleTypeService.getUserRoleTypeByUserId(userId)).thenReturn(createUserRoleTypeDTO(userId, OPERATOR));

		BusinessException businessException = assertThrows(BusinessException.class,
			() -> operatorAuthorityService.createOperatorAdminAuthority(accountId, userId));

		assertEquals(ErrorCode.AUTHORITY_CREATION_NOT_ALLOWED, businessException.getErrorCode());
	}
	
    @Test
    void findOperatorUserAuthorityRoleListByAccount() {
        Long accountId = 1L;

    	List<AuthorityRoleDTO> authorityRoleList = List.of(
		    	AuthorityRoleDTO.builder()
		    		.userId("user1")
		    		.roleName("operator_admin")
		    		.authorityStatus(AuthorityStatus.ACTIVE)
		    		.build());
    	
    	when(authorityRepository.findOperatorUserAuthorityRoleListByAccount(accountId))
    			.thenReturn(authorityRoleList);
    	
    	//invoke
    	List<AuthorityRoleDTO> authorityRoleListFound = operatorAuthorityService.findOperatorUserAuthorityRoleListByAccount(accountId);
    	
    	//verify
    	verify(authorityRepository, times(1)).findOperatorUserAuthorityRoleListByAccount(accountId);
    	
    	//assert
    	assertThat(authorityRoleListFound.size()).isEqualTo(authorityRoleList.size());
    	assertThat(authorityRoleListFound.get(0)).isEqualTo(authorityRoleList.get(0));
    }

    @Test
    void createPendingAuthorityForOperator_pending_authority_exists() {
        Long accountId = 1L;
        String roleCode = "roleCode";
        String userId = "userId";
        String authorityUuid = "uuid";
        PmrvUser modificationUser = PmrvUser.builder().userId("current_user_id").build();
        Authority existingAuthority = createAuthority(userId, roleCode, AuthorityStatus.PENDING);
        Authority updatedAuthority = createAuthority(userId, roleCode, AuthorityStatus.PENDING);
        updatedAuthority.setUuid(authorityUuid);

        when(authorityRepository.findByUserIdAndAccountId(userId, accountId)).thenReturn(Optional.of(existingAuthority));
        when(authorityAssignmentService.updatePendingAuthority(existingAuthority, roleCode, modificationUser.getUserId()))
            .thenReturn(updatedAuthority);

        String result = operatorAuthorityService.createPendingAuthorityForOperator(accountId, roleCode, userId, modificationUser);
        
        assertThat(result).isEqualTo(authorityUuid);

        verify(authorityRepository, times(1)).findByUserIdAndAccountId(userId, accountId);
        verify(roleRepository, never()).findByCode(anyString());
        verify(authorityAssignmentService, times(1))
            .updatePendingAuthority(existingAuthority, roleCode, modificationUser.getUserId());
    }

    @Test
    void createPendingAuthorityForOperator_throws_exception_when_active_authority_exists() {
        Long accountId = 1L;
        String roleCode = "roleCode";
        String userId = "userId";
        PmrvUser currentUser = PmrvUser.builder().userId("current_user_id").build();
        Authority existingAuthority = createAuthority(userId, "anotherRoleCode", AuthorityStatus.ACTIVE);

        when(authorityRepository.findByUserIdAndAccountId(userId, accountId)).thenReturn(Optional.of(existingAuthority));

        BusinessException businessException = assertThrows(BusinessException.class,
            () -> operatorAuthorityService.createPendingAuthorityForOperator(accountId, roleCode, userId, currentUser));

        assertEquals(ErrorCode.AUTHORITY_USER_UPDATE_NON_PENDING_AUTHORITY_NOT_ALLOWED, businessException.getErrorCode());

        verify(authorityRepository, times(1)).findByUserIdAndAccountId(userId, accountId);
        verify(authorityRepository, never()).save(any());
        verify(roleRepository, never()).findByCode(anyString());
    }

    @Test
    void createPendingAuthorityForOperator_authority_not_exists() {
        Long accountId = 1L;
        String roleCode = "roleCode";
        String userId = "userId";
        PmrvUser currentUser = PmrvUser.builder().userId("current_user_id").build();
        Role role = Role.builder().code(roleCode).build();
        
        AccountDTO account = AccountDTO.builder().id(accountId).build();
        
        String authorityUuid = "uuid";
        Authority authority = Authority.builder()
                .userId(currentUser.getUserId())
                .code(roleCode)
                .accountId(accountId)
                .status(AuthorityStatus.PENDING)
                .createdBy(currentUser.getUserId())
                .uuid(authorityUuid)
                .build();

        when(authorityRepository.findByUserIdAndAccountId(userId, accountId)).thenReturn(Optional.empty());
        when(roleRepository.findByCode(roleCode)).thenReturn(Optional.of(role));
        when(authorityAssignmentService.createAuthorityPermissionsForRole(Mockito.any(Authority.class), Mockito.eq(role)))
            .thenReturn(authority);

        String result = operatorAuthorityService.createPendingAuthorityForOperator(accountId, roleCode, userId, currentUser);
        assertThat(result).isEqualTo(authorityUuid);
        
        ArgumentCaptor<Authority> authorityCaptor = ArgumentCaptor.forClass(Authority.class);
        verify(authorityRepository, times(1)).findByUserIdAndAccountId(userId, accountId);
        verify(roleRepository, times(1)).findByCode(roleCode);
        verify(authorityAssignmentService, times(1))
            .createAuthorityPermissionsForRole(authorityCaptor.capture(), eq(role));

        Authority authoritySaved = authorityCaptor.getValue();

        assertThat(authoritySaved).isNotNull();
        assertThat(authoritySaved.getUserId()).isEqualTo(userId);
        assertThat(authoritySaved.getCode()).isEqualTo(role.getCode());
        assertThat(authoritySaved.getUuid()).isNotNull();
        assertThat(authoritySaved.getCreatedBy()).isEqualTo(currentUser.getUserId());
        assertThat(authoritySaved.getStatus()).isEqualTo(AuthorityStatus.PENDING);
        assertThat(authoritySaved.getAccountId()).isEqualTo(accountId);
        assertThat(authoritySaved.getVerificationBodyId()).isNull();
        assertThat(authoritySaved.getCompetentAuthority()).isNull();
    }
    
    @Test
    void findActiveOperatorUsersByAccount() {
    	Long accountId = 1L;
    	List<String> admins = List.of("admin1");
    	
    	when(authorityRepository.findActiveOperatorUsersByAccountAndRoleCode(accountId, AuthorityConstants.OPERATOR_ADMIN_ROLE_CODE))
    		.thenReturn(admins);
    	
    	List<String> result = operatorAuthorityService.findActiveOperatorAdminUsersByAccount(accountId);
    	
    	assertThat(result).isEqualTo(admins);
    	verify(authorityRepository, times(1)).findActiveOperatorUsersByAccountAndRoleCode(accountId, AuthorityConstants.OPERATOR_ADMIN_ROLE_CODE);
    }
    
	private Role createRole(String code, RoleType roleType,  Permission... permissions) {
		Role role = Role.builder().code(code).type(roleType).build();
		for(Permission permission : permissions) {
			role.addPermission(
					RolePermission.builder()
						.permission(permission).build());
		}
		return role;
	}

	private UserRoleTypeDTO createUserRoleTypeDTO(String userId, RoleType roleType) {
		return UserRoleTypeDTO.builder().userId(userId).roleType(roleType).build();
	}

	private Authority createAuthority(String userId, String roleCode, AuthorityStatus status){
	    return Authority.builder()
            .userId(userId)
            .code(roleCode)
            .status(status)
            .authorityPermissions(new ArrayList<>())
            .build();
    }

}
