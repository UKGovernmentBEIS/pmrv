package uk.gov.pmrv.api.workflow.bpmn.handler.permitnotification;

import lombok.RequiredArgsConstructor;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.permit.DeterminationType;
import uk.gov.pmrv.api.workflow.request.flow.permitnotification.service.PermitNotificationOfficialNoticeService;

@Service
@RequiredArgsConstructor
public class PermitNotificationGenerateOfficialNoticeHandler implements JavaDelegate {

    private final PermitNotificationOfficialNoticeService service;

    @Override
    public void execute(DelegateExecution execution) {

        final String requestId = (String) execution.getVariable(BpmnProcessConstants.REQUEST_ID);
        final DeterminationType determinationType =
            (DeterminationType) execution.getVariable(BpmnProcessConstants.REVIEW_DETERMINATION);
        switch (determinationType) {
            case GRANTED:
                service.generateAndSaveGrantedOfficialNotice(requestId);
                break;
            case REJECTED:
                service.generateAndSaveRejectedOfficialNotice(requestId);
                break;
            default:
                throw new UnsupportedOperationException("Determination type is not supported: " + determinationType);
        }

    }
}
