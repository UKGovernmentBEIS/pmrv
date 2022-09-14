package uk.gov.pmrv.api.verificationbody.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.pmrv.api.verificationbody.domain.VerificationBody;
import uk.gov.pmrv.api.verificationbody.domain.dto.VerificationBodyEditDTO;
import uk.gov.pmrv.api.verificationbody.domain.dto.VerificationBodyInfoDTO;
import uk.gov.pmrv.api.verificationbody.enumeration.VerificationBodyStatus;
import uk.gov.pmrv.api.verificationbody.repository.VerificationBodyRepository;
import uk.gov.pmrv.api.verificationbody.transform.VerificationBodyMapper;

@Service
@RequiredArgsConstructor
public class VerificationBodyCreationService {

    private final VerificationBodyRepository verificationBodyRepository;
    private final AccreditationRefNumValidationService accreditationRefNumValidationService;
    private final VerificationBodyMapper verificationBodyMapper;

    @Transactional
    public VerificationBodyInfoDTO createVerificationBody(VerificationBodyEditDTO verificationBodyCreationDTO) {
        accreditationRefNumValidationService.validate(verificationBodyCreationDTO.getAccreditationReferenceNumber());
        
        VerificationBody verificationBody = verificationBodyMapper.toVerificationBody(verificationBodyCreationDTO);
        verificationBody.setStatus(VerificationBodyStatus.PENDING);

        VerificationBody persistedVerificationBody = verificationBodyRepository.save(verificationBody);

        return verificationBodyMapper.toVerificationBodyInfoDTO(persistedVerificationBody);
    }
}
