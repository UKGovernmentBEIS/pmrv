package uk.gov.pmrv.api.workflow.request.flow.permitvariation.handler;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import uk.gov.pmrv.api.common.domain.model.PmrvUser;
import uk.gov.pmrv.api.workflow.request.WorkflowService;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.common.actionhandler.RequestTaskActionHandler;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.RequestTaskActionEmptyPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitvariation.domain.PermitVariationSubmitOutcome;
import uk.gov.pmrv.api.workflow.request.flow.permitvariation.service.PermitVariationOperatorSubmitService;

@RequiredArgsConstructor
@Component
public class PermitVariationOperatorSubmitActionHandler implements RequestTaskActionHandler<RequestTaskActionEmptyPayload> {
	
	private final RequestTaskService requestTaskService;
	private final PermitVariationOperatorSubmitService permitVariationOperatorSubmitService;
	private final WorkflowService workflowService;

	@Override
	public void process(Long requestTaskId, RequestTaskActionType requestTaskActionType, PmrvUser pmrvUser,
			RequestTaskActionEmptyPayload payload) {
		final RequestTask requestTask = requestTaskService.findTaskById(requestTaskId);
		
		//submit permit variation
		permitVariationOperatorSubmitService.submitPermitVariation(requestTask, pmrvUser);
		
		//set request's submission date
        requestTask.getRequest().setSubmissionDate(LocalDateTime.now());
		
		//complete task
        workflowService.completeTask(requestTask.getProcessTaskId(), 
        		Map.of(BpmnProcessConstants.PERMIT_VARIATION_SUBMIT_OUTCOME, PermitVariationSubmitOutcome.SUBMITTED));
	}

	@Override
	public List<RequestTaskActionType> getTypes() {
		return List.of(RequestTaskActionType.PERMIT_VARIATION_OPERATOR_SUBMIT_APPLICATION);
	}

}
