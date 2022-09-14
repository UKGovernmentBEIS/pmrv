package uk.gov.pmrv.api.workflow.request.flow.permitissuance.review.handler;

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
import uk.gov.pmrv.api.workflow.request.flow.permitissuance.review.domain.PermitIssuanceApplicationReviewRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitissuance.review.domain.PermitIssuanceDeterminateable;
import uk.gov.pmrv.api.workflow.request.flow.permitissuance.review.domain.PermitIssuanceSaveReviewDeterminationRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitissuance.review.service.PermitIssuanceReviewService;

@Component
@RequiredArgsConstructor
public class PermitReviewSaveDeterminationActionHandler
    implements RequestTaskActionHandler<PermitIssuanceSaveReviewDeterminationRequestTaskActionPayload> {

    private final RequestTaskService requestTaskService;
    private final PermitIssuanceReviewService permitIssuanceReviewService;
    private final PermitReviewDeterminationAndDecisionsValidatorService permitReviewDeterminationValidatorAndDecisionsService;

    @Override
    public void process(final Long requestTaskId,
                        final RequestTaskActionType requestTaskActionType,
                        final PmrvUser pmrvUser,
                        final PermitIssuanceSaveReviewDeterminationRequestTaskActionPayload payload) {

        final PermitIssuanceDeterminateable determination = payload.getDetermination();

        final RequestTask requestTask = requestTaskService.findTaskById(requestTaskId);
        final RequestType requestType = requestTask.getRequest().getType();
        final PermitIssuanceApplicationReviewRequestTaskPayload taskPayload =
            (PermitIssuanceApplicationReviewRequestTaskPayload) requestTask.getPayload();

        if (!permitReviewDeterminationValidatorAndDecisionsService.isDeterminationAndDecisionsValid(determination, taskPayload, requestType)) {
            throw new BusinessException(ErrorCode.FORM_VALIDATION);
        }

        permitIssuanceReviewService.saveDetermination(payload, requestTask);
    }

    @Override
    public List<RequestTaskActionType> getTypes() {
        return List.of(RequestTaskActionType.PERMIT_ISSUANCE_SAVE_REVIEW_DETERMINATION);
    }
}
