package uk.gov.pmrv.api.workflow.request.application.verificationbodyappointed;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.pmrv.api.account.domain.event.AccountVerificationBodyAppointedEvent;

@ExtendWith(MockitoExtension.class)
class AccountVerificationBodyAppointedEventListenerTest {

    @InjectMocks
    private AccountVerificationBodyAppointedEventListener listener;
    
    @Mock
    private RequestVerificationBodyService requestVerificationBodyService;
    
    @Test
    void onAccountVerificationBodyAppointedEvent() {
        Long verificationBodyId = 1L;
        Long accountId = 1L;
        AccountVerificationBodyAppointedEvent event = 
                AccountVerificationBodyAppointedEvent.builder().accountId(accountId).verificationBodyId(verificationBodyId).build();
        
        listener.onAccountVerificationBodyAppointedEvent(event);
        
        verify(requestVerificationBodyService, times(1)).appointVerificationBodyToRequestsOfAccount(verificationBodyId, accountId);
    }
}
