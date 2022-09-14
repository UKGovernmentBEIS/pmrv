package uk.gov.pmrv.api.verificationbody.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.pmrv.api.verificationbody.enumeration.VerificationBodyStatus.PENDING;

import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.common.domain.enumeration.EmissionTradingScheme;
import uk.gov.pmrv.api.verificationbody.domain.VerificationBody;
import uk.gov.pmrv.api.verificationbody.domain.dto.VerificationBodyEditDTO;
import uk.gov.pmrv.api.verificationbody.domain.dto.VerificationBodyInfoDTO;
import uk.gov.pmrv.api.verificationbody.repository.VerificationBodyRepository;
import uk.gov.pmrv.api.verificationbody.transform.VerificationBodyMapper;

@ExtendWith(MockitoExtension.class)
class VerificationBodyCreationServiceTest {

    @InjectMocks
    private VerificationBodyCreationService verificationBodyCreationService;

    @Mock
    private VerificationBodyRepository verificationBodyRepository;
    
    @Mock
    private AccreditationRefNumValidationService accreditationRefNumValidationService;

    @Mock
    private VerificationBodyMapper verificationBodyMapper;

    @Test
    void createVerificationBody() {
        String vbName = "vbName";
        String accreditationReferenceNumber = "accreditationReferenceNumber";
        VerificationBodyEditDTO verificationBodyCreationInfoDTO = VerificationBodyEditDTO.builder()
            .name(vbName)
            .accreditationReferenceNumber(accreditationReferenceNumber)
            .emissionTradingSchemes(Set.of(EmissionTradingScheme.UK_ETS_INSTALLATIONS))
            .build();
        VerificationBody verificationBody = VerificationBody.builder().name(vbName).build();
        VerificationBodyInfoDTO verificationBodyInfoDTO = VerificationBodyInfoDTO.builder().name(vbName).status(PENDING).build();
        VerificationBody savedVerificationBody = VerificationBody.builder().name(vbName).status(PENDING).build();

        when(verificationBodyMapper.toVerificationBody(verificationBodyCreationInfoDTO)).thenReturn(verificationBody);
        when(verificationBodyRepository.save(verificationBody)).thenReturn(savedVerificationBody);
        when(verificationBodyMapper.toVerificationBodyInfoDTO(savedVerificationBody)).thenReturn(verificationBodyInfoDTO);

        VerificationBodyInfoDTO actualVerificationBodyInfoDTO =
            verificationBodyCreationService.createVerificationBody(verificationBodyCreationInfoDTO);

        verify(accreditationRefNumValidationService, times(1)).validate(accreditationReferenceNumber);
        verify(verificationBodyMapper, times(1)).toVerificationBody(verificationBodyCreationInfoDTO);
        verify(verificationBodyRepository, times(1)).save(verificationBody);
        verify(verificationBodyMapper, times(1)).toVerificationBodyInfoDTO(verificationBody);

        assertEquals(verificationBodyInfoDTO, actualVerificationBodyInfoDTO);
    }
}