package uk.gov.pmrv.api.workflow.request.flow.aer.handler;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;

import uk.gov.pmrv.api.common.domain.model.PmrvUser;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.aer.domain.AerSaveApplicationRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aer.service.RequestAerApplyService;
import uk.gov.pmrv.api.workflow.request.flow.common.actionhandler.RequestTaskActionHandler;

import java.util.List;

@RequiredArgsConstructor
@Component
public class AerApplySaveActionHandler implements RequestTaskActionHandler<AerSaveApplicationRequestTaskActionPayload>{
    
    private final RequestAerApplyService requestAerApplyService;
    private final RequestTaskService requestTaskService;
    
    @Override
    public void process(final Long requestTaskId,
                        final RequestTaskActionType requestTaskActionType,
                        final PmrvUser pmrvUser, 
                        final AerSaveApplicationRequestTaskActionPayload payload) {
        
        final RequestTask requestTask = requestTaskService.findTaskById(requestTaskId);
        requestAerApplyService.applySaveAction(payload, requestTask);
    }

    @Override
    public List<RequestTaskActionType> getTypes() {
        return List.of(RequestTaskActionType.AER_SAVE_APPLICATION);
    }
}
