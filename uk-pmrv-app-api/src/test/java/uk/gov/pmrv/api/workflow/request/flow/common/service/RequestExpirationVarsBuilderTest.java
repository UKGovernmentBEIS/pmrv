package uk.gov.pmrv.api.workflow.request.flow.common.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Date;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.SubRequestType;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;

@ExtendWith(MockitoExtension.class)
class RequestExpirationVarsBuilderTest {

    @InjectMocks
    private RequestExpirationVarsBuilder builder;

    @Mock
    private RequestCalculateExpirationService requestCalculateExpirationService;
    
    @Test
    void buildExpirationReminderVars_by_requestType_and_subType() {
        RequestType requestType = RequestType.PERMIT_ISSUANCE;
        SubRequestType subRequestType = SubRequestType.RFI;
        
        Date expirationDate = new Date();
        Date firstReminderDate = new Date();
        Date secondReminderDate = new Date();
        
        when(requestCalculateExpirationService.calculateExpirationDate(requestType)).thenReturn(expirationDate);
        when(requestCalculateExpirationService.calculateFirstReminderDate(expirationDate)).thenReturn(firstReminderDate);
        when(requestCalculateExpirationService.calculateSecondReminderDate(expirationDate)).thenReturn(secondReminderDate);
        
        // invoke
        Map<String, Object> result = builder.buildExpirationVars(requestType, subRequestType);
        
        assertThat(result).isEqualTo(Map.of(
                subRequestType.getCode() + BpmnProcessConstants._EXPIRATION_DATE, expirationDate,
                subRequestType.getCode() + BpmnProcessConstants._FIRST_REMINDER_DATE, firstReminderDate, 
                subRequestType.getCode() + BpmnProcessConstants._SECOND_REMINDER_DATE, secondReminderDate)
                );
        verify(requestCalculateExpirationService, times(1)).calculateExpirationDate(requestType);
        verify(requestCalculateExpirationService, times(1)).calculateFirstReminderDate(expirationDate);
        verify(requestCalculateExpirationService, times(1)).calculateSecondReminderDate(expirationDate);
    }
}
