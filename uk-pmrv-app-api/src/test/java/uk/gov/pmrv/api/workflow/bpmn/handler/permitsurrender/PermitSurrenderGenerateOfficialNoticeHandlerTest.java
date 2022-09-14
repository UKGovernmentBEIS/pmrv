package uk.gov.pmrv.api.workflow.bpmn.handler.permitsurrender;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.permitsurrender.domain.PermitSurrenderReviewDeterminationType;
import uk.gov.pmrv.api.workflow.request.flow.permitsurrender.service.notification.PermitSurrenderOfficialNoticeService;

@ExtendWith(MockitoExtension.class)
public class PermitSurrenderGenerateOfficialNoticeHandlerTest {

    @InjectMocks
    private PermitSurrenderGenerateOfficialNoticeHandler handler;

    @Mock
    private PermitSurrenderOfficialNoticeService service;

    @Mock
    private DelegateExecution execution;

    @Test
    void execute_granted() throws Exception {
        final String requestId = "1";
        when(execution.getVariable(BpmnProcessConstants.REQUEST_ID)).thenReturn(requestId);
        when(execution.getVariable(BpmnProcessConstants.REVIEW_DETERMINATION)).thenReturn(PermitSurrenderReviewDeterminationType.GRANTED);

        handler.execute(execution);
        
        verify(execution, times(1)).getVariable(BpmnProcessConstants.REQUEST_ID);
        verify(execution, times(1)).getVariable(BpmnProcessConstants.REVIEW_DETERMINATION);
        verify(service, times(1)).generateAndSaveGrantedOfficialNotice(requestId);
    }
    
    @Test
    void execute_rejected() throws Exception {
        final String requestId = "1";
        when(execution.getVariable(BpmnProcessConstants.REQUEST_ID)).thenReturn(requestId);
        when(execution.getVariable(BpmnProcessConstants.REVIEW_DETERMINATION)).thenReturn(PermitSurrenderReviewDeterminationType.REJECTED);

        handler.execute(execution);
        
        verify(execution, times(1)).getVariable(BpmnProcessConstants.REQUEST_ID);
        verify(execution, times(1)).getVariable(BpmnProcessConstants.REVIEW_DETERMINATION);
        verify(service, times(1)).generateAndSaveRejectedOfficialNotice(requestId);
    }
    
    @Test
    void execute_deemed() throws Exception {
        final String requestId = "1";
        when(execution.getVariable(BpmnProcessConstants.REQUEST_ID)).thenReturn(requestId);
        when(execution.getVariable(BpmnProcessConstants.REVIEW_DETERMINATION)).thenReturn(PermitSurrenderReviewDeterminationType.DEEMED_WITHDRAWN);

        handler.execute(execution);
        
        verify(execution, times(1)).getVariable(BpmnProcessConstants.REQUEST_ID);
        verify(execution, times(1)).getVariable(BpmnProcessConstants.REVIEW_DETERMINATION);
        verify(service, times(1)).generateAndSaveDeemedWithdrawnOfficialNotice(requestId);
    }
}
