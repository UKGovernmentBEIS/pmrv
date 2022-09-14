package uk.gov.pmrv.api.workflow.request.application.item.listener;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import uk.gov.pmrv.api.workflow.request.application.item.service.RequestTaskVisitService;
import uk.gov.pmrv.api.workflow.request.application.taskcompleted.RequestTaskCompletedEvent;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestTaskAttachmentsUncoupleService;

@RequiredArgsConstructor
@Component
public class RequestTaskCompletedEventListener {

    private final RequestTaskVisitService requestTaskVisitService;
    private final RequestTaskAttachmentsUncoupleService requestTaskAttachmentsUncoupleService;
    
    @EventListener
    public void onRequestTaskCompletedEvent(RequestTaskCompletedEvent event) {
        
        final Long requestTaskId = event.getRequestTaskId();
        requestTaskVisitService.deleteByTaskId(requestTaskId);
        requestTaskAttachmentsUncoupleService.uncoupleAttachments(requestTaskId);
    }
}
