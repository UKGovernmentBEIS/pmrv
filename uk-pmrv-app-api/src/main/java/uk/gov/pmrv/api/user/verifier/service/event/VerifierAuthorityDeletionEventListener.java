package uk.gov.pmrv.api.user.verifier.service.event;

import lombok.RequiredArgsConstructor;

import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import uk.gov.pmrv.api.authorization.verifier.event.VerifierAuthorityDeletionEvent;
import uk.gov.pmrv.api.user.core.service.auth.AuthService;

@RequiredArgsConstructor
@Component(value =  "verifierUserDeletionEventListener")
public class VerifierAuthorityDeletionEventListener {

    private final AuthService authService;

    @Order(3)
    @EventListener(VerifierAuthorityDeletionEvent.class)
    public void onVerifierUserDeletedEvent(VerifierAuthorityDeletionEvent deletionEvent) {
        authService.disableUser(deletionEvent.getUserId());
    }
}
