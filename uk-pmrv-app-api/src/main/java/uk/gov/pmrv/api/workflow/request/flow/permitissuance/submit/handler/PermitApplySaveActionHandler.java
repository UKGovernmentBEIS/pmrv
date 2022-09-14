package uk.gov.pmrv.api.workflow.request.flow.permitissuance.submit.handler;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.pmrv.api.common.domain.model.PmrvUser;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.common.actionhandler.RequestTaskActionHandler;
import uk.gov.pmrv.api.workflow.request.flow.permitissuance.submit.domain.PermitIssuanceSaveApplicationRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitissuance.submit.service.RequestPermitApplyService;

@RequiredArgsConstructor
@Component
public class PermitApplySaveActionHandler implements RequestTaskActionHandler<PermitIssuanceSaveApplicationRequestTaskActionPayload>{
    
    private final RequestPermitApplyService requestPermitApplyService;
    private final RequestTaskService requestTaskService;
    
    @Override
    public void process(final Long requestTaskId,
                        final RequestTaskActionType requestTaskActionType,
                        final PmrvUser pmrvUser, 
                        final PermitIssuanceSaveApplicationRequestTaskActionPayload payload) {
        
        final RequestTask requestTask = requestTaskService.findTaskById(requestTaskId);
        requestPermitApplyService.applySaveAction(payload, requestTask);
    }

    @Override
    public List<RequestTaskActionType> getTypes() {
        return List.of(RequestTaskActionType.PERMIT_ISSUANCE_SAVE_APPLICATION);
    }
}
