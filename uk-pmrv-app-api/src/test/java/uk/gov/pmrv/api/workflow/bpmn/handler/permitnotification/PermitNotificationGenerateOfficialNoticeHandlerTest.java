package uk.gov.pmrv.api.workflow.bpmn.handler.permitnotification;

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
import uk.gov.pmrv.api.workflow.request.flow.common.domain.permit.DeterminationType;
import uk.gov.pmrv.api.workflow.request.flow.permitnotification.service.PermitNotificationOfficialNoticeService;

@ExtendWith(MockitoExtension.class)
class PermitNotificationGenerateOfficialNoticeHandlerTest {

    @InjectMocks
    private PermitNotificationGenerateOfficialNoticeHandler handler;

    @Mock
    private PermitNotificationOfficialNoticeService service;

    @Mock
    private DelegateExecution execution;

    @Test
    void execute_granted() {

        final String requestId = "1";
        when(execution.getVariable(BpmnProcessConstants.REQUEST_ID)).thenReturn(requestId);
        when(execution.getVariable(BpmnProcessConstants.REVIEW_DETERMINATION)).thenReturn(
            DeterminationType.GRANTED);

        handler.execute(execution);

        verify(execution, times(1)).getVariable(BpmnProcessConstants.REQUEST_ID);
        verify(execution, times(1)).getVariable(BpmnProcessConstants.REVIEW_DETERMINATION);
        verify(service, times(1)).generateAndSaveGrantedOfficialNotice(requestId);
    }

    @Test
    void execute_rejected() {

        final String requestId = "1";
        when(execution.getVariable(BpmnProcessConstants.REQUEST_ID)).thenReturn(requestId);
        when(execution.getVariable(BpmnProcessConstants.REVIEW_DETERMINATION)).thenReturn(
            DeterminationType.REJECTED);

        handler.execute(execution);

        verify(execution, times(1)).getVariable(BpmnProcessConstants.REQUEST_ID);
        verify(execution, times(1)).getVariable(BpmnProcessConstants.REVIEW_DETERMINATION);
        verify(service, times(1)).generateAndSaveRejectedOfficialNotice(requestId);
    }
}
