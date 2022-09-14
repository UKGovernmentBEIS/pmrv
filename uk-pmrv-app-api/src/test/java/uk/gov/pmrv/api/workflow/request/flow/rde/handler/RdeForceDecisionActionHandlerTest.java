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
import uk.gov.pmrv.api.workflow.request.flow.rde.domain.RdeDecisionType;
import uk.gov.pmrv.api.workflow.request.flow.rde.domain.RdeForceDecisionPayload;
import uk.gov.pmrv.api.workflow.request.flow.rde.domain.RdeForceDecisionRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.rde.domain.RdeForceDecisionRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.rde.domain.RdeOutcome;

@ExtendWith(MockitoExtension.class)
class RdeForceDecisionActionHandlerTest {

    @InjectMocks
    private RdeForceDecisionActionHandler handler;

    @Mock
    private RequestTaskService requestTaskService;

    @Mock
    private WorkflowService workflowService;


    @Test
    void process() {

        final Long requestTaskId = 1L;
        final String requestId = "1";
        final PmrvUser pmrvUser = PmrvUser.builder().userId("userId").build();

        final RdeForceDecisionRequestTaskActionPayload taskActionPayload =
            RdeForceDecisionRequestTaskActionPayload.builder().rdeForceDecisionPayload(
                    RdeForceDecisionPayload.builder()
                        .decision(RdeDecisionType.REJECTED)
                        .evidence("evidence")
                        .build())
                .build();

        final PermitIssuanceRequestPayload requestPayload = PermitIssuanceRequestPayload.builder().rdeData(RdeData.builder().build()).build();

        final RequestTask requestTask = RequestTask.builder()
            .processTaskId("processTaskId")
            .payload(RdeForceDecisionRequestTaskPayload.builder().build())
            .request(Request.builder().payload(requestPayload).id(requestId).build())
            .build();

        when(requestTaskService.findTaskById(requestTaskId)).thenReturn(requestTask);

        handler.process(requestTaskId, RequestTaskActionType.RDE_FORCE_DECISION, pmrvUser, taskActionPayload);

        verify(requestTaskService, times(1)).findTaskById(requestTaskId);
        verify(workflowService, times(1)).completeTask("processTaskId",
            Map.of(BpmnProcessConstants.RDE_OUTCOME, RdeOutcome.FORCE_REJECTED));

        assertThat(requestPayload.getRdeData().getRdeForceDecisionPayload().getDecision()).isEqualTo(RdeDecisionType.REJECTED);
        assertThat(requestPayload.getRdeData().getRdeForceDecisionPayload().getEvidence()).isEqualTo("evidence");
    }

    @Test
    void getTypes() {
        assertThat(handler.getTypes()).containsExactly(RequestTaskActionType.RDE_FORCE_DECISION);
    }
}
