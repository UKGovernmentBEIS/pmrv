package uk.gov.pmrv.api.workflow.request.flow.permitsurrender.handler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.common.domain.model.PmrvUser;
import uk.gov.pmrv.api.workflow.request.WorkflowService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.DecisionNotification;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.NotifyOperatorForDecisionRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.permit.cessation.PermitCessationCompletedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.permit.cessation.PermitCessationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitsurrender.domain.PermitSurrenderRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitsurrender.service.RequestPermitSurrenderCessationService;

@ExtendWith(MockitoExtension.class)
class PermitSurrenderCessationNotifyOperatorActionHandlerTest {

    @InjectMocks
    private PermitSurrenderCessationNotifyOperatorActionHandler handler;

    @Mock
    private RequestTaskService requestTaskService;

    @Mock
    private WorkflowService workflowService;

    @Mock
    private RequestPermitSurrenderCessationService requestPermitSurrenderCessationService;

    @Test
    void process() {
        Long requestTaskId = 1L;
        Request request = Request.builder()
            .type(RequestType.PERMIT_SURRENDER)
            .payload(PermitSurrenderRequestPayload.builder()
                .payloadType(RequestPayloadType.PERMIT_SURRENDER_REQUEST_PAYLOAD)
                .regulatorReviewer("regulatorReviewer")
                .build())
            .build();
        NotifyOperatorForDecisionRequestTaskActionPayload taskActionPayload = NotifyOperatorForDecisionRequestTaskActionPayload.builder()
            .decisionNotification(DecisionNotification.builder().build())
            .build();
        PermitCessationSubmitRequestTaskPayload requestTaskPayload = PermitCessationSubmitRequestTaskPayload.builder()
            .payloadType(RequestTaskPayloadType.PERMIT_SURRENDER_CESSATION_SUBMIT_PAYLOAD)
            .build();
        RequestTask requestTask = RequestTask.builder()
            .id(requestTaskId)
            .payload(requestTaskPayload)
            .request(request)
            .processTaskId("processTaskId")
            .build();

        PermitCessationCompletedRequestActionPayload cessationCompletedRequestActionPayload =
            PermitCessationCompletedRequestActionPayload.builder()
                .payloadType(RequestActionPayloadType.PERMIT_SURRENDER_CESSATION_COMPLETED_PAYLOAD)
                .build();
        RequestTaskActionType requestTaskActionType = RequestTaskActionType.PERMIT_SURRENDER_CESSATION_NOTIFY_OPERATOR_FOR_DECISION;
        PmrvUser pmrvUser = PmrvUser.builder().userId("user").build();

        when(requestTaskService.findTaskById(requestTaskId)).thenReturn(requestTask);

        //invoke
        handler.process(requestTaskId, requestTaskActionType, pmrvUser, taskActionPayload);

        verify(requestTaskService, times(1)).findTaskById(requestTaskId);
        verify(requestPermitSurrenderCessationService, times(1))
            .executeNotifyOperatorActions(requestTask, pmrvUser, taskActionPayload);
        verify(workflowService, times(1))
            .completeTask(requestTask.getProcessTaskId());
    }

    @Test
    void getTypes() {
        assertThat(handler.getTypes()).containsExactly(RequestTaskActionType.PERMIT_SURRENDER_CESSATION_NOTIFY_OPERATOR_FOR_DECISION);
    }
}