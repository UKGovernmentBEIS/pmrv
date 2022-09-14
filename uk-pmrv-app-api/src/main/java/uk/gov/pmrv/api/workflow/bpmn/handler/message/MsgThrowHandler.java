package uk.gov.pmrv.api.workflow.bpmn.handler.message;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.camunda.bpm.model.bpmn.instance.MessageEventDefinition;
import org.camunda.bpm.model.bpmn.instance.ThrowEvent;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;

@Service
public class MsgThrowHandler implements JavaDelegate {

    @Override
    public void execute(final DelegateExecution execution) {
        
        final ThrowEvent messageEvent = (ThrowEvent) execution.getBpmnModelElementInstance();
        final MessageEventDefinition messageEventDefinition = (MessageEventDefinition) messageEvent
            .getEventDefinitions().iterator().next();
        final String messageName = messageEventDefinition.getMessage().getName();
        execution.getProcessEngineServices()
            .getRuntimeService()
            .createMessageCorrelation(messageName)
            .processInstanceBusinessKey((String) execution.getVariable(BpmnProcessConstants.BUSINESS_KEY))
            .correlateAll();
    }
}
