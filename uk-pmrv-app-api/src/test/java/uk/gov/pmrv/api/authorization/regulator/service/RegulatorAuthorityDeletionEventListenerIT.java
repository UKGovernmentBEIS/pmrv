package uk.gov.pmrv.api.authorization.regulator.service;

import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationEventPublisher;
import org.testcontainers.junit.jupiter.Testcontainers;

import uk.gov.pmrv.api.AbstractContainerBaseTest;
import uk.gov.pmrv.api.authorization.regulator.event.RegulatorAuthorityDeletionEvent;
import uk.gov.pmrv.api.workflow.request.application.userdeleted.RegulatorAuthorityDeletionEventListener;

import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.times;

@Testcontainers
@SpringBootTest
class RegulatorAuthorityDeletionEventListenerIT extends AbstractContainerBaseTest {

    @Autowired 
    private ApplicationEventPublisher eventPublisher;
    
    @MockBean
    private uk.gov.pmrv.api.account.service.event.RegulatorAuthorityDeletionEventListener accountRegulatorAuthorityDeletionEventListener;
    
    @MockBean
    private RegulatorAuthorityDeletionEventListener workflowRegulatorAuthorityDeletionEventListener;

    @MockBean
    private uk.gov.pmrv.api.user.regulator.service.RegulatorAuthorityDeletionEventListener regulatorAuthorityDeletionEventListener; 
    
    @Test
    void listenersInvoked() {
        String userId = "user";
        
        //invoke
        final RegulatorAuthorityDeletionEvent event = RegulatorAuthorityDeletionEvent.builder().userId(userId).build();
        eventPublisher.publishEvent(event);
        
        //verify
        InOrder inOrder = inOrder(accountRegulatorAuthorityDeletionEventListener, 
                                  workflowRegulatorAuthorityDeletionEventListener, 
                                  regulatorAuthorityDeletionEventListener);
        
        inOrder.verify(accountRegulatorAuthorityDeletionEventListener, times(1)).onRegulatorUserDeletedEvent(event);
        inOrder.verify(workflowRegulatorAuthorityDeletionEventListener, times(1)).onRegulatorUserDeletedEvent(event);
        inOrder.verify(regulatorAuthorityDeletionEventListener, times(1)).onRegulatorAuthorityDeletedEvent(event);
    }
}
