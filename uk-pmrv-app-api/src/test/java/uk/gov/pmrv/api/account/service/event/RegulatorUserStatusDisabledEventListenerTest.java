package uk.gov.pmrv.api.account.service.event;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.account.service.AccountCaSiteContactService;
import uk.gov.pmrv.api.authorization.regulator.event.RegulatorUserStatusDisabledEvent;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class RegulatorUserStatusDisabledEventListenerTest {

    @InjectMocks
    private RegulatorUserStatusDisabledEventListener listener;
    
    @Mock
    private AccountCaSiteContactService accountCaSiteContactService;
    
    @Test
    void onRegulatorUserStatusDisabledEvent() {
        String userId = "user";
        RegulatorUserStatusDisabledEvent event = new RegulatorUserStatusDisabledEvent(userId);
        
        listener.onRegulatorUserStatusDisabledEvent(event);
        
        verify(accountCaSiteContactService, times(1)).removeUserFromCaSiteContact(userId);
    }
}
