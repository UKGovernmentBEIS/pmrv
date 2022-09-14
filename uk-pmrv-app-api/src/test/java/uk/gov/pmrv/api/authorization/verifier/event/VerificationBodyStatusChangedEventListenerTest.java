package uk.gov.pmrv.api.authorization.verifier.event;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.authorization.verifier.service.VerifierAuthorityUpdateService;
import uk.gov.pmrv.api.verificationbody.domain.event.VerificationBodyStatusDisabledEvent;
import uk.gov.pmrv.api.verificationbody.domain.event.VerificationBodyStatusEnabledEvent;

import java.util.Set;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class VerificationBodyStatusChangedEventListenerTest {

    @InjectMocks
    private VerificationBodyStatusChangedEventListener listener;

    @Mock
    private VerifierAuthorityUpdateService verifierAuthorityUpdateService;

    @Test
    void onVerificationBodyStatusDisabledEvent() {
        VerificationBodyStatusDisabledEvent event = new VerificationBodyStatusDisabledEvent(Set.of(1L, 2L));

        listener.onVerificationBodyStatusDisabledEvent(event);

        verify(verifierAuthorityUpdateService,times(1))
                .updateStatusToTemporaryByVerificationBodyIds(Set.of(1L, 2L));
    }

    @Test
    void onVerificationBodyStatusEnabledEvent() {
        VerificationBodyStatusEnabledEvent event = new VerificationBodyStatusEnabledEvent(Set.of(1L, 2L));

        listener.onVerificationBodyStatusEnabledEvent(event);

        verify(verifierAuthorityUpdateService,times(1))
                .updateTemporaryStatusByVerificationBodyIds(Set.of(1L, 2L));
    }
}
