package uk.gov.pmrv.api.workflow.bpmn.handler.permitnotification;

import org.apache.commons.lang3.time.DateUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.SubRequestType;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestExpirationVarsBuilder;
import uk.gov.pmrv.api.workflow.request.flow.permitnotification.service.PermitNotificationReviewSubmittedService;

import java.util.Date;
import java.util.Map;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PermitNotificationGrantedHandlerTest {

    @InjectMocks
    private PermitNotificationGrantedHandler handler;

    @Mock
    private PermitNotificationReviewSubmittedService reviewSubmittedService;

    @Mock
    private RequestExpirationVarsBuilder requestExpirationVarsBuilder;
    
    @Test
    void execute() {
        final DelegateExecution execution = mock(DelegateExecution.class);
        final String requestId = "1";
        final Date expirationDate = DateUtils.addDays(new Date(), 10);
        
        final Map<String, Object> vars = Map.of(
                "var1", "val1"
                );
        
        when(execution.getVariable(BpmnProcessConstants.REQUEST_ID)).thenReturn(requestId);
        when(reviewSubmittedService.getFollowUpInfo(requestId)).thenReturn(ImmutablePair.of(true, expirationDate));
        when(requestExpirationVarsBuilder.buildExpirationVars(SubRequestType.FOLLOW_UP_RESPONSE, expirationDate))
            .thenReturn(vars);
        
        // invoke
        handler.execute(execution);
        
        verify(execution, times(1)).getVariable(BpmnProcessConstants.REQUEST_ID);
        verify(reviewSubmittedService, times(1)).executeGrantedPostActions(requestId);
        verify(reviewSubmittedService, times(1)).getFollowUpInfo(requestId);
        verify(execution, times(1)).setVariable(BpmnProcessConstants.FOLLOW_UP_RESPONSE_NEEDED, true);
        verify(requestExpirationVarsBuilder, times(1)).buildExpirationVars(SubRequestType.FOLLOW_UP_RESPONSE, expirationDate);
        verify(execution, times(1)).setVariables(vars);
    }
    
    @Test
    void execute_follow_up_not_required() {
        final DelegateExecution execution = mock(DelegateExecution.class);
        final String requestId = "1";
        
        when(execution.getVariable(BpmnProcessConstants.REQUEST_ID)).thenReturn(requestId);
        when(reviewSubmittedService.getFollowUpInfo(requestId)).thenReturn(ImmutablePair.of(false, null));

        // invoke
        handler.execute(execution);
        
        verify(execution, times(1)).getVariable(BpmnProcessConstants.REQUEST_ID);
        verify(reviewSubmittedService, times(1)).executeGrantedPostActions(requestId);
        verify(reviewSubmittedService, times(1)).getFollowUpInfo(requestId);
        verify(execution, times(1)).setVariable(BpmnProcessConstants.FOLLOW_UP_RESPONSE_NEEDED, false);
        verifyNoInteractions(requestExpirationVarsBuilder);
        verifyNoMoreInteractions(execution);
    }

}
