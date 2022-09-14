package uk.gov.pmrv.api.authorization.verifier.event;

import lombok.RequiredArgsConstructor;

import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import uk.gov.pmrv.api.verificationbody.domain.event.VerificationBodyStatusDisabledEvent;
import uk.gov.pmrv.api.verificationbody.domain.event.VerificationBodyStatusEnabledEvent;
import uk.gov.pmrv.api.authorization.verifier.service.VerifierAuthorityUpdateService;

@RequiredArgsConstructor
@Component
public class VerificationBodyStatusChangedEventListener {

    private final VerifierAuthorityUpdateService verifierAuthorityUpdateService;

    @Order(1)
    @EventListener(VerificationBodyStatusDisabledEvent.class)
    public void onVerificationBodyStatusDisabledEvent(VerificationBodyStatusDisabledEvent event) {
        // Update Verifier Users authorities under Verification Body id
        verifierAuthorityUpdateService.updateStatusToTemporaryByVerificationBodyIds(event.getVerificationBodyIds());
    }

    @EventListener(VerificationBodyStatusEnabledEvent.class)
    public void onVerificationBodyStatusEnabledEvent(VerificationBodyStatusEnabledEvent event) {
        // Update Verifier Users authorities under Verification Body id
        verifierAuthorityUpdateService.updateTemporaryStatusByVerificationBodyIds(event.getVerificationBodyIds());
    }
}
