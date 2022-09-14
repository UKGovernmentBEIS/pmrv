package uk.gov.pmrv.api.workflow.request.flow.permitrevocation.handler;

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
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.DecisionNotification;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.NotifyOperatorForDecisionRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitrevocation.domain.PermitRevocationRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitrevocation.domain.PermitRevocationWaitForAppealRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitrevocation.validation.PermitRevocationValidator;

@ExtendWith(MockitoExtension.class)
class PermitRevocationNotifyOperatorForWithdrawalActionHandlerTest {

    @InjectMocks
    private PermitRevocationNotifyOperatorForWithdrawalActionHandler handler;

    @Mock
    private RequestTaskService requestTaskService;
    
    @Mock
    private PermitRevocationValidator validator;

    @Mock
    private WorkflowService workflowService;

    @Test
    void process() {

        final DecisionNotification decisionNotification = DecisionNotification.builder()
            .operators(Set.of("operator"))
            .signatory("regulator")
            .build();
        
        final NotifyOperatorForDecisionRequestTaskActionPayload taskActionPayload =
            NotifyOperatorForDecisionRequestTaskActionPayload.builder()
                .payloadType(RequestTaskActionPayloadType.PERMIT_REVOCATION_NOTIFY_OPERATOR_FOR_WITHDRAWAL_PAYLOAD)
                .decisionNotification(decisionNotification)
                .build();

        final PmrvUser pmrvUser = PmrvUser.builder().build();
        final String processTaskId = "processTaskId";
        final UUID file = UUID.randomUUID();
        final PermitRevocationWaitForAppealRequestTaskPayload taskPayload =
            PermitRevocationWaitForAppealRequestTaskPayload.builder()
                .reason("the reason")
                .withdrawFiles(Set.of(file))
                .revocationAttachments(Map.of(file, "file"))
                .build();
        final RequestTask requestTask = RequestTask.builder()
            .id(1L)
            .processTaskId(processTaskId)
            .payload(taskPayload)
            .request(Request.builder().payload(PermitRevocationRequestPayload.builder().build()).build())
            .build();

        when(requestTaskService.findTaskById(1L)).thenReturn(requestTask);

        //invoke
        handler.process(requestTask.getId(),
            RequestTaskActionType.PERMIT_REVOCATION_NOTIFY_OPERATOR_FOR_WITHDRAWAL,
            pmrvUser,
            taskActionPayload);
            
        verify(requestTaskService, times(1)).findTaskById(requestTask.getId());
        verify(validator, times(1)).validateNotifyUsers(requestTask, decisionNotification, pmrvUser);
        verify(validator, times(1)).validateWaitForAppealRequestTaskPayload(taskPayload);
        verify(workflowService, times(1)).completeTask(processTaskId);
        
        final PermitRevocationRequestPayload requestPayload = (PermitRevocationRequestPayload) requestTask.getRequest().getPayload();
        assertThat(requestPayload.getWithdrawDecisionNotification()).isEqualTo(decisionNotification);
        assertThat(requestPayload.getWithdrawReason()).isEqualTo("the reason");
        assertThat(requestPayload.getWithdrawFiles()).isEqualTo(Set.of(file));
        assertThat(requestPayload.getRevocationAttachments()).isEqualTo(Map.of(file, "file"));
    }

    @Test
    void getTypes() {
        assertThat(handler.getTypes()).containsExactly(RequestTaskActionType.PERMIT_REVOCATION_NOTIFY_OPERATOR_FOR_WITHDRAWAL);
    }
}