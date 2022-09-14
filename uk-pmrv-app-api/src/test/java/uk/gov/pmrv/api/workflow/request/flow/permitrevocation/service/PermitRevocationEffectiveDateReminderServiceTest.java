package uk.gov.pmrv.api.workflow.request.flow.permitrevocation.service;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.pmrv.api.files.common.domain.dto.FileInfoDTO;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.DecisionNotification;
import uk.gov.pmrv.api.workflow.request.flow.permitrevocation.domain.PermitRevocationRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitrevocation.service.notification.PermitRevocationOfficialNoticeService;

@ExtendWith(MockitoExtension.class)
class PermitRevocationEffectiveDateReminderServiceTest {

	@InjectMocks
    private PermitRevocationEffectiveDateReminderService service;

    @Mock
    private RequestService requestService;
    
    @Mock
    private PermitRevocationOfficialNoticeService permitRevocationOfficialNoticeService;
    
    @Test
    void sendEffectiveDateReminder() {
    	final String requestId = "1";
		final FileInfoDTO officialNotice = FileInfoDTO.builder().name("off").uuid(UUID.randomUUID().toString()).build();
		final DecisionNotification decisionNotification = DecisionNotification.builder()
            .signatory("signatory")
            .build();
    	final PermitRevocationRequestPayload requestPayload = PermitRevocationRequestPayload.builder()
            .officialNotice(officialNotice)
            .decisionNotification(decisionNotification)
            .build();
        final Request request = Request.builder()
            .id(requestId)
            .payload(requestPayload)
            .build();
        
        when(requestService.findRequestById(requestId)).thenReturn(request);
        
        service.sendEffectiveDateReminder(requestId);
        
        verify(requestService, times(1)).findRequestById(requestId);
        verify(permitRevocationOfficialNoticeService, times(1))
            .sendOfficialNotice(request, officialNotice, decisionNotification);
    }
}
