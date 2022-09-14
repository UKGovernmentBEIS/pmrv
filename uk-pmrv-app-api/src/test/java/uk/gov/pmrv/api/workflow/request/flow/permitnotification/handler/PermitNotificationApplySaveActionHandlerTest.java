package uk.gov.pmrv.api.workflow.request.flow.permitnotification.handler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.pmrv.api.common.domain.model.PmrvUser;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.permitnotification.domain.NonSignificantChange;
import uk.gov.pmrv.api.workflow.request.flow.permitnotification.domain.PermitNotificationSaveApplicationRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitnotification.domain.PermitNotificationType;
import uk.gov.pmrv.api.workflow.request.flow.permitnotification.service.RequestPermitNotificationService;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PermitNotificationApplySaveActionHandlerTest {

    @InjectMocks
    private PermitNotificationApplySaveActionHandler handler;

    @Mock
    private RequestPermitNotificationService service;

    @Mock
    private RequestTaskService requestTaskService;

    @Test
    void process() {
        PermitNotificationSaveApplicationRequestTaskActionPayload actionPayload =
                PermitNotificationSaveApplicationRequestTaskActionPayload.builder()
                        .payloadType(RequestTaskActionPayloadType.PERMIT_NOTIFICATION_SAVE_APPLICATION_PAYLOAD)
                        .permitNotification(NonSignificantChange.builder().type(PermitNotificationType.NON_SIGNIFICANT_CHANGE).build())
                        .build();

        RequestTask requestTask = RequestTask.builder().id(1L).build();
        PmrvUser pmrvUser = PmrvUser.builder().build();

        when(requestTaskService.findTaskById(1L)).thenReturn(requestTask);

        // Invoke
        handler.process(requestTask.getId(), RequestTaskActionType.PERMIT_NOTIFICATION_SAVE_APPLICATION, pmrvUser, actionPayload);

        // Verify
        verify(service, times(1)).applySavePayload(actionPayload, requestTask);
    }

    @Test
    void getTypes() {
        assertThat(handler.getTypes()).isEqualTo(List.of(RequestTaskActionType.PERMIT_NOTIFICATION_SAVE_APPLICATION));
    }
}
