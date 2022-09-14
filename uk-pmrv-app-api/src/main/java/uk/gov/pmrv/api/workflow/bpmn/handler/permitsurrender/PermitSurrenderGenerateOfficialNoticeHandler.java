package uk.gov.pmrv.api.workflow.bpmn.handler.permitsurrender;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.permitsurrender.domain.PermitSurrenderReviewDeterminationType;
import uk.gov.pmrv.api.workflow.request.flow.permitsurrender.service.notification.PermitSurrenderOfficialNoticeService;

@Service
@RequiredArgsConstructor
public class PermitSurrenderGenerateOfficialNoticeHandler implements JavaDelegate {

    private final PermitSurrenderOfficialNoticeService service;
    
    @Override
    public void execute(DelegateExecution execution) {
        final String requestId = (String) execution.getVariable(BpmnProcessConstants.REQUEST_ID);
        final PermitSurrenderReviewDeterminationType determinationType = (PermitSurrenderReviewDeterminationType) execution
                .getVariable(BpmnProcessConstants.REVIEW_DETERMINATION);
        switch (determinationType) {
        case GRANTED:
            service.generateAndSaveGrantedOfficialNotice(requestId);    
            break;
        case REJECTED:
            service.generateAndSaveRejectedOfficialNotice(requestId);    
            break;
        case DEEMED_WITHDRAWN:
            service.generateAndSaveDeemedWithdrawnOfficialNotice(requestId);    
            break;
        default:
            throw new UnsupportedOperationException("Determination type is not supported: " + determinationType);
        }
        
    }
}
