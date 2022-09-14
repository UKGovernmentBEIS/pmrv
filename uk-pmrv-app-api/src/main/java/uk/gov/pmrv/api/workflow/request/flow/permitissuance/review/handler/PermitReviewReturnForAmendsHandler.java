package uk.gov.pmrv.api.workflow.request.flow.permitissuance.review.handler;

import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.pmrv.api.common.domain.model.PmrvUser;
import uk.gov.pmrv.api.workflow.request.WorkflowService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.common.actionhandler.RequestTaskActionHandler;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.RequestTaskActionEmptyPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.ReviewOutcome;
import uk.gov.pmrv.api.workflow.request.flow.permitissuance.review.domain.PermitIssuanceApplicationReturnedForAmendsRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitissuance.review.domain.PermitIssuanceApplicationReviewRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitissuance.review.mapper.PermitReviewMapper;
import uk.gov.pmrv.api.workflow.request.flow.permitissuance.review.service.PermitIssuanceReviewService;
import uk.gov.pmrv.api.workflow.request.flow.permitissuance.review.validation.PermitReviewReturnForAmendsValidatorService;

@Component
@RequiredArgsConstructor
public class PermitReviewReturnForAmendsHandler implements RequestTaskActionHandler<RequestTaskActionEmptyPayload> {

    private final RequestTaskService requestTaskService;
    private final RequestService requestService;
    private final PermitIssuanceReviewService permitIssuanceReviewService;
    private final PermitReviewReturnForAmendsValidatorService permitReviewReturnForAmendsValidatorService;
    private final WorkflowService workflowService;
    private static final PermitReviewMapper permitReviewMapper = Mappers.getMapper(PermitReviewMapper.class);

    @Override
    @Transactional
    public void process(Long requestTaskId, RequestTaskActionType requestTaskActionType, PmrvUser pmrvUser, RequestTaskActionEmptyPayload payload) {
        final RequestTask requestTask = requestTaskService.findTaskById(requestTaskId);

        // Validate that all review groups have been decided and at least one review group is 'Operator to amend'
        permitReviewReturnForAmendsValidatorService.validate((PermitIssuanceApplicationReviewRequestTaskPayload) requestTask.getPayload());

        // Update request payload
        permitIssuanceReviewService.saveRequestReturnForAmends(requestTask, pmrvUser);

        // Add PERMIT_ISSUANCE_APPLICATION_RETURNED_FOR_AMENDS request action
        createRequestAction(requestTask.getRequest(), pmrvUser, (PermitIssuanceApplicationReviewRequestTaskPayload) requestTask.getPayload());

        // Close task
        workflowService.completeTask(requestTask.getProcessTaskId(),
                Map.of(BpmnProcessConstants.REVIEW_OUTCOME, ReviewOutcome.AMENDS_NEEDED.name()));
    }

    @Override
    public List<RequestTaskActionType> getTypes() {
        return List.of(RequestTaskActionType.PERMIT_ISSUANCE_REVIEW_RETURN_FOR_AMENDS);
    }

    private void createRequestAction(Request request, PmrvUser pmrvUser, PermitIssuanceApplicationReviewRequestTaskPayload taskPayload) {
        PermitIssuanceApplicationReturnedForAmendsRequestActionPayload requestActionPayload = permitReviewMapper
                .toPermitIssuanceApplicationReturnedForAmendsRequestActionPayload(taskPayload);

        requestService.addActionToRequest(request, requestActionPayload, RequestActionType.PERMIT_ISSUANCE_APPLICATION_RETURNED_FOR_AMENDS,
                pmrvUser.getUserId());
    }
}
