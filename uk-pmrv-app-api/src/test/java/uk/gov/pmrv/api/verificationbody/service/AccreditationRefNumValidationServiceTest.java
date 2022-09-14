package uk.gov.pmrv.api.verificationbody.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.verificationbody.domain.VerificationBody;
import uk.gov.pmrv.api.verificationbody.repository.VerificationBodyRepository;

@ExtendWith(MockitoExtension.class)
class AccreditationRefNumValidationServiceTest {

    @InjectMocks
    private AccreditationRefNumValidationService accreditationRefNumValidationService;

    @Mock
    private VerificationBodyRepository verificationBodyRepository;

    @Test
    void validate() {
        String accreditationReferenceNumber = "accreditationReferenceNumber";
        VerificationBody verificationBody = VerificationBody.builder()
            .name("name")
            .accreditationReferenceNumber("accrRefNum")
            .build();

        when(verificationBodyRepository.findAll()).thenReturn(List.of(verificationBody));

        accreditationRefNumValidationService.validate(accreditationReferenceNumber);

        verify(verificationBodyRepository, times(1)).findAll();
    }

    @Test
    void validate_invalid_accreditation_ref_num() {
        String accreditationReferenceNumber = "accreditationReferenceNumber";
        VerificationBody verificationBody = VerificationBody.builder()
            .name("name")
            .accreditationReferenceNumber(accreditationReferenceNumber)
            .build();

        when(verificationBodyRepository.findAll()).thenReturn(List.of(verificationBody));

        BusinessException be = assertThrows(BusinessException.class, () ->
            accreditationRefNumValidationService.validate(accreditationReferenceNumber));

        assertThat(be.getErrorCode()).isEqualTo(ErrorCode.VERIFICATION_BODY_CONTAINS_NON_UNIQUE_REF_NUM);

        verify(verificationBodyRepository, times(1)).findAll();
    }

    @Test
    void validate_with_vb() {
        Long verificationBodyId = 1L;
        String accreditationReferenceNumber = "accreditationReferenceNumber";
        VerificationBody verificationBody = VerificationBody.builder()
            .name("name")
            .accreditationReferenceNumber("accrRefNum")
            .build();

        when(verificationBodyRepository.findByIdNot(verificationBodyId)).thenReturn(List.of(verificationBody));

        accreditationRefNumValidationService.validate(accreditationReferenceNumber, verificationBodyId);

        verify(verificationBodyRepository, times(1)).findByIdNot(verificationBodyId);
    }
}