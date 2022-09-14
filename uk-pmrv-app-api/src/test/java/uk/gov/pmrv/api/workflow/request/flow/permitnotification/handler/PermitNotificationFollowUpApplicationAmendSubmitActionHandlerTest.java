package uk.gov.pmrv.api.workflow.request.flow.permitnotification.handler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Map;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.common.domain.model.PmrvUser;
import uk.gov.pmrv.api.workflow.request.WorkflowService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.permitnotification.domain.PermitNotificationFollowUpApplicationAmendsSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitnotification.domain.PermitNotificationFollowUpSubmitApplicationAmendRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitnotification.domain.PermitNotificationRequestPayload;

@ExtendWith(MockitoExtension.class)
class PermitNotificationFollowUpApplicationAmendSubmitActionHandlerTest {

    @InjectMocks
    private PermitNotificationFollowUpApplicationAmendSubmitActionHandler handler;

    @Mock
    private RequestTaskService requestTaskService;

    @Mock
    private RequestService requestService;

    @Mock
    private WorkflowService workflowService;

    @Test
    void process() {

        final PermitNotificationFollowUpSubmitApplicationAmendRequestTaskActionPayload taskActionPayload =
            PermitNotificationFollowUpSubmitApplicationAmendRequestTaskActionPayload.builder()
                .payloadType(
                    RequestTaskActionPayloadType.PERMIT_NOTIFICATION_FOLLOW_UP_SUBMIT_APPLICATION_AMEND_PAYLOAD)
                .followUpSectionsCompleted(Map.of("section1", false))
                .build();
        final PmrvUser pmrvUser = PmrvUser.builder().userId("userId").build();
        final String processTaskId = "processTaskId";
        final UUID file = UUID.randomUUID();
        final PermitNotificationFollowUpApplicationAmendsSubmitRequestTaskPayload taskPayload =
            PermitNotificationFollowUpApplicationAmendsSubmitRequestTaskPayload.builder()
                .followUpResponse("the response")
                .followUpFiles(Set.of(file))
                .followUpAttachments(Map.of(file, "filename"))
                .reviewSectionsCompleted(Map.of("section1", true))
                .followUpSectionsCompleted(Map.of("section1", true))
                .build();
        final Request request =
            Request.builder().id("requestId").payload(PermitNotificationRequestPayload.builder().build()).build();
        final RequestTask requestTask = RequestTask.builder()
            .id(1L)
            .processTaskId(processTaskId)
            .payload(taskPayload)
            .request(request)
            .build();

        when(requestTaskService.findTaskById(1L)).thenReturn(requestTask);

        // Invoke
        handler.process(requestTask.getId(),
            RequestTaskActionType.PERMIT_NOTIFICATION_FOLLOW_UP_NOTIFY_OPERATOR_FOR_DECISION,
            pmrvUser,
            taskActionPayload);

        // Verify
        verify(requestTaskService, times(1)).findTaskById(requestTask.getId());
        verify(workflowService, times(1)).completeTask(processTaskId);
        verify(requestService, times(1)).addActionToRequest(request,
            null,
            RequestActionType.PERMIT_NOTIFICATION_FOLLOW_UP_APPLICATION_AMENDS_SUBMITTED,
            "userId");

        final PermitNotificationRequestPayload requestPayload =
            (PermitNotificationRequestPayload) requestTask.getRequest().getPayload();
        assertThat(requestPayload.getFollowUpResponse()).isEqualTo("the response");
        assertThat(requestPayload.getFollowUpResponseFiles()).isEqualTo(Set.of(file));
        assertThat(requestPayload.getFollowUpResponseAttachments()).isEqualTo(Map.of(file, "filename"));
        assertThat(requestPayload.getFollowUpReviewSectionsCompleted()).isEqualTo(Map.of("section1", true));
        assertThat(requestPayload.getFollowUpSectionsCompleted()).isEqualTo(Map.of("section1", false));
    }

    @Test
    void getTypes() {
        assertThat(handler.getTypes()).containsExactly(
            RequestTaskActionType.PERMIT_NOTIFICATION_FOLLOW_UP_SUBMIT_APPLICATION_AMEND);
    }
}
