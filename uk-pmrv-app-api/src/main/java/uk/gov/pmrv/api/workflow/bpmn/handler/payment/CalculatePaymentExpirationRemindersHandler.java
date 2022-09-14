package uk.gov.pmrv.api.workflow.bpmn.handler.payment;

import java.util.Date;
import lombok.RequiredArgsConstructor;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.SubRequestType;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestExpirationVarsBuilder;

@Service
@RequiredArgsConstructor
public class CalculatePaymentExpirationRemindersHandler implements JavaDelegate {

    private final RequestExpirationVarsBuilder requestExpirationVarsBuilder;

    @Override
    public void execute(DelegateExecution execution) {
        final Date paymentExpirationDate = (Date) execution.getVariable(BpmnProcessConstants.PAYMENT_EXPIRATION_DATE);
        execution.setVariables(requestExpirationVarsBuilder.buildExpirationReminderVars(SubRequestType.PAYMENT, paymentExpirationDate));
    }
}
