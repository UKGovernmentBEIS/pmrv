package uk.gov.pmrv.api.workflow.request.flow.permitsurrender.handler;

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
import uk.gov.pmrv.api.workflow.request.flow.permitsurrender.domain.PermitSurrenderOutcome;
import uk.gov.pmrv.api.workflow.request.flow.permitsurrender.service.RequestPermitSurrenderService;

@RequiredArgsConstructor
@Component
public class PermitSurrenderApplySubmitActionHandler implements RequestTaskActionHandler<RequestTaskActionEmptyPayload>{
    
    private final RequestPermitSurrenderService requestPermitSurrenderService;
    private final WorkflowService workflowService;
    private final RequestTaskService requestTaskService;

    @Override
    public void process(final Long requestTaskId,
                        final RequestTaskActionType requestTaskActionType,
                        final PmrvUser pmrvUser, 
                        final RequestTaskActionEmptyPayload payload) {
        final RequestTask requestTask = requestTaskService.findTaskById(requestTaskId);

        //submit permit surrender
        requestPermitSurrenderService.applySubmitPayload(requestTask, pmrvUser);
        
        //set request's submission date
        requestTask.getRequest().setSubmissionDate(LocalDateTime.now());

        //complete task
        workflowService.completeTask(requestTask.getProcessTaskId(),
            Map.of(BpmnProcessConstants.SURRENDER_OUTCOME, PermitSurrenderOutcome.SUBMITTED));
    }

    @Override
    public List<RequestTaskActionType> getTypes() {
        return List.of(RequestTaskActionType.PERMIT_SURRENDER_SUBMIT_APPLICATION);
    }
}