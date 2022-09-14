package uk.gov.pmrv.api.workflow.bpmn.handler.payment;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import lombok.RequiredArgsConstructor;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestTaskTimeManagementService;

@Service
@RequiredArgsConstructor
public class SetPaymentTaskDueDateHandler implements JavaDelegate {

    private final RequestTaskTimeManagementService requestTaskTimeManagementService;

    @Override
    public void execute(DelegateExecution execution) {
        final String expirationDateKey = BpmnProcessConstants.PAYMENT_EXPIRATION_DATE;
        final String requestId = (String) execution.getVariable(BpmnProcessConstants.REQUEST_ID);
        final Date paymentExpirationDate = (Date) execution.getVariable(expirationDateKey);
        final LocalDate dueDate = paymentExpirationDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        requestTaskTimeManagementService.setDueDateToTasks(requestId, expirationDateKey, dueDate);
    }
}
