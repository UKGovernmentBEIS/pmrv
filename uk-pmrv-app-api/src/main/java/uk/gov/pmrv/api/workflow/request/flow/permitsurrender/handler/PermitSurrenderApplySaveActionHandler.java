package uk.gov.pmrv.api.workflow.request.flow.permitsurrender.handler;

import java.util.List;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import uk.gov.pmrv.api.common.domain.model.PmrvUser;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.common.actionhandler.RequestTaskActionHandler;
import uk.gov.pmrv.api.workflow.request.flow.permitsurrender.domain.PermitSurrenderSaveApplicationRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitsurrender.service.RequestPermitSurrenderService;

@RequiredArgsConstructor
@Component
public class PermitSurrenderApplySaveActionHandler implements RequestTaskActionHandler<PermitSurrenderSaveApplicationRequestTaskActionPayload>{
    
    private final RequestPermitSurrenderService requestPermitSurrenderService;
    private final RequestTaskService requestTaskService;
    
    @Override
    public void process(final Long requestTaskId,
                        final RequestTaskActionType requestTaskActionType,
                        final PmrvUser pmrvUser, 
                        final PermitSurrenderSaveApplicationRequestTaskActionPayload actionPayload) {
        final RequestTask requestTask = requestTaskService.findTaskById(requestTaskId);
        requestPermitSurrenderService.applySavePayload(actionPayload, requestTask);
    }

    @Override
    public List<RequestTaskActionType> getTypes() {
        return List.of(RequestTaskActionType.PERMIT_SURRENDER_SAVE_APPLICATION);
    }
}
