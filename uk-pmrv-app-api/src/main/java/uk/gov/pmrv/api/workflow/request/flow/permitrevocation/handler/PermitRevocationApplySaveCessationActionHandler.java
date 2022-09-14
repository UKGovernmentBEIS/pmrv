package uk.gov.pmrv.api.workflow.request.flow.permitrevocation.handler;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.pmrv.api.common.domain.model.PmrvUser;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.common.actionhandler.RequestTaskActionHandler;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.permit.cessation.PermitSaveCessationRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitrevocation.service.RequestPermitRevocationCessationService;

@Component
@RequiredArgsConstructor
public class PermitRevocationApplySaveCessationActionHandler implements
    RequestTaskActionHandler<PermitSaveCessationRequestTaskActionPayload> {

    private final RequestTaskService requestTaskService;
    private final RequestPermitRevocationCessationService cessationService;

    @Override
    public void process(final Long requestTaskId,
                        final RequestTaskActionType requestTaskActionType,
                        final PmrvUser pmrvUser,
                        final PermitSaveCessationRequestTaskActionPayload payload) {

        final RequestTask requestTask = requestTaskService.findTaskById(requestTaskId);
        cessationService.applySaveCessation(requestTask, payload);
    }

    @Override
    public List<RequestTaskActionType> getTypes() {
        return List.of(RequestTaskActionType.PERMIT_REVOCATION_SAVE_CESSATION);
    }
}
