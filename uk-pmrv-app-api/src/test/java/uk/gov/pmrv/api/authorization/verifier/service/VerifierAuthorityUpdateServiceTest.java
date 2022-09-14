package uk.gov.pmrv.api.authorization.verifier.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import uk.gov.pmrv.api.authorization.AuthorityConstants;
import uk.gov.pmrv.api.authorization.core.domain.Authority;
import uk.gov.pmrv.api.authorization.core.domain.AuthorityStatus;
import uk.gov.pmrv.api.authorization.core.domain.Role;
import uk.gov.pmrv.api.authorization.core.repository.AuthorityRepository;
import uk.gov.pmrv.api.authorization.core.repository.RoleRepository;
import uk.gov.pmrv.api.authorization.core.service.AuthorityAssignmentService;
import uk.gov.pmrv.api.authorization.verifier.event.VerifierUserDisabledEvent;
import uk.gov.pmrv.api.authorization.verifier.domain.VerifierAuthorityUpdateDTO;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.user.verifier.service.VerifierAdminCreationEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VerifierAuthorityUpdateServiceTest {

	@InjectMocks
	private VerifierAuthorityUpdateService service;
	
	@Mock
	private VerifierAdminExistenceValidator verifierAdminExistenceValidator;

	@Mock
	private VerifierStatusModificationAllowanceValidator verifierStatusModificationAllowanceValidator;
	
	@Mock
	private AuthorityAssignmentService authorityAssignmentService;
	
	@Mock
	private AuthorityRepository authorityRepository;
	
	@Mock
	private RoleRepository roleRepository;

	@Mock
	private ApplicationEventPublisher eventPublisher;

	@Spy
	private ArrayList<VerifierAuthorityUpdateValidator> verifierAuthorityUpdateValidators;

	@BeforeEach
	void setUp() {
		verifierAuthorityUpdateValidators.add(verifierAdminExistenceValidator);
		verifierAuthorityUpdateValidators.add(verifierStatusModificationAllowanceValidator);
	}
	
	@Test
	void updateVerifierAuthorities_active_to_disabled() {
		Long verificationBodyId = 1L;
		String user = "user";
		Authority authority = 
				Authority.builder()
						.userId(user)
						.status(AuthorityStatus.ACTIVE)
						.code("role1")
						.build();
		List<VerifierAuthorityUpdateDTO> verifiersUpdate = 
				List.of(
						VerifierAuthorityUpdateDTO.builder().userId(user).authorityStatus(AuthorityStatus.DISABLED).roleCode("role2").build()
						);
		
		Role newRole = Role.builder().code("role2").build();
		
		when(authorityRepository.findByUserIdAndVerificationBodyId(verifiersUpdate.get(0).getUserId(), verificationBodyId))
			.thenReturn(Optional.of(authority));
		when(roleRepository.findByCode(verifiersUpdate.get(0).getRoleCode()))
			.thenReturn(Optional.of(newRole));
		when(authorityAssignmentService.updateAuthorityWithNewRole(authority, newRole))
			.thenReturn(authority);
		
		// Invoke
		List<String> actual = service.updateVerifierAuthorities(verifiersUpdate, verificationBodyId);

		// Verify
		assertThat(authority.getStatus()).isEqualTo(AuthorityStatus.DISABLED);
		assertThat(actual).isEmpty();
		verify(verifierAdminExistenceValidator, times(1)).validateUpdate(verifiersUpdate, verificationBodyId);
		verify(verifierStatusModificationAllowanceValidator, times(1)).validateUpdate(verifiersUpdate, verificationBodyId);
		verify(authorityRepository, times(1)).findByUserIdAndVerificationBodyId(verifiersUpdate.get(0).getUserId(), verificationBodyId);
		verify(roleRepository, times(1)).findByCode("role2");
		verify(authorityAssignmentService, times(1)).updateAuthorityWithNewRole(authority, newRole);
		verify(authorityRepository, times(1)).save(authority);
		verify(eventPublisher, times(1)).publishEvent(VerifierUserDisabledEvent.builder().userId(user).build());
		verifyNoMoreInteractions(eventPublisher);
	}

	@Test
	void updateVerifierAuthorities_disabled_to_active() {
		Long verificationBodyId = 1L;
		String user = "user";
		Authority authority =
			Authority.builder()
				.userId(user)
				.status(AuthorityStatus.DISABLED)
				.code("role1")
				.build();
		List<VerifierAuthorityUpdateDTO> verifiersUpdate =
			List.of(
				VerifierAuthorityUpdateDTO.builder().userId(user).authorityStatus(AuthorityStatus.ACTIVE).roleCode("role2").build()
			);

		Role newRole = Role.builder().code("role2").build();

		when(authorityRepository.findByUserIdAndVerificationBodyId(verifiersUpdate.get(0).getUserId(), verificationBodyId))
			.thenReturn(Optional.of(authority));
		when(roleRepository.findByCode(verifiersUpdate.get(0).getRoleCode()))
			.thenReturn(Optional.of(newRole));
		when(authorityAssignmentService.updateAuthorityWithNewRole(authority, newRole))
			.thenReturn(authority);

		// Invoke
		List<String> actual = service.updateVerifierAuthorities(verifiersUpdate, verificationBodyId);

		// Verify
		assertThat(authority.getStatus()).isEqualTo(AuthorityStatus.ACTIVE);
		assertThat(actual).isEmpty();
		verify(verifierAdminExistenceValidator, times(1)).validateUpdate(verifiersUpdate, verificationBodyId);
		verify(verifierStatusModificationAllowanceValidator, times(1)).validateUpdate(verifiersUpdate, verificationBodyId);
		verify(authorityRepository, times(1)).findByUserIdAndVerificationBodyId(verifiersUpdate.get(0).getUserId(), verificationBodyId);
		verify(roleRepository, times(1)).findByCode("role2");
		verify(authorityAssignmentService, times(1)).updateAuthorityWithNewRole(authority, newRole);
		verify(authorityRepository, times(1)).save(authority);
		verify(eventPublisher, never()).publishEvent(any());
	}

	@Test
	void updateVerifierAuthorities_accepted_to_active() {
		Long verificationBodyId = 1L;
		String user = "user";
		Authority authority =
				Authority.builder()
						.userId(user)
						.status(AuthorityStatus.ACCEPTED)
						.code(AuthorityConstants.VERIFIER_ADMIN_ROLE_CODE)
						.verificationBodyId(verificationBodyId)
						.build();
		List<VerifierAuthorityUpdateDTO> verifiersUpdate =
				List.of(
						VerifierAuthorityUpdateDTO.builder().userId(user).authorityStatus(AuthorityStatus.ACTIVE)
								.roleCode(AuthorityConstants.VERIFIER_ADMIN_ROLE_CODE).build()
				);

		when(authorityRepository.findByUserIdAndVerificationBodyId(verifiersUpdate.get(0).getUserId(), verificationBodyId))
				.thenReturn(Optional.of(authority));

		// Invoke
		List<String> actual = service.updateVerifierAuthorities(verifiersUpdate, verificationBodyId);

		// Verify
		assertThat(authority.getStatus()).isEqualTo(AuthorityStatus.ACTIVE);
		assertThat(actual).isEqualTo(List.of(user));
		verify(verifierAdminExistenceValidator, times(1)).validateUpdate(verifiersUpdate, verificationBodyId);
		verify(verifierStatusModificationAllowanceValidator, times(1)).validateUpdate(verifiersUpdate, verificationBodyId);
		verify(authorityRepository, times(1)).findByUserIdAndVerificationBodyId(verifiersUpdate.get(0).getUserId(), verificationBodyId);
		verify(roleRepository, never()).findByCode(anyString());
		verify(authorityAssignmentService, never()).updateAuthorityWithNewRole(any(), any());
		verify(authorityRepository, times(1)).save(authority);
		verify(eventPublisher, times(1)).publishEvent(VerifierAdminCreationEvent.builder()
				.verificationBodyId(authority.getVerificationBodyId()).build());
		verifyNoMoreInteractions(eventPublisher);
	}

	@Test
	void updateVerifierAuthorities_code_not_changed() {
		Long verificationBodyId = 1L;
		String user = "user";
		Authority authority = 
				Authority.builder()
						.userId(user)
						.status(AuthorityStatus.ACTIVE)
						.code("role1")
						.build();
		
		List<VerifierAuthorityUpdateDTO> verifiersUpdate = 
				List.of(
						VerifierAuthorityUpdateDTO.builder().userId(user).authorityStatus(AuthorityStatus.DISABLED).roleCode("role1").build()
						);
		
		when(authorityRepository.findByUserIdAndVerificationBodyId(verifiersUpdate.get(0).getUserId(), verificationBodyId))
				.thenReturn(Optional.of(authority));

		// Invoke
		List<String> actual = service.updateVerifierAuthorities(verifiersUpdate, verificationBodyId);

		// Verify
		assertThat(authority.getStatus()).isEqualTo(AuthorityStatus.DISABLED);
		assertThat(actual).isEmpty();
		verify(verifierAdminExistenceValidator, times(1)).validateUpdate(verifiersUpdate, verificationBodyId);
		verify(verifierStatusModificationAllowanceValidator, times(1)).validateUpdate(verifiersUpdate, verificationBodyId);
		verify(authorityRepository, times(1)).findByUserIdAndVerificationBodyId(verifiersUpdate.get(0).getUserId(), verificationBodyId);
		verify(roleRepository, never()).findByCode(Mockito.anyString());
		verify(authorityAssignmentService, never()).updateAuthorityWithNewRole(Mockito.any(), Mockito.any());
		verify(authorityRepository, times(1)).save(authority);
		verify(eventPublisher, times(1)).publishEvent(VerifierUserDisabledEvent.builder().userId(user).build());
		verifyNoMoreInteractions(eventPublisher);
	}
	
	@Test
    void updateVerifierAuthorities_authority_not_found() {
        Long verificationBodyId = 1L;
        String user = "user";
        List<VerifierAuthorityUpdateDTO> verifiersUpdate = 
                List.of(
                        VerifierAuthorityUpdateDTO.builder().userId(user).authorityStatus(AuthorityStatus.DISABLED).roleCode("role2").build()
                        );
        
        when(authorityRepository.findByUserIdAndVerificationBodyId(verifiersUpdate.get(0).getUserId(), verificationBodyId))
            .thenReturn(Optional.empty());
        
        BusinessException ex = assertThrows(BusinessException.class, () ->
				service.updateVerifierAuthorities(verifiersUpdate, verificationBodyId));
        
        assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.AUTHORITY_USER_NOT_RELATED_TO_VERIFICATION_BODY);
        assertThat(ex.getData()).isEqualTo(new Object[]{user});
		verify(verifierAdminExistenceValidator, times(1)).validateUpdate(verifiersUpdate, verificationBodyId);
		verify(verifierStatusModificationAllowanceValidator, times(1)).validateUpdate(verifiersUpdate, verificationBodyId);
        verify(authorityRepository, times(1)).findByUserIdAndVerificationBodyId(user, verificationBodyId);
        verifyNoMoreInteractions(authorityRepository);
        verifyNoInteractions(roleRepository, authorityAssignmentService);
        verify(verifierAdminExistenceValidator, times(1)).validateUpdate(verifiersUpdate, verificationBodyId);
		verify(eventPublisher, never()).publishEvent(any());
	}

	@Test
	void updateVerifierAuthorities_authority_not_valid() {
		Long verificationBodyId = 1L;
		String user = "user";
		List<VerifierAuthorityUpdateDTO> verifiersUpdate =
				List.of(
						VerifierAuthorityUpdateDTO.builder().userId(user).authorityStatus(AuthorityStatus.DISABLED).roleCode("role2").build()
				);

		doThrow(new BusinessException(ErrorCode.AUTHORITY_VERIFIER_ADMIN_SHOULD_EXIST, user))
				.when(verifierAdminExistenceValidator).validateUpdate(verifiersUpdate, verificationBodyId);

		BusinessException ex = assertThrows(BusinessException.class, () ->
				service.updateVerifierAuthorities(verifiersUpdate, verificationBodyId));

		assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.AUTHORITY_VERIFIER_ADMIN_SHOULD_EXIST);
		assertThat(ex.getData()).isEqualTo(new Object[]{user});
		verify(verifierAdminExistenceValidator, times(1)).validateUpdate(verifiersUpdate, verificationBodyId);
		verifyNoInteractions(verifierStatusModificationAllowanceValidator, authorityRepository, roleRepository, authorityAssignmentService);
		verify(eventPublisher, never()).publishEvent(any());
	}

	@Test
	void updateTemporaryStatusByVerificationBodyIds() {
		Set<Long> ids = Set.of(1L, 2L);
		Authority authority1 = Authority.builder().status(AuthorityStatus.TEMP_DISABLED).build();
		Authority authority2 = Authority.builder().status(AuthorityStatus.TEMP_DISABLED_PENDING).build();

		// Mock
		when(authorityRepository.findAllByVerificationBodyIdInAndStatusIn(ids, Set.of(AuthorityStatus.TEMP_DISABLED, AuthorityStatus.TEMP_DISABLED_PENDING)))
				.thenReturn(List.of(authority1, authority2));

		// Invoke
		service.updateTemporaryStatusByVerificationBodyIds(ids);

		// Assert
		assertEquals(AuthorityStatus.ACTIVE, authority1.getStatus());
		assertEquals(AuthorityStatus.PENDING, authority2.getStatus());
		verify(authorityRepository, times(1))
				.findAllByVerificationBodyIdInAndStatusIn(ids, Set.of(AuthorityStatus.TEMP_DISABLED, AuthorityStatus.TEMP_DISABLED_PENDING));
		verify(authorityRepository, times(1)).saveAll(List.of(authority1, authority2));
	}

	@Test
	void updateTemporaryStatusByVerificationBodyIds_only_temp_disabled() {
		Set<Long> ids = Set.of(1L);
		Authority authority = Authority.builder().status(AuthorityStatus.TEMP_DISABLED).build();

		// Mock
		when(authorityRepository.findAllByVerificationBodyIdInAndStatusIn(ids, Set.of(AuthorityStatus.TEMP_DISABLED, AuthorityStatus.TEMP_DISABLED_PENDING)))
				.thenReturn(List.of(authority));

		// Invoke
		service.updateTemporaryStatusByVerificationBodyIds(ids);

		// Assert
		assertEquals(AuthorityStatus.ACTIVE, authority.getStatus());
		verify(authorityRepository, times(1))
				.findAllByVerificationBodyIdInAndStatusIn(ids, Set.of(AuthorityStatus.TEMP_DISABLED, AuthorityStatus.TEMP_DISABLED_PENDING));
		verify(authorityRepository, times(1)).saveAll(List.of(authority));
	}

	@Test
	void updateTemporaryStatusByVerificationBodyIds_only_temp_pending() {
		Set<Long> ids = Set.of(1L);
		Authority authority = Authority.builder().status(AuthorityStatus.TEMP_DISABLED_PENDING).build();

		// Mock
		when(authorityRepository.findAllByVerificationBodyIdInAndStatusIn(ids, Set.of(AuthorityStatus.TEMP_DISABLED, AuthorityStatus.TEMP_DISABLED_PENDING)))
				.thenReturn(List.of(authority));

		// Invoke
		service.updateTemporaryStatusByVerificationBodyIds(ids);

		// Assert
		assertEquals(AuthorityStatus.PENDING, authority.getStatus());
		verify(authorityRepository, times(1))
				.findAllByVerificationBodyIdInAndStatusIn(ids, Set.of(AuthorityStatus.TEMP_DISABLED, AuthorityStatus.TEMP_DISABLED_PENDING));
		verify(authorityRepository, times(1)).saveAll(List.of(authority));
	}

	@Test
	void updateStatusToTemporaryByVerificationBodyIds() {
		Set<Long> ids = Set.of(1L, 2L);
		Authority authority1 = Authority.builder().status(AuthorityStatus.ACTIVE).build();
		Authority authority2 = Authority.builder().status(AuthorityStatus.PENDING).build();

		// Mock
		when(authorityRepository.findAllByVerificationBodyIdInAndStatusIn(ids, Set.of(AuthorityStatus.ACTIVE, AuthorityStatus.PENDING)))
				.thenReturn(List.of(authority1, authority2));

		// Invoke
		service.updateStatusToTemporaryByVerificationBodyIds(ids);

		// Assert
		assertEquals(AuthorityStatus.TEMP_DISABLED, authority1.getStatus());
		assertEquals(AuthorityStatus.TEMP_DISABLED_PENDING, authority2.getStatus());
		verify(authorityRepository, times(1))
				.findAllByVerificationBodyIdInAndStatusIn(ids, Set.of(AuthorityStatus.ACTIVE, AuthorityStatus.PENDING));
		verify(authorityRepository, times(1)).saveAll(List.of(authority1, authority2));
	}

	@Test
	void updateStatusToTemporaryByVerificationBodyIds_only_active() {
		Set<Long> ids = Set.of(1L);
		Authority authority = Authority.builder().status(AuthorityStatus.ACTIVE).build();

		// Mock
		when(authorityRepository.findAllByVerificationBodyIdInAndStatusIn(ids, Set.of(AuthorityStatus.ACTIVE, AuthorityStatus.PENDING)))
				.thenReturn(List.of(authority));

		// Invoke
		service.updateStatusToTemporaryByVerificationBodyIds(ids);

		// Assert
		assertEquals(AuthorityStatus.TEMP_DISABLED, authority.getStatus());
		verify(authorityRepository, times(1))
				.findAllByVerificationBodyIdInAndStatusIn(ids, Set.of(AuthorityStatus.ACTIVE, AuthorityStatus.PENDING));
		verify(authorityRepository, times(1)).saveAll(List.of(authority));
	}

	@Test
	void updateStatusToTemporaryByVerificationBodyIds_only_pending() {
		Set<Long> ids = Set.of(1L);
		Authority authority = Authority.builder().status(AuthorityStatus.PENDING).build();

		// Mock
		when(authorityRepository.findAllByVerificationBodyIdInAndStatusIn(ids, Set.of(AuthorityStatus.ACTIVE, AuthorityStatus.PENDING)))
				.thenReturn(List.of(authority));

		// Invoke
		service.updateStatusToTemporaryByVerificationBodyIds(ids);

		// Assert
		assertEquals(AuthorityStatus.TEMP_DISABLED_PENDING, authority.getStatus());
		verify(authorityRepository, times(1))
				.findAllByVerificationBodyIdInAndStatusIn(ids, Set.of(AuthorityStatus.ACTIVE, AuthorityStatus.PENDING));
		verify(authorityRepository, times(1)).saveAll(List.of(authority));
	}
}
