package uk.gov.pmrv.api.authorization.verifier.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.pmrv.api.authorization.core.domain.Authority;
import uk.gov.pmrv.api.authorization.core.domain.AuthorityStatus;
import uk.gov.pmrv.api.authorization.core.repository.AuthorityRepository;
import uk.gov.pmrv.api.authorization.verifier.domain.VerifierAuthorityUpdateDTO;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VerifierStatusModificationAllowanceValidatorTest {

    @InjectMocks
    private VerifierStatusModificationAllowanceValidator validator;

    @Mock
    private AuthorityRepository authorityRepository;

    @Test
    void validateUpdate() {
        Long verificationBodyId = 1L;
        String userId1 = "userId1";
        String userId2 = "userId2";
        Set<String> users = Set.of(userId1, userId2);
        List<VerifierAuthorityUpdateDTO> verifiersUpdate = List.of(
                VerifierAuthorityUpdateDTO.builder().userId(userId1).authorityStatus(AuthorityStatus.DISABLED).build(),
                VerifierAuthorityUpdateDTO.builder().userId(userId2).authorityStatus(AuthorityStatus.ACCEPTED).build()
        );
        List<Authority> authorities = List.of(
                Authority.builder().userId(userId1).status(AuthorityStatus.ACTIVE).build(),
                Authority.builder().userId(userId2).status(AuthorityStatus.ACCEPTED).build()
        );

        when(authorityRepository.findAllByUserIdInAndVerificationBodyId(users, verificationBodyId))
                .thenReturn(authorities);

        // Invoke
        validator.validateUpdate(verifiersUpdate, verificationBodyId);

        // Verify
        verify(authorityRepository, times(1))
                .findAllByUserIdInAndVerificationBodyId(users, verificationBodyId);
    }

    @Test
    void validateUpdate_status_not_valid() {
        Long verificationBodyId = 1L;
        String userId1 = "userId1";
        String userId2 = "userId2";
        Set<String> users = Set.of(userId1, userId2);
        List<VerifierAuthorityUpdateDTO> verifiersUpdate = List.of(
                VerifierAuthorityUpdateDTO.builder().userId(userId1).authorityStatus(AuthorityStatus.DISABLED).build(),
                VerifierAuthorityUpdateDTO.builder().userId(userId2).authorityStatus(AuthorityStatus.DISABLED).build()
        );
        List<Authority> authorities = List.of(
                Authority.builder().userId(userId1).status(AuthorityStatus.ACTIVE).build(),
                Authority.builder().userId(userId2).status(AuthorityStatus.ACCEPTED).build()
        );

        when(authorityRepository.findAllByUserIdInAndVerificationBodyId(users, verificationBodyId))
                .thenReturn(authorities);

        // Invoke
        BusinessException ex = assertThrows(BusinessException.class, () ->
                validator.validateUpdate(verifiersUpdate, verificationBodyId));

        // Verify
        assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.AUTHORITY_INVALID_STATUS);
        verify(authorityRepository, times(1))
                .findAllByUserIdInAndVerificationBodyId(users, verificationBodyId);
    }
}
