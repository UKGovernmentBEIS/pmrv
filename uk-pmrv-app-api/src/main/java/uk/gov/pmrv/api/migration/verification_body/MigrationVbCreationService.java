package uk.gov.pmrv.api.migration.verification_body;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.actuate.autoconfigure.endpoint.condition.ConditionalOnAvailableEndpoint;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.pmrv.api.migration.MigrationEndpoint;
import uk.gov.pmrv.api.verificationbody.domain.dto.VerificationBodyEditDTO;
import uk.gov.pmrv.api.verificationbody.domain.dto.VerificationBodyInfoDTO;
import uk.gov.pmrv.api.verificationbody.enumeration.VerificationBodyStatus;
import uk.gov.pmrv.api.verificationbody.repository.VerificationBodyRepository;
import uk.gov.pmrv.api.verificationbody.service.VerificationBodyCreationService;
import uk.gov.pmrv.api.verificationbody.service.VerificationBodyManagementService;

@Service
@ConditionalOnAvailableEndpoint(endpoint = MigrationEndpoint.class)
@Log4j2
@RequiredArgsConstructor
public class MigrationVbCreationService {
    private final VerificationBodyCreationService verificationBodyCreationService;
    private final VerificationBodyManagementService verificationBodyManagementService;
    private final VerificationBodyRepository verificationBodyRepository;

    @Transactional
    public void createVb(VerificationBodyEditDTO verificationBodyEditDTO, boolean isDisabled) {
        VerificationBodyInfoDTO verificationBody = verificationBodyCreationService.createVerificationBody(verificationBodyEditDTO);
        if (isDisabled) {
            verificationBodyRepository.findByIdAndStatus(verificationBody.getId(), VerificationBodyStatus.PENDING)
                    .ifPresent(vb -> vb.setStatus(VerificationBodyStatus.DISABLED));
        } else {
            verificationBodyManagementService.activateVerificationBody(verificationBody.getId());
        }
    }
}

