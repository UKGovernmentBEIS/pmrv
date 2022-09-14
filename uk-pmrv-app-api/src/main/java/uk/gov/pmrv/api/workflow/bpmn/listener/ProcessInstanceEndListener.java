package uk.gov.pmrv.api.workflow.bpmn.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;

/**
 * Camunda listener that listens to completion of a process instance.
 */
@Component
@RequiredArgsConstructor
@Log4j2
public class ProcessInstanceEndListener {

    private final RequestService requestService;

    /**
     * On process instance end event terminate request.
     * Activity id should be equal to process id to avoid triggering end listener twice.
     *
     * @param execution {@link DelegateExecution}
     * @see EventListener
     */
    @EventListener(condition = "#execution.eventName=='end' " +
        "&& #execution.getBpmnModelElementInstance() instanceof T(org.camunda.bpm.model.bpmn.instance.EndEvent) " +
        "&& #execution.getActivityInstanceId() eq #execution.getProcessInstanceId() " +
        "&& #execution.getVariable('requestId') ne null")
    public void onProcessInstanceEndEvent(DelegateExecution execution) {
        if(execution.hasVariable(BpmnProcessConstants.REQUEST_ID)){
            String requestId = (String) execution.getVariable(BpmnProcessConstants.REQUEST_ID);
            requestService.terminateRequest(requestId, execution.getProcessInstanceId());
        }
    }

    /**
     * Added because of  PMRV-3127.
     * When calling {@link uk.gov.pmrv.api.workflow.request.WorkflowService#deleteProcessInstance(String, String)}
     * the end event that is fired does not have BpmnModelElementInstance so a separate listener was added.
     */
    @EventListener(condition = "#execution.eventName=='end' " +
        "&& #execution.getBpmnModelElementInstance() == null " +
        "&& #execution.getActivityInstanceId() eq #execution.getProcessInstanceId() " +
        "&& #execution.getVariable('requestId') ne null")
    public void onProcessInstanceCancelEvent(DelegateExecution execution) {
        onProcessInstanceEndEvent(execution);
        log.info("Request: {} cancelled by the system", execution.getVariable(BpmnProcessConstants.REQUEST_ID));
    }
}
