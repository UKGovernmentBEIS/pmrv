package uk.gov.pmrv.api.workflow.request.flow.permitvariation.handler;

import java.util.List;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import uk.gov.pmrv.api.common.domain.model.PmrvUser;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.common.actionhandler.RequestTaskActionHandler;
import uk.gov.pmrv.api.workflow.request.flow.common.validation.PermitReviewDeterminationAndDecisionsValidatorService;
import uk.gov.pmrv.api.workflow.request.flow.permitvariation.domain.PermitVariationApplicationReviewRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitvariation.domain.PermitVariationDeterminateable;
import uk.gov.pmrv.api.workflow.request.flow.permitvariation.domain.PermitVariationSaveReviewDeterminationRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitvariation.service.PermitVariationReviewService;

@Component
@RequiredArgsConstructor
public class PermitVariationReviewSaveDeterminationActionHandler 
	implements RequestTaskActionHandler<PermitVariationSaveReviewDeterminationRequestTaskActionPayload> {

    private final RequestTaskService requestTaskService;
    private final PermitVariationReviewService permitVariationReviewService;
    private final PermitReviewDeterminationAndDecisionsValidatorService permitReviewDeterminationValidatorService;

    @Override
    public void process(final Long requestTaskId,
                        final RequestTaskActionType requestTaskActionType,
                        final PmrvUser pmrvUser,
                        final PermitVariationSaveReviewDeterminationRequestTaskActionPayload payload) {
        final PermitVariationDeterminateable determination = payload.getDetermination();
        final RequestTask requestTask = requestTaskService.findTaskById(requestTaskId);
        final RequestType requestType = requestTask.getRequest().getType();
        final PermitVariationApplicationReviewRequestTaskPayload taskPayload =
            (PermitVariationApplicationReviewRequestTaskPayload) requestTask.getPayload();

        if (!permitReviewDeterminationValidatorService.isDeterminationAndDecisionsValid(determination, taskPayload, requestType)) {
            throw new BusinessException(ErrorCode.FORM_VALIDATION);
        }

        permitVariationReviewService.saveDetermination(payload, requestTask);
    }

    @Override
    public List<RequestTaskActionType> getTypes() {
        return List.of(RequestTaskActionType.PERMIT_VARIATION_SAVE_REVIEW_DETERMINATION);
    }
}
