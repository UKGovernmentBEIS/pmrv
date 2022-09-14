package uk.gov.pmrv.api.workflow.request.flow.rde.handler;

import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.pmrv.api.common.domain.model.PmrvUser;
import uk.gov.pmrv.api.workflow.request.WorkflowService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.common.actionhandler.RequestTaskActionHandler;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.rde.domain.RdeDecisionType;
import uk.gov.pmrv.api.workflow.request.flow.rde.domain.RdeForceDecisionPayload;
import uk.gov.pmrv.api.workflow.request.flow.rde.domain.RdeForceDecisionRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.rde.domain.RdeForceDecisionRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.rde.domain.RdeOutcome;
import uk.gov.pmrv.api.workflow.request.flow.rde.domain.RequestPayloadRdeable;

@Component
@RequiredArgsConstructor
public class RdeForceDecisionActionHandler
    implements RequestTaskActionHandler<RdeForceDecisionRequestTaskActionPayload> {

    private final RequestTaskService requestTaskService;
    private final WorkflowService workflowService;

    @Override
    public void process(final Long requestTaskId,
                        final RequestTaskActionType requestTaskActionType,
                        final PmrvUser pmrvUser,
                        final RdeForceDecisionRequestTaskActionPayload taskActionPayload) {
        
        // update task payload
        final RequestTask requestTask = requestTaskService.findTaskById(requestTaskId);
        final RdeForceDecisionRequestTaskPayload taskPayload = (RdeForceDecisionRequestTaskPayload) requestTask.getPayload();
        final RdeForceDecisionPayload rdeForceDecisionPayload = taskActionPayload.getRdeForceDecisionPayload();
        taskPayload.setRdeForceDecisionPayload(rdeForceDecisionPayload);
        
        // update request payload
        final Request request = requestTask.getRequest();
        final RequestPayloadRdeable requestPayload = (RequestPayloadRdeable) request.getPayload();
        requestPayload.getRdeData().setRdeForceDecisionPayload(rdeForceDecisionPayload);
        requestPayload.getRdeData().setRdeAttachments(taskPayload.getRdeAttachments());
        
        // complete task
        final RdeOutcome rdeOutcome = rdeForceDecisionPayload.getDecision() == RdeDecisionType.ACCEPTED ?
            RdeOutcome.FORCE_ACCEPTED : RdeOutcome.FORCE_REJECTED;
        workflowService.completeTask(requestTask.getProcessTaskId(),
            Map.of(BpmnProcessConstants.RDE_OUTCOME, rdeOutcome));
    }

    @Override
    public List<RequestTaskActionType> getTypes() {
        return List.of(RequestTaskActionType.RDE_FORCE_DECISION);
    }
}
