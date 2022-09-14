package uk.gov.pmrv.api.workflow.request.flow.permitnotification.service;

import lombok.RequiredArgsConstructor;

import org.mapstruct.factory.Mappers;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uk.gov.pmrv.api.common.domain.model.PmrvUser;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.permitnotification.domain.PermitNotificationApplicationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitnotification.domain.PermitNotificationApplicationSubmittedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitnotification.domain.PermitNotificationContainer;
import uk.gov.pmrv.api.workflow.request.flow.permitnotification.domain.PermitNotificationRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitnotification.domain.PermitNotificationSaveApplicationRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitnotification.mapper.PermitNotificationMapper;

@Service
@RequiredArgsConstructor
public class RequestPermitNotificationService {

    private final RequestService requestService;
    private final RequestTaskService requestTaskService;
    private final PermitNotificationSubmitValidatorService permitNotificationSubmitValidatorService;
    private static final PermitNotificationMapper permitNotificationMapper = Mappers.getMapper(PermitNotificationMapper.class);

    @Transactional
    public void applySavePayload(PermitNotificationSaveApplicationRequestTaskActionPayload actionPayload,
                                 RequestTask requestTask) {
        PermitNotificationApplicationSubmitRequestTaskPayload taskPayload = (PermitNotificationApplicationSubmitRequestTaskPayload) requestTask.getPayload();

        if(taskPayload == null) {
            taskPayload = PermitNotificationApplicationSubmitRequestTaskPayload.builder()
                    .payloadType(RequestTaskPayloadType.PERMIT_NOTIFICATION_APPLICATION_SUBMIT_PAYLOAD).build();
            requestTask.setPayload(taskPayload);
        }

        taskPayload.setPermitNotification(actionPayload.getPermitNotification());
        taskPayload.setSectionsCompleted(actionPayload.getSectionsCompleted());

        requestTaskService.saveRequestTask(requestTask);
    }

    @Transactional
    public void applySubmitPayload(RequestTask requestTask, PmrvUser authUser) {
        Request request = requestTask.getRequest();
        PermitNotificationApplicationSubmitRequestTaskPayload
                taskPayload = (PermitNotificationApplicationSubmitRequestTaskPayload) requestTask.getPayload();

        PermitNotificationContainer permitNotificationContainer = permitNotificationMapper.toPermitNotificationContainer(taskPayload);

        // Validate permit notification
        permitNotificationSubmitValidatorService.validatePermitNotification(permitNotificationContainer);

        // Update request payload
        PermitNotificationRequestPayload requestPayload = (PermitNotificationRequestPayload) request.getPayload();
        requestPayload.setPermitNotification(taskPayload.getPermitNotification());
        requestPayload.setPermitNotificationAttachments(taskPayload.getPermitNotificationAttachments());
        requestService.saveRequest(request);

        // Add action
        PermitNotificationApplicationSubmittedRequestActionPayload applicationSubmittedActionPayload = permitNotificationMapper.toApplicationSubmittedRequestActionPayload(taskPayload);
        applicationSubmittedActionPayload.setPermitNotificationAttachments(requestPayload.getPermitNotificationAttachments());
        requestService.addActionToRequest(request, applicationSubmittedActionPayload, RequestActionType.PERMIT_NOTIFICATION_APPLICATION_SUBMITTED, authUser.getUserId());
    }
}
