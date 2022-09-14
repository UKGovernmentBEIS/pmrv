package uk.gov.pmrv.api.workflow.request.flow.rfi.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.WorkflowService;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.SubRequestType;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestExpirationVarsBuilder;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class RfiSendEventService {

    private final WorkflowService workflowService;
    private final RequestExpirationVarsBuilder requestExpirationVarsBuilder;

    public void send(final String requestId, final LocalDate deadline) {
        final Date deadlineDate = Date.from(deadline
                .atTime(LocalTime.MIN)
                .atZone(ZoneId.systemDefault())
                .toInstant());
        
        final Map<String, Object> rfiVariables = new HashMap<>();
        rfiVariables.put(BpmnProcessConstants.RFI_START_TIME, new Date());
        rfiVariables.putAll(requestExpirationVarsBuilder
                .buildExpirationVars(SubRequestType.RFI, deadlineDate));
        
        workflowService.sendEvent(requestId, BpmnProcessConstants.RFI_REQUESTED, rfiVariables);
    }
}
