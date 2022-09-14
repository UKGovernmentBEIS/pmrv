package uk.gov.pmrv.api.authorization.verifier.event;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.pmrv.api.authorization.verifier.service.VerifierAuthorityDeletionService;
import uk.gov.pmrv.api.verificationbody.domain.event.VerificationBodyDeletedEvent;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class VerificationBodyDeletedEventListenerTest {

    @InjectMocks
    private VerificationBodyDeletedEventListener listener;

    @Mock
    private VerifierAuthorityDeletionService verifierAuthorityDeletionService;

    @Test
    void onVerificationBodyStatusDisabledEvent() {
        final Long verificationBodyId = 1L;
        VerificationBodyDeletedEvent event = new VerificationBodyDeletedEvent(verificationBodyId);

        listener.onVerificationBodyDeletedEvent(event);

        verify(verifierAuthorityDeletionService,times(1))
                .deleteVerifierAuthorities(verificationBodyId);
    }
}
