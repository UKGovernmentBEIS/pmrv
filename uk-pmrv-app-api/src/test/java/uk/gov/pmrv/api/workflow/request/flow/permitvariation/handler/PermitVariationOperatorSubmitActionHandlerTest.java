package uk.gov.pmrv.api.workflow.request.flow.permitvariation.handler;

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
import uk.gov.pmrv.api.workflow.request.flow.common.domain.RequestTaskActionEmptyPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitvariation.domain.PermitVariationSubmitOutcome;
import uk.gov.pmrv.api.workflow.request.flow.permitvariation.service.PermitVariationOperatorSubmitService;

@ExtendWith(MockitoExtension.class)
class PermitVariationOperatorSubmitActionHandlerTest {

	@InjectMocks
    private PermitVariationOperatorSubmitActionHandler handler;
	
	@Mock
    private RequestTaskService requestTaskService;
	
	@Mock
    private PermitVariationOperatorSubmitService permitVariationOperatorSubmitService;
	
	@Mock
    private WorkflowService workflowService;
	
	@Test
	void process() {
		Long requestTaskId = 1L;
		RequestTaskActionType requestTaskActionType = RequestTaskActionType.PERMIT_VARIATION_OPERATOR_SUBMIT_APPLICATION;
		PmrvUser pmrvUser = PmrvUser.builder().userId("user").build();
		RequestTaskActionEmptyPayload payload = RequestTaskActionEmptyPayload.builder().build();
		
		String processTaskId = "processTaskId";
		Request request = Request.builder().id("1").build();
		RequestTask requestTask = RequestTask.builder().id(1L).request(request).processTaskId(processTaskId).build();
		
		when(requestTaskService.findTaskById(1L)).thenReturn(requestTask);
		
		handler.process(requestTaskId, requestTaskActionType, pmrvUser, payload);
		
		assertThat(request.getSubmissionDate()).isNotNull();
        verify(requestTaskService, times(1)).findTaskById(requestTask.getId());
        verify(permitVariationOperatorSubmitService, times(1)).submitPermitVariation(requestTask, pmrvUser);
        verify(workflowService, times(1)).completeTask(processTaskId,
            Map.of(BpmnProcessConstants.PERMIT_VARIATION_SUBMIT_OUTCOME, PermitVariationSubmitOutcome.SUBMITTED));
	}
	
	@Test
	void getTypes() {
		assertThat(handler.getTypes())
				.containsExactlyInAnyOrder(RequestTaskActionType.PERMIT_VARIATION_OPERATOR_SUBMIT_APPLICATION);
	}
}
