package uk.gov.pmrv.api.workflow.request.flow.notificationsystemmessage.handler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.flow.notificationsystemmessage.domain.SystemMessageNotificationPayload;
import uk.gov.pmrv.api.workflow.request.flow.notificationsystemmessage.domain.SystemMessageNotificationRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.notificationsystemmessage.domain.SystemMessageNotificationRequestTaskPayload;

class SystemMessageNotificationInitializerTest {

    private final SystemMessageNotificationInitializer initializer = new SystemMessageNotificationInitializer();

    @Test
    void initializePayload() {
        SystemMessageNotificationPayload systemMessageNotificationPayload = SystemMessageNotificationPayload.builder()
            .text("text")
            .subject("subject")
            .build();

        SystemMessageNotificationRequestPayload requestPayload = SystemMessageNotificationRequestPayload.builder()
            .payloadType(RequestPayloadType.SYSTEM_MESSAGE_NOTIFICATION_REQUEST_PAYLOAD)
            .messagePayload(systemMessageNotificationPayload)
            .build();

        Request request = Request.builder().payload(requestPayload).build();

        RequestTaskPayload requestTaskPayload = initializer.initializePayload(request);

        assertEquals(RequestTaskPayloadType.SYSTEM_MESSAGE_NOTIFICATION_PAYLOAD, requestTaskPayload.getPayloadType());
        assertThat(requestTaskPayload).isInstanceOf(SystemMessageNotificationRequestTaskPayload.class);
        assertEquals(systemMessageNotificationPayload, ((SystemMessageNotificationRequestTaskPayload) requestTaskPayload).getMessagePayload());
    }
}