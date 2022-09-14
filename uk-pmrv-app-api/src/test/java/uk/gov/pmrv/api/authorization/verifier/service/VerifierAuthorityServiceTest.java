package uk.gov.pmrv.api.authorization.verifier.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.authorization.core.domain.Authority;
import uk.gov.pmrv.api.authorization.core.domain.AuthorityStatus;
import uk.gov.pmrv.api.authorization.core.domain.Role;
import uk.gov.pmrv.api.authorization.core.repository.AuthorityRepository;
import uk.gov.pmrv.api.authorization.core.repository.RoleRepository;
import uk.gov.pmrv.api.authorization.core.service.AuthorityAssignmentService;
import uk.gov.pmrv.api.common.domain.enumeration.RoleType;
import uk.gov.pmrv.api.common.domain.model.PmrvUser;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;

@ExtendWith(MockitoExtension.class)
class VerifierAuthorityServiceTest {
	
	@InjectMocks
	private VerifierAuthorityService service;
	
	@Mock
	private AuthorityAssignmentService authorityAssignmentService;
	
	@Mock
	private AuthorityRepository authorityRepository;
	
	@Mock
	private RoleRepository roleRepository;
	
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

    @Test
    void createPendingAuthority_new_authority() {
        final Long verificationBodyId = 1L;
        final String roleCode = "roleCode";
        final String verifierUser = "user";
        final PmrvUser authCreationUser = PmrvUser.builder().userId("pmrvUser").roleType(RoleType.VERIFIER).build();
        Role role = Role.builder().code(roleCode).build();

        when(authorityRepository.findByUserIdAndVerificationBodyId(verifierUser, verificationBodyId))
            .thenReturn(Optional.empty());
        when(roleRepository.findByCode(roleCode)).thenReturn(Optional.of(role));
        
        String authorityUuid = "uuid";
        Authority authority = Authority.builder()
                .userId(verifierUser)
                .code(roleCode)
                .verificationBodyId(verificationBodyId)
                .status(AuthorityStatus.PENDING)
                .createdBy(authCreationUser.getUserId())
                .uuid(authorityUuid)
                .build();
        
        when(authorityAssignmentService.createAuthorityPermissionsForRole(Mockito.any(Authority.class), Mockito.eq(role)))
            .thenReturn(authority);
        
        //invoke
        String result = service.createPendingAuthority(verificationBodyId, roleCode, verifierUser, authCreationUser);

        assertThat(result).isEqualTo(authorityUuid);
        
        ArgumentCaptor<Authority> authorityCaptor = ArgumentCaptor.forClass(Authority.class);
        verify(authorityAssignmentService, times(1)).createAuthorityPermissionsForRole(authorityCaptor.capture(), eq(role));
        Authority savedAuthority = authorityCaptor.getValue();

        assertThat(savedAuthority.getCode()).isEqualTo(role.getCode());
        assertThat(savedAuthority.getStatus()).isEqualTo(AuthorityStatus.PENDING);
        assertThat(savedAuthority.getUserId()).isEqualTo(verifierUser);
        assertThat(savedAuthority.getVerificationBodyId()).isEqualTo(verificationBodyId);
        assertThat(savedAuthority.getUuid()).isNotBlank();
        assertThat(savedAuthority.getCreatedBy()).isEqualTo(authCreationUser.getUserId());
        assertThat(savedAuthority.getCompetentAuthority()).isNull();
        assertThat(savedAuthority.getAccountId()).isNull();

        verify(authorityRepository, times(1)).findByUserIdAndVerificationBodyId(verifierUser, verificationBodyId);
        verify(roleRepository, times(1)).findByCode(roleCode);
    }

    @Test
    void createPendingAuthority_throws_exception_when_active_authority_exists() {
        final Long verificationBodyId = 1L;
        final String roleCode = "roleCode";
        final String userId = "user";
        final PmrvUser pmrvUser = PmrvUser.builder().userId("pmrvUser").roleType(RoleType.VERIFIER).build();

        Authority authority = Authority.builder().userId(userId).status(AuthorityStatus.ACTIVE).code(roleCode).build();

        when(authorityRepository.findByUserIdAndVerificationBodyId(userId, verificationBodyId))
            .thenReturn(Optional.of(authority));

        BusinessException exception = assertThrows(BusinessException.class,
            () -> service.createPendingAuthority(verificationBodyId, roleCode, userId, pmrvUser));

        assertEquals(ErrorCode.AUTHORITY_USER_UPDATE_NON_PENDING_AUTHORITY_NOT_ALLOWED, exception.getErrorCode());

        verify(authorityRepository, times(1)).findByUserIdAndVerificationBodyId(userId, verificationBodyId);
        verifyNoInteractions(roleRepository);
    }

    @Test
    void createPendingAuthority_pending_authority_exists() {
        final Long verificationBodyId = 1L;
        final String roleCode = "roleCode";
        final String verifierUser = "user";
        final PmrvUser authCreationUser = PmrvUser.builder().userId("pmrvUser").roleType(RoleType.VERIFIER).build();

        Authority authorityExisting = Authority.builder().userId(verifierUser).status(AuthorityStatus.PENDING).code(roleCode).build();
        String uuid = "authorityUuid";
        Authority authorityUpdated = Authority.builder().userId(verifierUser).status(AuthorityStatus.PENDING).code(roleCode).uuid(uuid).build();

        when(authorityRepository.findByUserIdAndVerificationBodyId(verifierUser, verificationBodyId))
            .thenReturn(Optional.of(authorityExisting));
        when(authorityAssignmentService.updatePendingAuthority(authorityExisting, roleCode, authCreationUser.getUserId()))
            .thenReturn(authorityUpdated);

        //invoke
        String result = service.createPendingAuthority(verificationBodyId, roleCode, verifierUser, authCreationUser);
        
        assertThat(result).isEqualTo(uuid);
        
        verify(authorityRepository, times(1)).findByUserIdAndVerificationBodyId(verifierUser, verificationBodyId);

        verify(authorityAssignmentService, times(1)).updatePendingAuthority(authorityExisting, roleCode, authCreationUser.getUserId());
        verifyNoInteractions(roleRepository);
    }

    @Test
    void existsByUserIdAndVerificationBodyIdNotNull_true() {
        final String userId = "userId";
        Authority authority = Authority.builder().build();
        
        when(authorityRepository.findByUserIdAndVerificationBodyIdNotNull(userId)).thenReturn(Optional.of(authority));

        assertTrue(service.existsByUserIdAndVerificationBodyIdNotNull(userId));
        
        verify(authorityRepository, times(1)).findByUserIdAndVerificationBodyIdNotNull(userId);
    }

    @Test
    void existsByUserIdAndVerificationBodyIdNotNull_false() {
        final String userId = "userId";

        when(authorityRepository.findByUserIdAndVerificationBodyIdNotNull(userId)).thenReturn(Optional.empty());
        assertFalse(service.existsByUserIdAndVerificationBodyIdNotNull(userId));

        verify(authorityRepository, times(1)).findByUserIdAndVerificationBodyIdNotNull(userId);
    }

}
