package uk.gov.pmrv.api.workflow.request.flow.permitsurrender.handler;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.pmrv.api.common.domain.model.PmrvUser;
import uk.gov.pmrv.api.workflow.request.WorkflowService;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.common.actionhandler.RequestTaskActionHandler;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.NotifyOperatorForDecisionRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitsurrender.service.RequestPermitSurrenderCessationService;

@Component
@RequiredArgsConstructor
public class PermitSurrenderCessationNotifyOperatorActionHandler implements
    RequestTaskActionHandler<NotifyOperatorForDecisionRequestTaskActionPayload> {

    private final RequestTaskService requestTaskService;
    private final WorkflowService workflowService;
    private final RequestPermitSurrenderCessationService requestPermitSurrenderCessationService;

    @Override
    public void process(Long requestTaskId, RequestTaskActionType requestTaskActionType, PmrvUser pmrvUser,
                        NotifyOperatorForDecisionRequestTaskActionPayload taskActionPayload) {
        RequestTask requestTask = requestTaskService.findTaskById(requestTaskId);

        requestPermitSurrenderCessationService.executeNotifyOperatorActions(requestTask, pmrvUser, taskActionPayload);

        workflowService.completeTask(requestTask.getProcessTaskId());
    }

    @Override
    public List<RequestTaskActionType> getTypes() {
        return List.of(RequestTaskActionType.PERMIT_SURRENDER_CESSATION_NOTIFY_OPERATOR_FOR_DECISION);
    }
}
