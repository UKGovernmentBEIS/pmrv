package uk.gov.pmrv.api.workflow.request.application.item.listener;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.workflow.request.application.item.service.RequestTaskVisitService;
import uk.gov.pmrv.api.workflow.request.application.taskdeleted.RequestTaskDeletedEvent;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestTaskAttachmentsUncoupleService;


@ExtendWith(MockitoExtension.class)
class RequestTaskDeletedEventListenerTest {

    @InjectMocks
    private RequestTaskDeletedEventListener listener;

    @Mock
    private RequestTaskVisitService requestTaskVisitService;

    @Mock
    private RequestTaskAttachmentsUncoupleService requestTaskAttachmentsUncoupleService;

    @Test
    void onRequestTaskDeletedEvent() {
        
        final RequestTaskDeletedEvent event = RequestTaskDeletedEvent.builder().requestTaskId(1L).build();
        listener.onRequestTaskDeletedEvent(event);
        verify(requestTaskVisitService, times(1)).deleteByTaskId(event.getRequestTaskId());
        verify(requestTaskAttachmentsUncoupleService, times(1)).deletePendingAttachments(1L);
    }
}
