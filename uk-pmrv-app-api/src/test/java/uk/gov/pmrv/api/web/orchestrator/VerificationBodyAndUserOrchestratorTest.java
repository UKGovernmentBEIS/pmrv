package uk.gov.pmrv.api.web.orchestrator;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.common.domain.model.PmrvUser;
import uk.gov.pmrv.api.user.verifier.service.VerifierUserInvitationService;
import uk.gov.pmrv.api.user.verifier.domain.AdminVerifierUserInvitationDTO;
import uk.gov.pmrv.api.verificationbody.domain.dto.VerificationBodyEditDTO;
import uk.gov.pmrv.api.web.orchestrator.dto.VerificationBodyCreationDTO;
import uk.gov.pmrv.api.verificationbody.domain.dto.VerificationBodyInfoDTO;
import uk.gov.pmrv.api.verificationbody.enumeration.VerificationBodyStatus;
import uk.gov.pmrv.api.verificationbody.service.VerificationBodyCreationService;

@ExtendWith(MockitoExtension.class)
class VerificationBodyAndUserOrchestratorTest {

    @InjectMocks
    private VerificationBodyAndUserOrchestrator orchestrator;

    @Mock
    private VerificationBodyCreationService verificationBodyCreationService;

    @Mock
    private VerifierUserInvitationService verifierUserInvitationService;

    @Test
    void createVerificationBody() {
        Long vbId = 1L;
        String vbName = "vbName";
        PmrvUser pmrvUser = PmrvUser.builder().userId("authUserId").build();
        VerificationBodyEditDTO vbCreationInfoDTO  = VerificationBodyEditDTO.builder().name("name").build();
        AdminVerifierUserInvitationDTO adminVerifierUser = AdminVerifierUserInvitationDTO.builder().email("email").build();
        VerificationBodyCreationDTO verificationBodyCreationDTO = VerificationBodyCreationDTO.builder()
            .verificationBody(vbCreationInfoDTO)
            .adminVerifierUserInvitation(adminVerifierUser)
            .build();

        VerificationBodyInfoDTO verificationBodyInfoDTO = VerificationBodyInfoDTO.builder()
            .id(vbId)
            .name(vbName)
            .status(VerificationBodyStatus.PENDING)
            .build();

        when(verificationBodyCreationService.createVerificationBody(vbCreationInfoDTO))
            .thenReturn(verificationBodyInfoDTO);

        VerificationBodyInfoDTO expectedDTO = orchestrator.createVerificationBody(pmrvUser, verificationBodyCreationDTO);

        assertEquals(verificationBodyInfoDTO, expectedDTO);

        verify(verificationBodyCreationService, times(1)).createVerificationBody(vbCreationInfoDTO);
        verify(verifierUserInvitationService, times(1))
            .inviteVerifierAdminUser(pmrvUser, adminVerifierUser, vbId);
    }
}