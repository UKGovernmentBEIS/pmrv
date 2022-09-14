package uk.gov.pmrv.api.workflow.bpmn.handler.permitvariation;

import lombok.RequiredArgsConstructor;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.common.domain.enumeration.RoleType;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.permitvariation.service.PermitVariationCancelledService;

@Service
@RequiredArgsConstructor
public class PermitVariationCancelledHandler implements JavaDelegate {

    private final PermitVariationCancelledService service;

    @Override
    public void execute(final DelegateExecution execution) throws Exception {

        final String requestId = (String) execution.getVariable(BpmnProcessConstants.REQUEST_ID);
        final RoleType userRole = (RoleType) execution.getVariable(BpmnProcessConstants.REQUEST_INITIATOR_ROLE_TYPE);
        service.cancel(requestId, userRole);
    }
}
