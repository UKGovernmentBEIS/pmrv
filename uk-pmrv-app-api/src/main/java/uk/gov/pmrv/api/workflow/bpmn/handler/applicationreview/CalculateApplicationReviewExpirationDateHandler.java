package uk.gov.pmrv.api.workflow.bpmn.handler.applicationreview;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.SubRequestType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestExpirationVarsBuilder;

@Service
@RequiredArgsConstructor
public class CalculateApplicationReviewExpirationDateHandler implements JavaDelegate {
    
    private final RequestExpirationVarsBuilder requestExpirationVarsBuilder;
    
    @Override
    public void execute(DelegateExecution execution) {
        final RequestType requestType = RequestType.valueOf((String) execution.getVariable(BpmnProcessConstants.REQUEST_TYPE));
        execution.setVariables(requestExpirationVarsBuilder.buildExpirationVars(requestType,
                SubRequestType.APPLICATION_REVIEW));
    }
}
