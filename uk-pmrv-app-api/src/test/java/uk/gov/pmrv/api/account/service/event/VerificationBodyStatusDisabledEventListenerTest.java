package uk.gov.pmrv.api.account.service.event;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.pmrv.api.account.service.AccountVerificationBodyUnappointService;
import uk.gov.pmrv.api.verificationbody.domain.event.VerificationBodyStatusDisabledEvent;

import java.util.Set;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class VerificationBodyStatusDisabledEventListenerTest {

    @InjectMocks
    private VerificationBodyStatusDisabledEventListener listener;

    @Mock
    private AccountVerificationBodyUnappointService accountVerificationBodyUnappointService;

    @Test
    void onVerificationBodyStatusDisabledEvent() {
        VerificationBodyStatusDisabledEvent event = new VerificationBodyStatusDisabledEvent(Set.of(1L, 2L));

        listener.onVerificationBodyStatusDisabledEvent(event);

        verify(accountVerificationBodyUnappointService,times(1))
                .unappointAccountsAppointedToVerificationBody(Set.of(1L, 2L));
    }
}
