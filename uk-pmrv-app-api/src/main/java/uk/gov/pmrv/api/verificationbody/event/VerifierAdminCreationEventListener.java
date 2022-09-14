package uk.gov.pmrv.api.verificationbody.event;


import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import uk.gov.pmrv.api.user.verifier.service.VerifierAdminCreationEvent;
import uk.gov.pmrv.api.verificationbody.service.VerificationBodyManagementService;

@Component
@RequiredArgsConstructor
public class VerifierAdminCreationEventListener {

    private final VerificationBodyManagementService verificationBodyManagementService;

    @EventListener(VerifierAdminCreationEvent.class)
    public void onVerifierAdminCreationEvent(VerifierAdminCreationEvent event) {
        verificationBodyManagementService.activateVerificationBody(event.getVerificationBodyId());
    }
}
