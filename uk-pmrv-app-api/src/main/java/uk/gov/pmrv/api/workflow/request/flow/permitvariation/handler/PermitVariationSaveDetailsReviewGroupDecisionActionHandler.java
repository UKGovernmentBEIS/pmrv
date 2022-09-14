package uk.gov.pmrv.api.workflow.request.flow.permitvariation.handler;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.pmrv.api.common.domain.model.PmrvUser;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.common.actionhandler.RequestTaskActionHandler;
import uk.gov.pmrv.api.workflow.request.flow.permitvariation.domain.PermitVariationSaveDetailsReviewGroupDecisionRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitvariation.service.PermitVariationReviewService;

@Component
@RequiredArgsConstructor
public class PermitVariationSaveDetailsReviewGroupDecisionActionHandler implements
    RequestTaskActionHandler<PermitVariationSaveDetailsReviewGroupDecisionRequestTaskActionPayload> {

    private final RequestTaskService requestTaskService;
    private final PermitVariationReviewService permitVariationReviewService;

    @Override
    public void process(final Long requestTaskId,
        final RequestTaskActionType requestTaskActionType,
        final PmrvUser pmrvUser,
        final PermitVariationSaveDetailsReviewGroupDecisionRequestTaskActionPayload payload) {

        final RequestTask requestTask = requestTaskService.findTaskById(requestTaskId);

        permitVariationReviewService.saveDetailsReviewGroupDecision(payload, requestTask);
    }

    @Override
    public List<RequestTaskActionType> getTypes() {
        return List.of(RequestTaskActionType.PERMIT_VARIATION_SAVE_DETAILS_REVIEW_GROUP_DECISION);
    }
}
