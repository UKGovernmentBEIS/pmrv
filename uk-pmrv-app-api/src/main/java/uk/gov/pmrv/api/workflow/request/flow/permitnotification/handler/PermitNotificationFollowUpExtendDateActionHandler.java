package uk.gov.pmrv.api.workflow.request.flow.permitnotification.handler;

import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.pmrv.api.common.domain.model.PmrvUser;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.common.actionhandler.RequestTaskActionHandler;
import uk.gov.pmrv.api.workflow.request.flow.permitnotification.domain.PermitNotificationFollowUpExtendDateRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitnotification.service.PermitNotificationSendEventService;

@RequiredArgsConstructor
@Component
public class PermitNotificationFollowUpExtendDateActionHandler 
    implements RequestTaskActionHandler<PermitNotificationFollowUpExtendDateRequestTaskActionPayload> {

    private final RequestTaskService requestTaskService;
    private final PermitNotificationSendEventService eventService;

    @Override
    public void process(final Long requestTaskId, 
                        final RequestTaskActionType requestTaskActionType, 
                        final PmrvUser pmrvUser,
                        final PermitNotificationFollowUpExtendDateRequestTaskActionPayload actionPayload) {

        final RequestTask requestTask = requestTaskService.findTaskById(requestTaskId);
        final LocalDate previousDueDate = requestTask.getDueDate();
        final LocalDate dueDate = actionPayload.getDueDate();
        
        // validate
        if (!dueDate.isAfter(previousDueDate)) {
            throw new BusinessException(ErrorCode.FORM_VALIDATION);
        }
        
        // send event
        final String requestId = requestTask.getRequest().getId();
        eventService.extendTimer(requestId, dueDate);        
    }

    @Override
    public List<RequestTaskActionType> getTypes() {
        return List.of(RequestTaskActionType.PERMIT_NOTIFICATION_FOLLOW_UP_EXTEND_DATE);
    }
}
