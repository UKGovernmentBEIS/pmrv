package uk.gov.pmrv.api.user.verifier.service.event;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.pmrv.api.authorization.verifier.event.VerifierAuthorityDeletionEvent;
import uk.gov.pmrv.api.user.core.service.auth.AuthService;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class VerifierAuthorityDeletionEventListenerTest {

    @InjectMocks
    private VerifierAuthorityDeletionEventListener listener;

    @Mock
    private AuthService authService;

    @Test
    void onVerifierUserDeletedEvent_not_delete() {
        String userId = "userId";
        VerifierAuthorityDeletionEvent deletionEvent = VerifierAuthorityDeletionEvent.builder()
                .userId(userId)
                .build();

        listener.onVerifierUserDeletedEvent(deletionEvent);

        verify(authService, never()).deleteUser(anyString());
        verify(authService, times(1)).disableUser(userId);
    }
}
