package uk.gov.pmrv.api.workflow.request.flow.rfi.handler;

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
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.RequestTaskActionEmptyPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitissuance.common.domain.PermitIssuanceRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.rfi.domain.RfiOutcome;

@ExtendWith(MockitoExtension.class)
class RfiCancelActionHandlerTest {

    @InjectMocks
    private RfiCancelActionHandler handler;

    @Mock
    private RequestTaskService requestTaskService;

    @Mock
    private WorkflowService workflowService;


    @Test
    void process() {

        final RequestTaskActionEmptyPayload actionPayload =
            RequestTaskActionEmptyPayload.builder().build();
        final PmrvUser pmrvUser = PmrvUser.builder().userId("userId").build();
        
        final Long requestTaskId = 1L;
        final String processTaskId = "processTaskId";
        final String requestId = "2";
        final RequestTask requestTask = RequestTask.builder()
            .id(requestTaskId)
            .request(Request.builder().payload(PermitIssuanceRequestPayload.builder().build()).id(requestId).build())
            .type(RequestTaskType.PERMIT_ISSUANCE_WAIT_FOR_RFI_RESPONSE)
            .processTaskId(processTaskId)
            .build();

        when(requestTaskService.findTaskById(requestTaskId)).thenReturn(requestTask);

        handler.process(
            requestTaskId,
            RequestTaskActionType.RFI_CANCEL,
            pmrvUser,
            actionPayload);

        verify(requestTaskService, times(1)).findTaskById(requestTaskId);
        verify(workflowService, times(1))
            .completeTask(processTaskId,
                Map.of(BpmnProcessConstants.RFI_OUTCOME, RfiOutcome.CANCELLED)
            );
    }

    @Test
    void getTypes() {
        assertThat(handler.getTypes()).containsExactly(RequestTaskActionType.RFI_CANCEL);
    }
}
