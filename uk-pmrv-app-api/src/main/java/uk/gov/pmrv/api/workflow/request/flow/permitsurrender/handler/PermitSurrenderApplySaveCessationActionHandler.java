package uk.gov.pmrv.api.workflow.request.flow.permitsurrender.handler;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.pmrv.api.common.domain.model.PmrvUser;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.common.actionhandler.RequestTaskActionHandler;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.permit.cessation.PermitSaveCessationRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitsurrender.service.RequestPermitSurrenderCessationService;

@Component
@RequiredArgsConstructor
public class PermitSurrenderApplySaveCessationActionHandler implements
    RequestTaskActionHandler<PermitSaveCessationRequestTaskActionPayload> {

    private final RequestTaskService requestTaskService;
    private final RequestPermitSurrenderCessationService requestPermitSurrenderCessationService;

    @Override
    public void process(Long requestTaskId, RequestTaskActionType requestTaskActionType, PmrvUser pmrvUser,
                        PermitSaveCessationRequestTaskActionPayload payload) {
        RequestTask requestTask = requestTaskService.findTaskById(requestTaskId);
        requestPermitSurrenderCessationService.applySaveCessation(requestTask, payload);
    }

    @Override
    public List<RequestTaskActionType> getTypes() {
        return List.of(RequestTaskActionType.PERMIT_SURRENDER_SAVE_CESSATION);
    }
}
