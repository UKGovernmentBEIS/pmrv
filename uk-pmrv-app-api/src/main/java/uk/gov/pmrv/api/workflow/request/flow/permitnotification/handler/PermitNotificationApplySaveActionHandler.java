package uk.gov.pmrv.api.workflow.request.flow.permitnotification.handler;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;

import uk.gov.pmrv.api.common.domain.model.PmrvUser;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.common.actionhandler.RequestTaskActionHandler;
import uk.gov.pmrv.api.workflow.request.flow.permitnotification.domain.PermitNotificationSaveApplicationRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitnotification.service.RequestPermitNotificationService;

import java.util.List;

@RequiredArgsConstructor
@Component
public class PermitNotificationApplySaveActionHandler implements RequestTaskActionHandler<PermitNotificationSaveApplicationRequestTaskActionPayload> {

    private final RequestTaskService requestTaskService;
    private final RequestPermitNotificationService requestPermitNotificationService;

    @Override
    public void process(Long requestTaskId, RequestTaskActionType requestTaskActionType, PmrvUser pmrvUser,
                        PermitNotificationSaveApplicationRequestTaskActionPayload actionPayload) {
        final RequestTask requestTask = requestTaskService.findTaskById(requestTaskId);
        requestPermitNotificationService.applySavePayload(actionPayload, requestTask);
    }

    @Override
    public List<RequestTaskActionType> getTypes() {
        return List.of(RequestTaskActionType.PERMIT_NOTIFICATION_SAVE_APPLICATION);
    }
}
