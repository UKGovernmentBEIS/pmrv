package uk.gov.pmrv.api.user.operator.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.pmrv.api.authorization.core.domain.AuthorityStatus;
import uk.gov.pmrv.api.authorization.core.domain.dto.AuthorityRoleDTO;
import uk.gov.pmrv.api.authorization.operator.service.OperatorAuthorityService;
import uk.gov.pmrv.api.common.domain.enumeration.RoleType;
import uk.gov.pmrv.api.common.domain.model.PmrvUser;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.user.operator.domain.OperatorUserDTO;

@ExtendWith(MockitoExtension.class)
class OperatorUserManagementServiceTest {

	@InjectMocks
        private OperatorUserManagementService service;
	
	@Mock
	private OperatorUserAuthService operatorUserAuthService;
	
	@Mock
	private OperatorAuthorityService operatorAuthorityService;
	
	@Test
	void updateCurrentOperatorUser() {
		PmrvUser pmrvUser = PmrvUser.builder().userId("userId").roleType(RoleType.OPERATOR).build();
		OperatorUserDTO operatorUserDTO = buildOperatorUserDTO();

		service.updateOperatorUser(pmrvUser, operatorUserDTO);

		verify(operatorUserAuthService, times(1)).updateOperatorUser(pmrvUser.getUserId(), operatorUserDTO);
	}
	

	@Test
	void getOperatorUserByAccountAndIdTestDifferentUserWithPermission() {
		final Long accountId = 1L;
		final String userId2 = "userId2";
		List<AuthorityRoleDTO> authorityRoleList = List.of(
				AuthorityRoleDTO.builder().userId(userId2).authorityStatus(AuthorityStatus.ACTIVE).build());

		when(operatorAuthorityService.findOperatorUserAuthorityRoleListByAccount(accountId)).thenReturn(authorityRoleList);

		// Invoke
		service.getOperatorUserByAccountAndId(accountId, userId2);

		// Verify
		verify(operatorAuthorityService, times(1)).findOperatorUserAuthorityRoleListByAccount(accountId);
		verify(operatorUserAuthService, times(1)).getOperatorUserById(userId2);
	}

	@Test
	void getOperatorUserByAccountAndIdTestUserNotExists() {
		final Long accountId = 1L;
		final String userId1 = "userId1";
		final String userId2 = "userId2";
		List<AuthorityRoleDTO> authorityRoleList = List.of(
				AuthorityRoleDTO.builder().userId(userId1).authorityStatus(AuthorityStatus.ACTIVE).build());

		when(operatorAuthorityService.findOperatorUserAuthorityRoleListByAccount(accountId)).thenReturn(authorityRoleList);

		// Invoke
		BusinessException businessException = assertThrows(BusinessException.class,
				() -> service.getOperatorUserByAccountAndId(accountId, userId2));

		// Verify
		assertEquals(ErrorCode.AUTHORITY_USER_NOT_RELATED_TO_ACCOUNT, businessException.getErrorCode());
		verify(operatorAuthorityService, times(1)).findOperatorUserAuthorityRoleListByAccount(accountId);
	}

	@Test
	void getOperatorUserByAccountAndIdTestAccountNotExists() {
		final Long accountId = 1L;
		final String userId = "userId";

		when(operatorAuthorityService.findOperatorUserAuthorityRoleListByAccount(accountId)).thenReturn(new ArrayList<>());

		// Invoke
		BusinessException businessException = assertThrows(BusinessException.class,
				() -> service.getOperatorUserByAccountAndId(accountId, userId));

		// Verify
		assertEquals(ErrorCode.AUTHORITY_USER_NOT_RELATED_TO_ACCOUNT, businessException.getErrorCode());
		verify(operatorAuthorityService, times(1)).findOperatorUserAuthorityRoleListByAccount(accountId);
	}

	@Test
	void updateOperatorUserByAccountAndIdTestDifferentUserWithPermission() {
		final Long accountId = 1L;
		final String userId2 = "userId2";
		List<AuthorityRoleDTO> authorityRoleList = List.of(
				AuthorityRoleDTO.builder().userId(userId2).authorityStatus(AuthorityStatus.ACTIVE).build());
		OperatorUserDTO operatorUserDTO = buildOperatorUserDTO();

		when(operatorAuthorityService.findOperatorUserAuthorityRoleListByAccount(accountId)).thenReturn(authorityRoleList);

		// Invoke
		service.updateOperatorUserByAccountAndId(accountId, userId2, operatorUserDTO);

		// Verify
		verify(operatorAuthorityService, times(1)).findOperatorUserAuthorityRoleListByAccount(accountId);
		verify(operatorUserAuthService, times(1)).updateOperatorUser(userId2, operatorUserDTO);
	}

	@Test
	void updateOperatorUserByAccountAndIdTestUserNotExists() {
		final Long accountId = 1L;
		final String userId1 = "userId1";
		final String userId2 = "userId2";
		List<AuthorityRoleDTO> authorityRoleList = List.of(
				AuthorityRoleDTO.builder().userId(userId1).authorityStatus(AuthorityStatus.ACTIVE).build());
		OperatorUserDTO operatorUserDTO = buildOperatorUserDTO();

		when(operatorAuthorityService.findOperatorUserAuthorityRoleListByAccount(accountId)).thenReturn(authorityRoleList);

		// Invoke
		BusinessException businessException = assertThrows(BusinessException.class,
				() -> service.updateOperatorUserByAccountAndId(accountId, userId2, operatorUserDTO));

		// Verify
		assertEquals(ErrorCode.AUTHORITY_USER_NOT_RELATED_TO_ACCOUNT, businessException.getErrorCode());
		verify(operatorAuthorityService, times(1)).findOperatorUserAuthorityRoleListByAccount(accountId);
		verify(operatorUserAuthService, never()).updateOperatorUser(anyString(), any());
	}

	@Test
	void updateOperatorUserByAccountAndIdTestAccountNotExists() {
		final Long accountId = 1L;
		final String userId2 = "userId2";
		OperatorUserDTO operatorUserDTO = buildOperatorUserDTO();

		when(operatorAuthorityService.findOperatorUserAuthorityRoleListByAccount(accountId)).thenReturn(new ArrayList<>());

		// Invoke
		BusinessException businessException = assertThrows(BusinessException.class,
				() -> service.updateOperatorUserByAccountAndId(accountId, userId2, operatorUserDTO));

		// Verify
		assertEquals(ErrorCode.AUTHORITY_USER_NOT_RELATED_TO_ACCOUNT, businessException.getErrorCode());
		verify(operatorAuthorityService, times(1)).findOperatorUserAuthorityRoleListByAccount(accountId);
		verify(operatorUserAuthService, never()).updateOperatorUser(anyString(), any());
	}
	
	private OperatorUserDTO buildOperatorUserDTO() {
		return OperatorUserDTO.builder()
				.firstName("fn")
				.lastName("ln")
				.email("email")
				.build();
	}
}
