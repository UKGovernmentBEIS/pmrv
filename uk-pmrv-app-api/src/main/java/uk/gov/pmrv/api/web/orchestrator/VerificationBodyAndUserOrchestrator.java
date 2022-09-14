package uk.gov.pmrv.api.web.orchestrator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.pmrv.api.common.domain.model.PmrvUser;
import uk.gov.pmrv.api.user.verifier.service.VerifierUserInvitationService;
import uk.gov.pmrv.api.web.orchestrator.dto.VerificationBodyCreationDTO;
import uk.gov.pmrv.api.verificationbody.domain.dto.VerificationBodyInfoDTO;
import uk.gov.pmrv.api.verificationbody.service.VerificationBodyCreationService;

@Service
@RequiredArgsConstructor
public class VerificationBodyAndUserOrchestrator {

    private final VerificationBodyCreationService verificationBodyCreationService;
    private final VerifierUserInvitationService verifierUserInvitationService;

    @Transactional
    public VerificationBodyInfoDTO createVerificationBody(PmrvUser pmrvUser, VerificationBodyCreationDTO verificationBodyCreationDTO) {

        VerificationBodyInfoDTO verificationBodyInfoDTO =
            verificationBodyCreationService.createVerificationBody(verificationBodyCreationDTO.getVerificationBody());

        verifierUserInvitationService.inviteVerifierAdminUser(pmrvUser,
            verificationBodyCreationDTO.getAdminVerifierUserInvitation(),
            verificationBodyInfoDTO.getId());

        return verificationBodyInfoDTO;
    }
}
