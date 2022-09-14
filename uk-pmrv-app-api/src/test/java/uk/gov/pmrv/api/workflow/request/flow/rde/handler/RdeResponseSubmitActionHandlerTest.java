package uk.gov.pmrv.api.workflow.request.flow.rde.handler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.common.domain.model.PmrvUser;
import uk.gov.pmrv.api.workflow.request.WorkflowService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.permitissuance.common.domain.PermitIssuanceRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.rde.domain.RdeData;
import uk.gov.pmrv.api.workflow.request.flow.rde.domain.RdeDecisionPayload;
import uk.gov.pmrv.api.workflow.request.flow.rde.domain.RdeDecisionType;
import uk.gov.pmrv.api.workflow.request.flow.rde.domain.RdeOutcome;
import uk.gov.pmrv.api.workflow.request.flow.rde.domain.RdeResponseSubmitRequestTaskActionPayload;

@ExtendWith(MockitoExtension.class)
class RdeResponseSubmitActionHandlerTest {

    @InjectMocks
    private RdeResponseSubmitActionHandler handler;

    @Mock
    private RequestTaskService requestTaskService;

    @Mock
    private WorkflowService workflowService;


    @Test
    void process_whenDecisionAccepted_thenOutcomeAccepted() {

        final Long requestTaskId = 1L;
        final String requestId = "2";
        final PmrvUser pmrvUser = PmrvUser.builder().userId("userId").build();

        final RdeResponseSubmitRequestTaskActionPayload taskActionPayload =
            RdeResponseSubmitRequestTaskActionPayload.builder().rdeDecisionPayload(RdeDecisionPayload.builder()
                .decision(RdeDecisionType.ACCEPTED)
                .reason("reason")
                .build()).build();

        final PermitIssuanceRequestPayload requestPayload = PermitIssuanceRequestPayload.builder().rdeData(RdeData.builder().build()).build();

        final RequestTask requestTask = RequestTask.builder()
            .processTaskId("processTaskId")
            .request(Request.builder().payload(requestPayload).id(requestId).build())
            .build();

        when(requestTaskService.findTaskById(requestTaskId)).thenReturn(requestTask);

        handler.process(requestTaskId, RequestTaskActionType.RDE_RESPONSE_SUBMIT, pmrvUser, taskActionPayload);

        verify(requestTaskService, times(1)).findTaskById(requestTaskId);
        verify(workflowService, times(1)).completeTask("processTaskId",
            Map.of(BpmnProcessConstants.RDE_OUTCOME, RdeOutcome.ACCEPTED));

        assertThat(requestPayload.getRdeData().getRdeDecisionPayload().getDecision()).isEqualTo(RdeDecisionType.ACCEPTED);
        assertThat(requestPayload.getRdeData().getRdeDecisionPayload().getReason()).isEqualTo("reason");
    }

    @Test
    void process_whenDecisionRejected_thenOutcomeRejected() {

        final Long requestTaskId = 1L;
        final String requestId = "2";
        final PmrvUser pmrvUser = PmrvUser.builder().userId("userId").build();

        final RdeResponseSubmitRequestTaskActionPayload taskActionPayload =
            RdeResponseSubmitRequestTaskActionPayload.builder().rdeDecisionPayload(RdeDecisionPayload.builder()
                .decision(RdeDecisionType.REJECTED)
                .reason("reason")
                .build()).build();

        final PermitIssuanceRequestPayload requestPayload = PermitIssuanceRequestPayload.builder().rdeData(RdeData.builder().build()).build();

        final RequestTask requestTask = RequestTask.builder()
            .processTaskId("processTaskId")
            .request(Request.builder().payload(requestPayload).id(requestId).build())
            .build();

        when(requestTaskService.findTaskById(requestTaskId)).thenReturn(requestTask);

        handler.process(requestTaskId, RequestTaskActionType.RDE_RESPONSE_SUBMIT, pmrvUser, taskActionPayload);

        verify(requestTaskService, times(1)).findTaskById(requestTaskId);
        verify(workflowService, times(1)).completeTask("processTaskId",
            Map.of(BpmnProcessConstants.RDE_OUTCOME, RdeOutcome.REJECTED));

        assertThat(requestPayload.getRdeData().getRdeDecisionPayload().getDecision()).isEqualTo(RdeDecisionType.REJECTED);
        assertThat(requestPayload.getRdeData().getRdeDecisionPayload().getReason()).isEqualTo("reason");
    }

    @Test
    void getTypes() {
        assertThat(handler.getTypes()).containsExactly(RequestTaskActionType.RDE_RESPONSE_SUBMIT);
    }
}
