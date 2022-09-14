package uk.gov.pmrv.api.workflow.request.flow.permitissuance.submit.handler;

import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.pmrv.api.common.domain.model.PmrvUser;
import uk.gov.pmrv.api.workflow.request.WorkflowService;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.common.actionhandler.RequestTaskActionHandler;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.RequestTaskActionEmptyPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitissuance.submit.service.RequestPermitApplyService;

@RequiredArgsConstructor
@Component
public class PermitApplySubmitActionHandler implements RequestTaskActionHandler<RequestTaskActionEmptyPayload>{
    
    private final RequestPermitApplyService requestPermitApplyService;
    private final WorkflowService workflowService;
    private final RequestTaskService requestTaskService;

    @Override
    public void process(final Long requestTaskId,
                        final RequestTaskActionType requestTaskActionType,
                        final PmrvUser pmrvUser, 
                        final RequestTaskActionEmptyPayload payload) {
        
        final RequestTask requestTask = requestTaskService.findTaskById(requestTaskId);

        //submit permit
        requestPermitApplyService.applySubmitAction(requestTask, pmrvUser);

        //set request's submission date
        requestTask.getRequest().setSubmissionDate(LocalDateTime.now());
        
        //complete task
        workflowService.completeTask(requestTask.getProcessTaskId());
       
    }

    @Override
    public List<RequestTaskActionType> getTypes() {
        return List.of(RequestTaskActionType.PERMIT_ISSUANCE_SUBMIT_APPLICATION);
    }
}
