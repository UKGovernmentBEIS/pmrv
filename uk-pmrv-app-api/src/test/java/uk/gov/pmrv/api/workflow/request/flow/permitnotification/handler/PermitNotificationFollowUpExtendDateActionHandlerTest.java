package uk.gov.pmrv.api.workflow.request.flow.permitnotification.handler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.common.domain.model.PmrvUser;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.permitnotification.domain.PermitNotificationFollowUpExtendDateRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitnotification.service.PermitNotificationSendEventService;

@ExtendWith(MockitoExtension.class)
class PermitNotificationFollowUpExtendDateActionHandlerTest {

    @InjectMocks
    private PermitNotificationFollowUpExtendDateActionHandler handler;

    @Mock
    private RequestTaskService requestTaskService;

    @Mock
    private PermitNotificationSendEventService eventService;

    @Test
    void process() {

        final LocalDate dueDate = LocalDate.of(2023, 1, 1);
        final PermitNotificationFollowUpExtendDateRequestTaskActionPayload taskActionPayload =
            PermitNotificationFollowUpExtendDateRequestTaskActionPayload.builder()
                .payloadType(RequestTaskActionPayloadType.PERMIT_NOTIFICATION_FOLLOW_UP_EXTEND_DATE_PAYLOAD)
                .dueDate(dueDate)
                .build();
        final PmrvUser pmrvUser = PmrvUser.builder().build();
        final RequestTask requestTask =
            RequestTask.builder()
                .id(1L)
                .dueDate(LocalDate.of(2022, 12, 1))
                .request(Request.builder().id("requestId").build())
                .build();

        when(requestTaskService.findTaskById(1L)).thenReturn(requestTask);

        // Invoke
        handler.process(requestTask.getId(), RequestTaskActionType.PERMIT_NOTIFICATION_FOLLOW_UP_EXTEND_DATE, pmrvUser,
            taskActionPayload);

        // Verify
        verify(requestTaskService, times(1)).findTaskById(requestTask.getId());
        verify(eventService, times(1)).extendTimer("requestId", dueDate);
    }

    @Test
    void getTypes() {
        assertThat(handler.getTypes()).containsExactly(RequestTaskActionType.PERMIT_NOTIFICATION_FOLLOW_UP_EXTEND_DATE);
    }
}
