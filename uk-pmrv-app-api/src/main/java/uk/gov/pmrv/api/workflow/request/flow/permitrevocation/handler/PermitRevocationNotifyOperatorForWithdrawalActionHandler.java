package uk.gov.pmrv.api.workflow.request.flow.permitrevocation.handler;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.pmrv.api.common.domain.model.PmrvUser;
import uk.gov.pmrv.api.workflow.request.WorkflowService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.common.actionhandler.RequestTaskActionHandler;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.DecisionNotification;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.NotifyOperatorForDecisionRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitrevocation.domain.PermitRevocationRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitrevocation.domain.PermitRevocationWaitForAppealRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitrevocation.validation.PermitRevocationValidator;

@Component
@RequiredArgsConstructor
public class PermitRevocationNotifyOperatorForWithdrawalActionHandler
    implements RequestTaskActionHandler<NotifyOperatorForDecisionRequestTaskActionPayload> {

    private final RequestTaskService requestTaskService;
    private final PermitRevocationValidator validator;
    private final WorkflowService workflowService;

    @Override
    public void process(final Long requestTaskId,
                        final RequestTaskActionType requestTaskActionType,
                        final PmrvUser pmrvUser,
                        final NotifyOperatorForDecisionRequestTaskActionPayload taskActionPayload) {

        final RequestTask requestTask = requestTaskService.findTaskById(requestTaskId);

        // validate
        final DecisionNotification permitDecisionNotification = taskActionPayload.getDecisionNotification();
        final PermitRevocationWaitForAppealRequestTaskPayload taskPayload =
            (PermitRevocationWaitForAppealRequestTaskPayload) requestTask.getPayload();

        validator.validateNotifyUsers(requestTask, permitDecisionNotification, pmrvUser);
        validator.validateWaitForAppealRequestTaskPayload(taskPayload);

        // fill request payload
        final Request request = requestTask.getRequest();
        final PermitRevocationRequestPayload requestPayload = (PermitRevocationRequestPayload) request.getPayload();
        requestPayload.setWithdrawReason(taskPayload.getReason());
        requestPayload.setWithdrawFiles(taskPayload.getWithdrawFiles());
        requestPayload.setRevocationAttachments(taskPayload.getRevocationAttachments());
        requestPayload.setWithdrawDecisionNotification(taskActionPayload.getDecisionNotification());

        // complete task
        workflowService.completeTask(requestTask.getProcessTaskId());
    }

    @Override
    public List<RequestTaskActionType> getTypes() {
        return List.of(RequestTaskActionType.PERMIT_REVOCATION_NOTIFY_OPERATOR_FOR_WITHDRAWAL);
    }
}
