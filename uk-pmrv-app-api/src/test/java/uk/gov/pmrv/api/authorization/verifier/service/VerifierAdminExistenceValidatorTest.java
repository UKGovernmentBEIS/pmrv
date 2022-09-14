package uk.gov.pmrv.api.authorization.verifier.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.pmrv.api.authorization.AuthorityConstants;
import uk.gov.pmrv.api.authorization.core.domain.Authority;
import uk.gov.pmrv.api.authorization.core.domain.AuthorityStatus;
import uk.gov.pmrv.api.authorization.verifier.domain.VerifierAuthorityUpdateDTO;
import uk.gov.pmrv.api.authorization.core.repository.AuthorityRepository;
import uk.gov.pmrv.api.authorization.verifier.service.VerifierAdminExistenceValidator;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;

@ExtendWith(MockitoExtension.class)
class VerifierAdminExistenceValidatorTest {
	
	@InjectMocks
	private VerifierAdminExistenceValidator validator;
	
	@Mock
	private AuthorityRepository authorityRepository;
	
	@Test
	void validateUpdate_active_admin_selected() {
		Long verificationBodyId = 1L;
		List<VerifierAuthorityUpdateDTO> verifiersUpdate = 
			List.of(
					VerifierAuthorityUpdateDTO.builder().userId("user1").authorityStatus(AuthorityStatus.ACTIVE).roleCode(AuthorityConstants.VERIFIER_ADMIN_ROLE_CODE).build()
					);
		
		//invoke
		validator.validateUpdate(verifiersUpdate, verificationBodyId);
		
		verify(authorityRepository, never()).findUsersByVerificationBodyAndCode(verificationBodyId, AuthorityConstants.VERIFIER_ADMIN_ROLE_CODE);
	}
	
	@Test
	void validateUpdate_no_active_admin_selected_but_another_admin_exist() {
		Long verificationBodyId = 1L;
		List<VerifierAuthorityUpdateDTO> verifiersUpdate = 
			List.of(
					VerifierAuthorityUpdateDTO.builder().userId("user1").authorityStatus(AuthorityStatus.DISABLED).roleCode(AuthorityConstants.VERIFIER_ADMIN_ROLE_CODE).build(),
					VerifierAuthorityUpdateDTO.builder().userId("user2").authorityStatus(AuthorityStatus.ACTIVE).roleCode("verifier").build()
					);
		
		when(authorityRepository.findUsersByVerificationBodyAndCode(verificationBodyId,AuthorityConstants.VERIFIER_ADMIN_ROLE_CODE))
			.thenReturn(List.of("another_admin"));
		
		//invoke
		validator.validateUpdate(verifiersUpdate, verificationBodyId);
		
		verify(authorityRepository, times(1)).findUsersByVerificationBodyAndCode(verificationBodyId, AuthorityConstants.VERIFIER_ADMIN_ROLE_CODE);
	}
	
	@Test
	void validateUpdate_no_active_admin_selected_and_admin_found_but_is_to_be_updated() {
		String admin = "admin";
		Long verificationBodyId = 1L;
		List<VerifierAuthorityUpdateDTO> verifiersUpdate = 
			List.of(
					VerifierAuthorityUpdateDTO.builder().userId(admin).authorityStatus(AuthorityStatus.DISABLED).roleCode(AuthorityConstants.VERIFIER_ADMIN_ROLE_CODE).build(),
					VerifierAuthorityUpdateDTO.builder().userId("user2").authorityStatus(AuthorityStatus.ACTIVE).roleCode("verifier").build()
					);
		
		when(authorityRepository.findUsersByVerificationBodyAndCode(verificationBodyId, AuthorityConstants.VERIFIER_ADMIN_ROLE_CODE))
			.thenReturn(List.of(admin));
		
		//invoke
		BusinessException exc = assertThrows(BusinessException.class, () -> {
			validator.validateUpdate(verifiersUpdate, verificationBodyId);
		});
		
		assertThat(exc.getErrorCode()).isEqualTo(ErrorCode.AUTHORITY_VERIFIER_ADMIN_SHOULD_EXIST);
		verify(authorityRepository, times(1)).findUsersByVerificationBodyAndCode(verificationBodyId, AuthorityConstants.VERIFIER_ADMIN_ROLE_CODE);
	}

	@Test
	void validateDeletion_only_verifier_admin() {
		Authority authority = Authority.builder().userId("userId").verificationBodyId(1L).code(AuthorityConstants.VERIFIER_ADMIN_ROLE_CODE).build();
		when(authorityRepository.existsOtherVerificationBodyAdmin(authority.getUserId(), authority.getVerificationBodyId())).thenReturn(false);
		BusinessException exception = assertThrows(BusinessException.class, () -> validator.validateDeletion(authority));
		assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.AUTHORITY_VERIFIER_ADMIN_SHOULD_EXIST);
	}

	@Test
	void validateDeletion_more_verifier_admins() {
		Authority authority = Authority.builder().userId("userId").verificationBodyId(1L).code(AuthorityConstants.VERIFIER_ADMIN_ROLE_CODE).build();
		when(authorityRepository.existsOtherVerificationBodyAdmin(authority.getUserId(), authority.getVerificationBodyId())).thenReturn(true);
		validator.validateDeletion(authority);
	}

}
