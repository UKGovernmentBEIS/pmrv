package uk.gov.pmrv.api.authorization.regulator.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.pmrv.api.authorization.core.domain.AuthorityStatus.ACCEPTED;
import static uk.gov.pmrv.api.authorization.core.domain.AuthorityStatus.ACTIVE;
import static uk.gov.pmrv.api.authorization.core.domain.AuthorityStatus.DISABLED;

import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.authorization.core.domain.AuthorityStatus;
import uk.gov.pmrv.api.authorization.core.repository.AuthorityRepository;
import uk.gov.pmrv.api.common.domain.enumeration.CompetentAuthority;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.authorization.regulator.domain.RegulatorUserUpdateStatusDTO;

@ExtendWith(MockitoExtension.class)
class RegulatorStatusModificationAllowanceValidatorTest {

    @InjectMocks
    private RegulatorStatusModificationAllowanceValidator validator;

    @Mock
    private AuthorityRepository authorityRepository;

    @Test
    void validate_whenActiveToAccepted_thenOK() {

        final String userId1 = "userId1";
        final String userId2 = "userId2";
        final List<RegulatorUserUpdateStatusDTO> regulatorUserUpdateStatuses = List.of(
            RegulatorUserUpdateStatusDTO.builder().userId(userId1).authorityStatus(ACTIVE).build(),
            RegulatorUserUpdateStatusDTO.builder().userId(userId2).authorityStatus(ACTIVE).build()
        );
        final Map<String, AuthorityStatus> existingUserStatuses = Map.of(
            userId1, DISABLED,
            userId2, ACCEPTED
        );
        final CompetentAuthority ca = CompetentAuthority.ENGLAND;

        when(authorityRepository.findStatusByUsersAndCA(List.of(userId1, userId2), ca)).thenReturn(existingUserStatuses);

        validator.validateUpdate(regulatorUserUpdateStatuses, ca);

        verify(authorityRepository, times(1)).findStatusByUsersAndCA(List.of(userId1, userId2), ca);
    }

    @Test
    void validate_whenAcceptedToAccepted_thenOK() {

        final String userId1 = "userId1";
        final String userId2 = "userId2";
        final List<RegulatorUserUpdateStatusDTO> regulatorUserUpdateStatuses = List.of(
            RegulatorUserUpdateStatusDTO.builder().userId(userId1).authorityStatus(ACTIVE).build(),
            RegulatorUserUpdateStatusDTO.builder().userId(userId2).authorityStatus(ACCEPTED).build()
        );
        final Map<String, AuthorityStatus> existingUserStatuses = Map.of(
            userId1, DISABLED,
            userId2, ACCEPTED
        );
        final CompetentAuthority ca = CompetentAuthority.ENGLAND;

        when(authorityRepository.findStatusByUsersAndCA(List.of(userId1, userId2), ca)).thenReturn(existingUserStatuses);

        validator.validateUpdate(regulatorUserUpdateStatuses, ca);

        verify(authorityRepository, times(1)).findStatusByUsersAndCA(List.of(userId1, userId2), ca);
    }

    @Test
    void validate_whenAcceptedToDisabled_thenThrowException() {

        final String userId1 = "userId1";
        final String userId2 = "userId2";
        final List<RegulatorUserUpdateStatusDTO> regulatorUserUpdateStatuses = List.of(
            RegulatorUserUpdateStatusDTO.builder().userId(userId1).authorityStatus(ACTIVE).build(),
            RegulatorUserUpdateStatusDTO.builder().userId(userId2).authorityStatus(DISABLED).build()
        );
        final Map<String, AuthorityStatus> existingUserStatuses = Map.of(
            userId1, DISABLED,
            userId2, ACCEPTED
        );
        final CompetentAuthority ca = CompetentAuthority.ENGLAND;

        when(authorityRepository.findStatusByUsersAndCA(List.of(userId1, userId2), ca)).thenReturn(existingUserStatuses);

        final BusinessException businessException = assertThrows(BusinessException.class, () ->
            validator.validateUpdate(regulatorUserUpdateStatuses, ca));

        assertThat(businessException.getErrorCode()).isEqualTo(ErrorCode.AUTHORITY_INVALID_STATUS);

        verify(authorityRepository, times(1)).findStatusByUsersAndCA(List.of(userId1, userId2), ca);
    }
}
