package uk.gov.pmrv.api.workflow.request.flow.permitrevocation.handler;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.pmrv.api.common.domain.model.PmrvUser;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.common.actionhandler.RequestTaskActionHandler;
import uk.gov.pmrv.api.workflow.request.flow.permitrevocation.domain.PermitRevocationApplicationWithdrawRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitrevocation.service.RequestPermitRevocationService;

@RequiredArgsConstructor
@Component
public class PermitRevocationWithdrawActionHandler
    implements RequestTaskActionHandler<PermitRevocationApplicationWithdrawRequestTaskActionPayload> {

    private final RequestPermitRevocationService requestPermitRevocationService;
    private final RequestTaskService requestTaskService;

    @Override
    public void process(final Long requestTaskId,
                        final RequestTaskActionType requestTaskActionType,
                        final PmrvUser pmrvUser,
                        final PermitRevocationApplicationWithdrawRequestTaskActionPayload actionPayload) {

        final RequestTask requestTask = requestTaskService.findTaskById(requestTaskId);
        requestPermitRevocationService.applyWithdrawPayload(actionPayload, requestTask);
    }

    @Override
    public List<RequestTaskActionType> getTypes() {
        return List.of(RequestTaskActionType.PERMIT_REVOCATION_WITHDRAW_APPLICATION);
    }
}
