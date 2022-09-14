package uk.gov.pmrv.api.workflow.request.flow.common.service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.SubRequestType;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;

@Service
@RequiredArgsConstructor
public class RequestExpirationVarsBuilder {
    
    private final RequestCalculateExpirationService requestCalculateExpirationService;
    
    public Map<String, Object> buildExpirationVars(RequestType requestType, SubRequestType subRequestType) {
        final Date requestExpirationDate = requestCalculateExpirationService.calculateExpirationDate(requestType);
        return this.buildExpirationVars(subRequestType, requestExpirationDate);
    }
    
    public Map<String, Object> buildExpirationVars(SubRequestType subRequestType, Date expirationDate) {
        Map<String, Object> expirationVars = new HashMap<>();
        expirationVars.put(subRequestType.getCode() + BpmnProcessConstants._EXPIRATION_DATE, expirationDate);
        expirationVars.putAll(buildExpirationReminderVars(subRequestType, expirationDate));

        return expirationVars;
    }

    public Map<String, Object> buildExpirationReminderVars(SubRequestType subRequestType, Date expirationDate) {
        Map<String, Object> expirationReminderVars = new HashMap<>();
        expirationReminderVars.put(subRequestType.getCode() + BpmnProcessConstants._FIRST_REMINDER_DATE,
            requestCalculateExpirationService.calculateFirstReminderDate(expirationDate));
        expirationReminderVars.put(subRequestType.getCode() + BpmnProcessConstants._SECOND_REMINDER_DATE,
            requestCalculateExpirationService.calculateSecondReminderDate(expirationDate));

        return expirationReminderVars;
    }
}
