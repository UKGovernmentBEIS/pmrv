package uk.gov.pmrv.api.user.verifier.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.authorization.verifier.service.VerifierAuthorityService;
import uk.gov.pmrv.api.common.domain.enumeration.RoleType;
import uk.gov.pmrv.api.common.domain.model.PmrvAuthority;
import uk.gov.pmrv.api.common.domain.model.PmrvUser;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.user.verifier.domain.VerifierUserDTO;

@ExtendWith(MockitoExtension.class)
class VerifierUserManagementServiceTest {

	@InjectMocks
    private VerifierUserManagementService service;
	
	@Mock
	private VerifierAuthorityService verifierAuthorityService;
	
	@Mock
	private VerifierUserAuthService verifierUserAuthService;
	
	@Test
	void getVerifierUserById_verifier_auth_user() {
		final String userId = "userId";
		final Long verBodyId = 1L;
		PmrvUser pmrvUser = PmrvUser.builder()
            .userId("authUserId")
            .roleType(RoleType.VERIFIER)
            .authorities(List.of(PmrvAuthority.builder().verificationBodyId(verBodyId).build()))
            .build();

		// Mock
		when(verifierAuthorityService.existsByUserIdAndVerificationBodyId(userId, verBodyId)).thenReturn(true);

		// Invoke
		service.getVerifierUserById(pmrvUser, userId);

		// Assert
		verify(verifierAuthorityService, times(1)).existsByUserIdAndVerificationBodyId(userId, verBodyId);
		verify(verifierUserAuthService, times(1)).getVerifierUserById(userId);
		verifyNoMoreInteractions(verifierAuthorityService);
	}

    @Test
    void getVerifierUserById_regulator_auth_user() {
        final String userId = "userId";
        PmrvUser pmrvUser = PmrvUser.builder()
            .userId("authUserId")
            .roleType(RoleType.REGULATOR)
            .build();

        // Mock
        when(verifierAuthorityService.existsByUserIdAndVerificationBodyIdNotNull(userId)).thenReturn(true);

        // Invoke
        service.getVerifierUserById(pmrvUser, userId);

        // Assert
        verify(verifierAuthorityService, times(1)).existsByUserIdAndVerificationBodyIdNotNull(userId);
        verify(verifierUserAuthService, times(1)).getVerifierUserById(userId);
        verifyNoMoreInteractions(verifierAuthorityService);
    }

    @Test
    void getVerifierUserById_auth_user_role_not_supported() {
        final String userId = "userId";
        PmrvUser pmrvUser = PmrvUser.builder()
            .userId("authUserId")
            .roleType(RoleType.OPERATOR)
            .build();

        assertThrows(UnsupportedOperationException.class, () -> service.getVerifierUserById(pmrvUser, userId));

        verifyNoInteractions(verifierUserAuthService);
        verifyNoInteractions(verifierAuthorityService);
    }

	@Test
	void getVerifierUserById_verifier_auth_user_wanted_user_not_verifier() {
        final String userId = "userId";
        final Long verBodyId = 1L;
        PmrvUser pmrvUser = PmrvUser.builder()
            .userId("authUserId")
            .roleType(RoleType.VERIFIER)
            .authorities(List.of(PmrvAuthority.builder().verificationBodyId(verBodyId).build()))
            .build();

		// Mock
		when(verifierAuthorityService.existsByUserIdAndVerificationBodyId(userId, verBodyId)).thenReturn(false);

		// Invoke
		BusinessException businessException = assertThrows(BusinessException.class,
				() -> service.getVerifierUserById(pmrvUser, userId));

		// Assert
		assertEquals(ErrorCode.AUTHORITY_USER_NOT_RELATED_TO_VERIFICATION_BODY, businessException.getErrorCode());
		verify(verifierAuthorityService, times(1)).existsByUserIdAndVerificationBodyId(userId, verBodyId);
		verifyNoInteractions(verifierUserAuthService);
        verifyNoMoreInteractions(verifierAuthorityService);
	}

    @Test
    void getVerifierUserById_regulator_auth_user_wanted_user_not_verifier() {
        final String userId = "userId";
        PmrvUser pmrvUser = PmrvUser.builder()
            .userId("authUserId")
            .roleType(RoleType.REGULATOR)
            .build();

        // Mock
        when(verifierAuthorityService.existsByUserIdAndVerificationBodyIdNotNull(userId)).thenReturn(false);

        // Invoke
        BusinessException businessException = assertThrows(BusinessException.class,
            () -> service.getVerifierUserById(pmrvUser, userId));

        // Assert
        assertEquals(ErrorCode.AUTHORITY_USER_IS_NOT_VERIFIER, businessException.getErrorCode());
        verify(verifierAuthorityService, times(1)).existsByUserIdAndVerificationBodyIdNotNull(userId);
        verifyNoInteractions(verifierUserAuthService);
        verifyNoMoreInteractions(verifierAuthorityService);
    }

	@Test
	void updateVerifierUserById_verifier_auth_user() {
		String userId = "userId";
		final Long verBodyId = 1L;
        PmrvUser pmrvUser = PmrvUser.builder()
            .userId("authUserId")
            .roleType(RoleType.VERIFIER)
            .authorities(List.of(PmrvAuthority.builder().verificationBodyId(verBodyId).build()))
            .build();
		VerifierUserDTO verifierUserDTO = VerifierUserDTO.builder().build();

		// Mock
		when(verifierAuthorityService.existsByUserIdAndVerificationBodyId(userId, verBodyId)).thenReturn(true);

		// Invoke
		service.updateVerifierUserById(pmrvUser, userId, verifierUserDTO);

		// Assert
		verify(verifierAuthorityService, times(1)).existsByUserIdAndVerificationBodyId(userId, verBodyId);
		verify(verifierUserAuthService, times(1)).updateVerifierUser(userId, verifierUserDTO);
        verifyNoMoreInteractions(verifierAuthorityService);
	}

    @Test
    void updateVerifierUserById_regulator_auth_user() {
        String userId = "userId";
        PmrvUser pmrvUser = PmrvUser.builder()
            .userId("authUserId")
            .roleType(RoleType.REGULATOR)
            .build();
        VerifierUserDTO verifierUserDTO = VerifierUserDTO.builder().build();

        // Mock
        when(verifierAuthorityService.existsByUserIdAndVerificationBodyIdNotNull(userId)).thenReturn(true);

        // Invoke
        service.updateVerifierUserById(pmrvUser, userId, verifierUserDTO);

        // Assert
        verify(verifierAuthorityService, times(1)).existsByUserIdAndVerificationBodyIdNotNull(userId);
        verify(verifierUserAuthService, times(1)).updateVerifierUser(userId, verifierUserDTO);
        verifyNoMoreInteractions(verifierAuthorityService);
    }

    @Test
    void updateVerifierUserById_auth_user_role_not_supported() {
        String userId = "userId";
        PmrvUser pmrvUser = PmrvUser.builder()
            .userId("authUserId")
            .roleType(RoleType.OPERATOR)
            .build();
        VerifierUserDTO verifierUserDTO = VerifierUserDTO.builder().build();

        assertThrows(UnsupportedOperationException.class,
            () -> service.updateVerifierUserById(pmrvUser, userId, verifierUserDTO));

        verifyNoInteractions(verifierUserAuthService);
        verifyNoInteractions(verifierAuthorityService);
    }

	@Test
	void updateVerifierUserById_verifier_auth_user_wanted_user_not_verifier() {
		String userId = "userId";
		final Long verBodyId = 1L;
        PmrvUser pmrvUser = PmrvUser.builder()
            .userId("authUserId")
            .roleType(RoleType.VERIFIER)
            .authorities(List.of(PmrvAuthority.builder().verificationBodyId(verBodyId).build()))
            .build();
        VerifierUserDTO verifierUserDTO = VerifierUserDTO.builder().build();

		// Mock
		when(verifierAuthorityService.existsByUserIdAndVerificationBodyId(userId, verBodyId)).thenReturn(false);

		// Invoke
		BusinessException businessException = assertThrows(BusinessException.class,
				() -> service.updateVerifierUserById(pmrvUser, userId, verifierUserDTO));

		// Assert
		assertEquals(ErrorCode.AUTHORITY_USER_NOT_RELATED_TO_VERIFICATION_BODY, businessException.getErrorCode());
		verify(verifierAuthorityService, times(1)).existsByUserIdAndVerificationBodyId(userId, verBodyId);
		verifyNoInteractions(verifierUserAuthService);
        verifyNoMoreInteractions(verifierAuthorityService);
	}

    @Test
    void updateVerifierUserById_regulator_auth_user_wanted_user_not_verifier() {
        String userId = "userId";
        PmrvUser pmrvUser = PmrvUser.builder()
            .userId("authUserId")
            .roleType(RoleType.REGULATOR)
            .build();
        VerifierUserDTO verifierUserDTO = VerifierUserDTO.builder().build();

        // Mock
        when(verifierAuthorityService.existsByUserIdAndVerificationBodyIdNotNull(userId)).thenReturn(false);

        // Invoke
        BusinessException businessException = assertThrows(BusinessException.class,
            () -> service.updateVerifierUserById(pmrvUser, userId, verifierUserDTO));

        // Assert
        assertEquals(ErrorCode.AUTHORITY_USER_IS_NOT_VERIFIER, businessException.getErrorCode());
        verify(verifierAuthorityService, times(1)).existsByUserIdAndVerificationBodyIdNotNull(userId);
        verifyNoMoreInteractions(verifierAuthorityService);
        verifyNoInteractions(verifierUserAuthService);
    }

	@Test
	void updateCurrentVerifierUser() {
		String userId = "userId";
		final Long verBodyId = 1L;
		PmrvUser pmrvUser = PmrvUser.builder().userId(userId)
				.authorities(List.of(PmrvAuthority.builder().verificationBodyId(verBodyId).build())).build();
		VerifierUserDTO verifierUserDTO = VerifierUserDTO.builder().build();

		// Invoke
		service.updateCurrentVerifierUser(pmrvUser, verifierUserDTO);

		// Assert
		verify(verifierUserAuthService, times(1)).updateVerifierUser(userId, verifierUserDTO);
	}
	
}
