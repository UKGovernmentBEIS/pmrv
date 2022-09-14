package uk.gov.pmrv.api.workflow.request.flow.permitrevocation.handler;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.stereotype.Component;
import static java.time.temporal.ChronoUnit.DAYS;
import uk.gov.pmrv.api.common.domain.model.PmrvUser;
import uk.gov.pmrv.api.workflow.request.WorkflowService;
import uk.gov.pmrv.api.workflow.utils.DateUtils;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.common.actionhandler.RequestTaskActionHandler;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.DecisionNotification;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.NotifyOperatorForDecisionRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitrevocation.domain.PermitRevocation;
import uk.gov.pmrv.api.workflow.request.flow.permitrevocation.domain.PermitRevocationApplicationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitrevocation.domain.PermitRevocationOutcome;
import uk.gov.pmrv.api.workflow.request.flow.permitrevocation.domain.PermitRevocationRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitrevocation.validation.PermitRevocationValidator;

@Component
@RequiredArgsConstructor
public class PermitRevocationNotifyOperatorActionHandler
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
        final PermitRevocationApplicationSubmitRequestTaskPayload taskPayload =
            (PermitRevocationApplicationSubmitRequestTaskPayload) requestTask.getPayload();

        validator.validateNotifyUsers(requestTask, permitDecisionNotification, pmrvUser);
        validator.validateSubmitRequestTaskPayload(taskPayload);

        final PermitRevocation permitRevocation = taskPayload.getPermitRevocation();

        // fill request payload
        final Request request = requestTask.getRequest();
        final PermitRevocationRequestPayload requestPayload = (PermitRevocationRequestPayload) request.getPayload();
        requestPayload.setPermitRevocation(permitRevocation);
        requestPayload.setDecisionNotification(taskActionPayload.getDecisionNotification());

        //if no fee charge is requested, set request payment amount to zero
        if(BooleanUtils.isFalse(permitRevocation.getFeeCharged())) {
            requestPayload.setPaymentAmount(BigDecimal.ZERO);
        }

        //set request's submission date
        request.setSubmissionDate(LocalDateTime.now());
        
        // complete task
        workflowService.completeTask(requestTask.getProcessTaskId(), buildTaskVariables(permitRevocation));
    }

    @Override
    public List<RequestTaskActionType> getTypes() {
        return List.of(RequestTaskActionType.PERMIT_REVOCATION_NOTIFY_OPERATOR_FOR_SUBMISSION);
    }

    private Map<String, Object> buildTaskVariables(PermitRevocation permitRevocation) {
        Map<String, Object> taskVariables = new HashMap<>();
        taskVariables.put(BpmnProcessConstants.REVOCATION_OUTCOME, PermitRevocationOutcome.NOTIFY_OPERATOR);

        final LocalDate effectiveLocalDate = permitRevocation.getEffectiveDate();
        final Date effectiveDate = DateUtils.convertLocalDateToDate(effectiveLocalDate);
        taskVariables.put(BpmnProcessConstants.REVOCATION_EFFECTIVE_DATE, effectiveDate);
        
        final LocalDate reminderEffectiveLocalDate = effectiveLocalDate.minus(28, DAYS);
		taskVariables.put(BpmnProcessConstants.REVOCATION_REMINDER_EFFECTIVE_DATE,
				DateUtils.convertLocalDateToDate(reminderEffectiveLocalDate));

        final LocalDate feeDate = permitRevocation.getFeeDate();
        final Date paymentExpirationDate = feeDate != null ? DateUtils.convertLocalDateToDate(feeDate) : null;
        taskVariables.put(BpmnProcessConstants.PAYMENT_EXPIRATION_DATE, paymentExpirationDate);
        
        return taskVariables;
    }
}
