package uk.gov.pmrv.api.authorization.verifier.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.authorization.AuthorityConstants;
import uk.gov.pmrv.api.authorization.core.domain.AuthorityStatus;
import uk.gov.pmrv.api.authorization.core.domain.dto.AuthorityRoleDTO;
import uk.gov.pmrv.api.authorization.core.domain.dto.UserAuthoritiesDTO;
import uk.gov.pmrv.api.authorization.core.domain.dto.UserAuthorityDTO;
import uk.gov.pmrv.api.authorization.core.repository.AuthorityRepository;
import uk.gov.pmrv.api.authorization.rules.domain.Scope;
import uk.gov.pmrv.api.authorization.rules.services.resource.VerificationBodyAuthorizationResourceService;
import uk.gov.pmrv.api.common.domain.model.PmrvAuthority;
import uk.gov.pmrv.api.common.domain.model.PmrvUser;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VerifierAuthorityQueryServiceTest {

	@InjectMocks
	private VerifierAuthorityQueryService service;

	@Mock
	private AuthorityRepository authorityRepository;

	@Mock
	private VerificationBodyAuthorizationResourceService verificationBodyAuthorizationResourceService;

	@Test
	void getVerifierAuthorities_has_edit_user_scope() {
		final Long vb = 1L;
		final String userId = "user";
        PmrvAuthority pmrvAuthority = PmrvAuthority.builder().verificationBodyId(vb).build();
		final PmrvUser authUser = PmrvUser.builder().userId("authUser").authorities(List.of(pmrvAuthority)).build();

		AuthorityRoleDTO authorityRoleDTO = createAuthorityRoleDTO(userId);
		UserAuthorityDTO verifierUserAuthority = createEditableUserAuthority(userId);

		// Mock
        when(verificationBodyAuthorizationResourceService.hasUserScopeToVerificationBody(authUser, vb, Scope.EDIT_USER))
            .thenReturn(true);
		when(authorityRepository.findVerifierUserAuthorityRoleListByVerificationBody(vb))
			.thenReturn(List.of(authorityRoleDTO));

		// Invoke
		UserAuthoritiesDTO result = service.getVerifierAuthorities(authUser);

		// Assert
		assertThat(result.isEditable()).isTrue();
		assertThat(result.getAuthorities()).isEqualTo(List.of(verifierUserAuthority));

		verify(authorityRepository, times(1)).findVerifierUserAuthorityRoleListByVerificationBody(vb);
		verify(verificationBodyAuthorizationResourceService, times(1))
            .hasUserScopeToVerificationBody(authUser, vb, Scope.EDIT_USER);
		verifyNoMoreInteractions(authorityRepository);
	}

	@Test
	void getVerifierUsers_no_edit_user_scope() {
		final Long vb = 1L;
		final String userId = "user";
        PmrvAuthority pmrvAuthority = PmrvAuthority.builder().verificationBodyId(vb).build();
        final PmrvUser authUser = PmrvUser.builder().userId("authUser").authorities(List.of(pmrvAuthority)).build();

		AuthorityRoleDTO authorityRoleDTO = createAuthorityRoleDTO(userId);
        UserAuthorityDTO verifierUserAuthority = createUserAuthority(userId);

		// Mock
        when(verificationBodyAuthorizationResourceService.hasUserScopeToVerificationBody(authUser, vb, Scope.EDIT_USER))
            .thenReturn(false);
        when(authorityRepository.findNonPendingVerifierUserAuthorityRoleListByVerificationBody(vb))
            .thenReturn(List.of(authorityRoleDTO));

		// Invoke
		UserAuthoritiesDTO result = service.getVerifierAuthorities(authUser);

		// Assert
		assertThat(result.isEditable()).isFalse();
		assertThat(result.getAuthorities()).isEqualTo(List.of(verifierUserAuthority));

		verify(authorityRepository, times(1))
            .findNonPendingVerifierUserAuthorityRoleListByVerificationBody(vb);
		verify(verificationBodyAuthorizationResourceService, times(1))
            .hasUserScopeToVerificationBody(authUser, vb, Scope.EDIT_USER);
        verifyNoMoreInteractions(authorityRepository);
	}

	@Test
    void findVerifierAdminsByVerificationBody() {
        Long verificationBodyId = 1L;

        List<String> expectResult = List.of("admin1");
        when(authorityRepository.findUsersByVerificationBodyAndCode(verificationBodyId, AuthorityConstants.VERIFIER_ADMIN_ROLE_CODE))
            .thenReturn(expectResult);

        //invoke
        List<String> result = service.findVerifierAdminsByVerificationBody(verificationBodyId);

        assertThat(result).isEqualTo(expectResult);
        verify(authorityRepository, times(1)).findUsersByVerificationBodyAndCode(verificationBodyId, AuthorityConstants.VERIFIER_ADMIN_ROLE_CODE);
    }

	@Test
	void getVerificationBodyAuthorities() {
		final Long vbId = 1L;
		final String userId = "user";
		AuthorityRoleDTO authorityRoleDTO = createAuthorityRoleDTO(userId);
        UserAuthorityDTO verifierUserAuthority = createEditableUserAuthority(userId);

		// Mock
		when(authorityRepository.findVerifierUserAuthorityRoleListByVerificationBody(vbId))
				.thenReturn(List.of(authorityRoleDTO));

		// Invoke
        UserAuthoritiesDTO result = service.getVerificationBodyAuthorities(vbId, true);

		// Assert
        assertThat(result.isEditable()).isTrue();
        assertThat(result.getAuthorities()).isEqualTo(List.of(verifierUserAuthority));
		verify(authorityRepository, times(1)).findVerifierUserAuthorityRoleListByVerificationBody(vbId);
        verifyNoMoreInteractions(authorityRepository);
	}

    @Test
    void existsByUserIdAndVerificationBodyId() {
        final String userId = "userId";
        final Long verificationBodyId = 1L;

        // Mock
        when(authorityRepository.existsByUserIdAndVerificationBodyId(userId, verificationBodyId)).thenReturn(true);

        // Invoke
        boolean actual = service.existsByUserIdAndVerificationBodyId(userId, verificationBodyId);

        // Assert
        assertTrue(actual);
        verify(authorityRepository, times(1)).existsByUserIdAndVerificationBodyId(userId, verificationBodyId);
    }

    private UserAuthorityDTO createEditableUserAuthority(final String userId) {
	    return UserAuthorityDTO.builder()
            .userId(userId)
            .roleCode("verifier_admin")
            .roleName("Verifier Admin")
            .authorityStatus(AuthorityStatus.ACTIVE)
            .build();
    }

    private UserAuthorityDTO createUserAuthority(final String userId) {
        return UserAuthorityDTO.builder()
            .userId(userId)
            .roleCode("verifier_admin")
            .roleName("Verifier Admin")
            .build();
    }

	private AuthorityRoleDTO createAuthorityRoleDTO(final String userId) {
	    return AuthorityRoleDTO.builder()
				.userId(userId)
            	.authorityStatus(AuthorityStatus.ACTIVE)
            	.roleCode("verifier_admin")
            	.roleName("Verifier Admin")
            	.build();
    }
}
