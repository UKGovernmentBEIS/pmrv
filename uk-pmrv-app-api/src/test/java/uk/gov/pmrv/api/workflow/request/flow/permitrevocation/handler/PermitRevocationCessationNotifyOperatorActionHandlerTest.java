package uk.gov.pmrv.api.workflow.request.flow.permitrevocation.handler;

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
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.DecisionNotification;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.NotifyOperatorForDecisionRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.permit.cessation.PermitCessationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitrevocation.domain.PermitRevocationRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitrevocation.service.RequestPermitRevocationCessationService;

@ExtendWith(MockitoExtension.class)
class PermitRevocationCessationNotifyOperatorActionHandlerTest {

    @InjectMocks
    private PermitRevocationCessationNotifyOperatorActionHandler handler;

    @Mock
    private RequestTaskService requestTaskService;

    @Mock
    private WorkflowService workflowService;

    @Mock
    private RequestPermitRevocationCessationService cessationService;

    @Test
    void process() {
        
        final Long requestTaskId = 1L;
        final Request request = Request.builder()
            .type(RequestType.PERMIT_REVOCATION)
            .payload(PermitRevocationRequestPayload.builder()
                .payloadType(RequestPayloadType.PERMIT_REVOCATION_REQUEST_PAYLOAD)
                .regulatorAssignee("regulatorAssignee")
                .build())
            .build();
        final NotifyOperatorForDecisionRequestTaskActionPayload taskActionPayload = NotifyOperatorForDecisionRequestTaskActionPayload.builder()
            .decisionNotification(DecisionNotification.builder().build())
            .build();
        final PermitCessationSubmitRequestTaskPayload requestTaskPayload = PermitCessationSubmitRequestTaskPayload.builder()
            .payloadType(RequestTaskPayloadType.PERMIT_REVOCATION_CESSATION_SUBMIT_PAYLOAD)
            .build();
        final RequestTask requestTask = RequestTask.builder()
            .id(requestTaskId)
            .payload(requestTaskPayload)
            .request(request)
            .processTaskId("processTaskId")
            .build();
        
        final RequestTaskActionType requestTaskActionType = RequestTaskActionType.PERMIT_REVOCATION_NOTIFY_OPERATOR_FOR_CESSATION;
        final PmrvUser pmrvUser = PmrvUser.builder().userId("user").build();

        when(requestTaskService.findTaskById(requestTaskId)).thenReturn(requestTask);

        //invoke
        handler.process(requestTaskId, requestTaskActionType, pmrvUser, taskActionPayload);

        verify(requestTaskService, times(1)).findTaskById(requestTaskId);
        verify(cessationService, times(1))
            .executeNotifyOperatorActions(requestTask, pmrvUser, taskActionPayload);
        verify(workflowService, times(1)).completeTask(requestTask.getProcessTaskId());
    }

    @Test
    void getTypes() {
        assertThat(handler.getTypes()).containsExactly(RequestTaskActionType.PERMIT_REVOCATION_NOTIFY_OPERATOR_FOR_CESSATION);
    }
}