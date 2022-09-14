package uk.gov.pmrv.api.workflow.request.flow.notificationsystemmessage.handler;

import java.util.Set;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.core.service.InitializeRequestTaskHandler;
import uk.gov.pmrv.api.workflow.request.flow.notificationsystemmessage.domain.SystemMessageNotificationRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.notificationsystemmessage.domain.SystemMessageNotificationRequestTaskPayload;

@Service
public class SystemMessageNotificationInitializer implements InitializeRequestTaskHandler {

    @Override
    public RequestTaskPayload initializePayload(Request request) {
        SystemMessageNotificationRequestPayload requestPayload = (SystemMessageNotificationRequestPayload) request.getPayload();
        return SystemMessageNotificationRequestTaskPayload.builder()
            .payloadType(RequestTaskPayloadType.SYSTEM_MESSAGE_NOTIFICATION_PAYLOAD)
            .messagePayload(requestPayload.getMessagePayload())
            .build();
    }

    @Override
    public Set<RequestTaskType> getRequestTaskTypes() {
        return RequestTaskType.getSystemMessageNotificationTypes();
    }
}
