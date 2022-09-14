package uk.gov.pmrv.api.workflow.bpmn.handler.common;

import lombok.RequiredArgsConstructor;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;

import org.springframework.stereotype.Service;

import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestStatus;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;

@Service
@RequiredArgsConstructor
public class RequestUpdateStatusHandler implements JavaDelegate {

    private final RequestService requestService;

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        String requestId = (String) execution.getVariable(BpmnProcessConstants.REQUEST_ID);
        RequestStatus status = RequestStatus.valueOf((String) execution.getVariable(BpmnProcessConstants.REQUEST_STATUS));

        requestService.updateRequestStatus(requestId, status);
    }
}
