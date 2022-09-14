package uk.gov.pmrv.api.workflow.request.flow.permitsurrender.service;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.permitsurrender.service.notification.PermitSurrenderOfficialNoticeService;

@ExtendWith(MockitoExtension.class)
class PermitSurrenderNoticeDateReminderServiceTest {

	@InjectMocks
    private PermitSurrenderNoticeDateReminderService service;

    @Mock
    private RequestService requestService;
    
    @Mock
    private PermitSurrenderOfficialNoticeService permitSurrenderOfficialNoticeService;
    
    @Test
    void sendEffectiveDateReminder() {
    	final String requestId = "1";
        final Request request = Request.builder()
            .id(requestId)
            .build();
        
        when(requestService.findRequestById(requestId)).thenReturn(request);
        
        service.sendNoticeDateReminder(requestId);
        
        verify(requestService, times(1)).findRequestById(requestId);
        verify(permitSurrenderOfficialNoticeService, times(1)).sendReviewDeterminationOfficialNotice(request);
    }
    
}
