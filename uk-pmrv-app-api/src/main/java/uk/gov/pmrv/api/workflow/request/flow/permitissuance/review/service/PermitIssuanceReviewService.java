package uk.gov.pmrv.api.workflow.request.flow.permitissuance.review.service;

import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.pmrv.api.common.domain.model.PmrvUser;
import uk.gov.pmrv.api.permit.domain.PermitContainer;
import uk.gov.pmrv.api.permit.validation.PermitValidatorService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.DecisionNotification;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.permit.PermitReviewGroup;
import uk.gov.pmrv.api.workflow.request.flow.common.service.permit.PermitReviewService;
import uk.gov.pmrv.api.workflow.request.flow.permitissuance.common.domain.PermitIssuanceRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitissuance.common.mapper.PermitMapper;
import uk.gov.pmrv.api.workflow.request.flow.permitissuance.review.domain.PermitIssuanceApplicationAmendsSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitissuance.review.domain.PermitIssuanceApplicationReviewRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitissuance.review.domain.PermitIssuanceDeterminateable;
import uk.gov.pmrv.api.workflow.request.flow.permitissuance.review.domain.PermitIssuanceReviewDecision;
import uk.gov.pmrv.api.workflow.request.flow.permitissuance.review.domain.PermitIssuanceReviewRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitissuance.review.domain.PermitIssuanceReviewRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitissuance.review.domain.PermitIssuanceSaveApplicationAmendRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitissuance.review.domain.PermitIssuanceSaveApplicationReviewRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitissuance.review.domain.PermitIssuanceSaveReviewDeterminationRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitissuance.review.domain.PermitIssuanceSaveReviewGroupDecisionRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitissuance.review.domain.PermitIssuanceSubmitApplicationAmendRequestTaskActionPayload;

@Service
@RequiredArgsConstructor
public class PermitIssuanceReviewService {

    private final RequestService requestService;
    private final RequestTaskService requestTaskService;
    private final PermitReviewService permitReviewService;
    private final PermitValidatorService permitValidatorService;
    private static final PermitMapper permitMapper = Mappers.getMapper(PermitMapper.class);

    @Transactional
    public void savePermit(PermitIssuanceSaveApplicationReviewRequestTaskActionPayload permitReviewRequestTaskActionPayload,
        RequestTask requestTask) {
        PermitIssuanceApplicationReviewRequestTaskPayload
            permitIssuanceApplicationReviewRequestTaskPayload = (PermitIssuanceApplicationReviewRequestTaskPayload) requestTask.getPayload();

		permitReviewService.cleanUpDeprecatedReviewGroupDecisions(permitIssuanceApplicationReviewRequestTaskPayload,
				permitReviewRequestTaskActionPayload.getPermit().getMonitoringApproaches().getMonitoringApproaches().keySet());
		
		permitReviewService.resetDeterminationIfNotDeemedWithdrawn(permitIssuanceApplicationReviewRequestTaskPayload);
        
        updatePermitIssuanceReviewRequestTaskPayload(permitIssuanceApplicationReviewRequestTaskPayload, permitReviewRequestTaskActionPayload);

        requestTaskService.saveRequestTask(requestTask);
    }

    @Transactional
    public void saveReviewGroupDecision(final PermitIssuanceSaveReviewGroupDecisionRequestTaskActionPayload payload,
        final RequestTask requestTask) {

        final PermitReviewGroup group = payload.getGroup();
        final PermitIssuanceReviewDecision decision = payload.getDecision();

        final PermitIssuanceApplicationReviewRequestTaskPayload taskPayload =
            (PermitIssuanceApplicationReviewRequestTaskPayload) requestTask.getPayload();
        final Map<PermitReviewGroup, PermitIssuanceReviewDecision> reviewGroupDecisions = taskPayload.getReviewGroupDecisions();

        reviewGroupDecisions.put(group, decision);

        final Map<String, Boolean> reviewSectionsCompleted = payload.getReviewSectionsCompleted();
        taskPayload.setReviewSectionsCompleted(reviewSectionsCompleted);
        
        permitReviewService.resetDeterminationIfNotValidWithDecisions(taskPayload, requestTask.getRequest().getType());
        
        requestTaskService.saveRequestTask(requestTask);
    }

    @Transactional
    public void saveDetermination(final PermitIssuanceSaveReviewDeterminationRequestTaskActionPayload payload,
        final RequestTask requestTask) {

        final PermitIssuanceApplicationReviewRequestTaskPayload taskPayload =
            (PermitIssuanceApplicationReviewRequestTaskPayload) requestTask.getPayload();

        final PermitIssuanceDeterminateable determination = payload.getDetermination();
        taskPayload.setDetermination(determination);

        final Map<String, Boolean> reviewSectionsCompleted = payload.getReviewSectionsCompleted();
        taskPayload.setReviewSectionsCompleted(reviewSectionsCompleted);

        requestTaskService.saveRequestTask(requestTask);
    }

    @Transactional
    public void savePermitDecisionNotification(final RequestTask requestTask,
        final DecisionNotification permitDecisionNotification,
        final PmrvUser pmrvUser) {

        final Request request = requestTask.getRequest();
        final PermitIssuanceApplicationReviewRequestTaskPayload taskPayload =
            (PermitIssuanceApplicationReviewRequestTaskPayload) requestTask.getPayload();

        final PermitIssuanceRequestPayload permitIssuanceRequestPayload =
            (PermitIssuanceRequestPayload) request.getPayload();

        updatePermitIssuanceRequestPayload(permitIssuanceRequestPayload, taskPayload, pmrvUser);
        permitIssuanceRequestPayload.setPermitDecisionNotification(permitDecisionNotification);

        requestService.saveRequest(request);
    }

    @Transactional
    public void saveRequestReturnForAmends(RequestTask requestTask, PmrvUser pmrvUser) {
        Request request = requestTask.getRequest();
        PermitIssuanceApplicationReviewRequestTaskPayload taskPayload =
            (PermitIssuanceApplicationReviewRequestTaskPayload) requestTask.getPayload();

        PermitIssuanceRequestPayload permitIssuanceRequestPayload =
            (PermitIssuanceRequestPayload) request.getPayload();

        updatePermitIssuanceRequestPayload(permitIssuanceRequestPayload, taskPayload, pmrvUser);

        requestService.saveRequest(request);
    }

    @Transactional
    public void saveRequestPeerReviewAction(RequestTask requestTask, String selectedPeerReview, PmrvUser pmrvUser) {
        Request request = requestTask.getRequest();
        PermitIssuanceApplicationReviewRequestTaskPayload taskPayload =
            (PermitIssuanceApplicationReviewRequestTaskPayload) requestTask.getPayload();

        PermitIssuanceRequestPayload permitIssuanceRequestPayload =
            (PermitIssuanceRequestPayload) request.getPayload();

        updatePermitIssuanceRequestPayload(permitIssuanceRequestPayload, taskPayload, pmrvUser);
        permitIssuanceRequestPayload.setRegulatorPeerReviewer(selectedPeerReview);

        requestService.saveRequest(request);
    }

    @Transactional
    public void amendPermit(PermitIssuanceSaveApplicationAmendRequestTaskActionPayload permitSaveApplicationAmendRequestTaskActionPayload,
        RequestTask requestTask) {
        PermitIssuanceApplicationAmendsSubmitRequestTaskPayload
            permitIssuanceApplicationAmendsSubmitRequestTaskPayload = (PermitIssuanceApplicationAmendsSubmitRequestTaskPayload) requestTask.getPayload();

		permitReviewService.cleanUpDeprecatedReviewGroupDecisions(
				permitIssuanceApplicationAmendsSubmitRequestTaskPayload,
				permitSaveApplicationAmendRequestTaskActionPayload.getPermit().getMonitoringApproaches()
						.getMonitoringApproaches().keySet());

        updatePermitIssuanceReviewRequestTaskPayload(permitIssuanceApplicationAmendsSubmitRequestTaskPayload,
            permitSaveApplicationAmendRequestTaskActionPayload);

        requestTaskService.saveRequestTask(requestTask);
    }

    @Transactional
    public void submitAmendedPermit(PermitIssuanceSubmitApplicationAmendRequestTaskActionPayload submitApplicationAmendRequestTaskActionPayload,
        RequestTask requestTask) {
        Request request = requestTask.getRequest();
        PermitIssuanceApplicationAmendsSubmitRequestTaskPayload permitIssuanceApplicationAmendsSubmitRequestTaskPayload =
            (PermitIssuanceApplicationAmendsSubmitRequestTaskPayload) requestTask.getPayload();

        PermitContainer permitContainer = permitMapper.toPermitContainer(permitIssuanceApplicationAmendsSubmitRequestTaskPayload);
        permitValidatorService.validatePermit(permitContainer);

        PermitIssuanceRequestPayload permitIssuanceRequestPayload = (PermitIssuanceRequestPayload) request.getPayload();
        permitIssuanceRequestPayload.setPermitType(permitIssuanceApplicationAmendsSubmitRequestTaskPayload.getPermitType());
        permitIssuanceRequestPayload.setPermit(permitIssuanceApplicationAmendsSubmitRequestTaskPayload.getPermit());
        permitIssuanceRequestPayload.setPermitAttachments(permitIssuanceApplicationAmendsSubmitRequestTaskPayload.getPermitAttachments());
        permitIssuanceRequestPayload.setReviewSectionsCompleted(permitIssuanceApplicationAmendsSubmitRequestTaskPayload.getReviewSectionsCompleted());
        permitIssuanceRequestPayload.setPermitSectionsCompleted(submitApplicationAmendRequestTaskActionPayload.getPermitSectionsCompleted());

        requestService.saveRequest(request);
    }

    private void updatePermitIssuanceRequestPayload(PermitIssuanceRequestPayload permitIssuanceRequestPayload,
        PermitIssuanceApplicationReviewRequestTaskPayload reviewRequestTaskPayload, PmrvUser pmrvUser) {
        permitIssuanceRequestPayload.setRegulatorReviewer(pmrvUser.getUserId());
        permitIssuanceRequestPayload.setPermitType(reviewRequestTaskPayload.getPermitType());
        permitIssuanceRequestPayload.setPermit(reviewRequestTaskPayload.getPermit());
        permitIssuanceRequestPayload.setPermitSectionsCompleted(reviewRequestTaskPayload.getPermitSectionsCompleted());
        permitIssuanceRequestPayload.setPermitAttachments(reviewRequestTaskPayload.getPermitAttachments());
        permitIssuanceRequestPayload.setReviewSectionsCompleted(reviewRequestTaskPayload.getReviewSectionsCompleted());
        permitIssuanceRequestPayload.setReviewGroupDecisions(reviewRequestTaskPayload.getReviewGroupDecisions());
        permitIssuanceRequestPayload.setReviewAttachments(reviewRequestTaskPayload.getReviewAttachments());
        permitIssuanceRequestPayload.setDetermination(reviewRequestTaskPayload.getDetermination());
    }

    private void updatePermitIssuanceReviewRequestTaskPayload(PermitIssuanceReviewRequestTaskPayload permitIssuanceReviewRequestTaskPayload,
        PermitIssuanceReviewRequestTaskActionPayload permitIssuanceReviewRequestTaskActionPayload) {
        permitIssuanceReviewRequestTaskPayload.setPermitType(permitIssuanceReviewRequestTaskActionPayload.getPermitType());
        permitIssuanceReviewRequestTaskPayload.setPermit(permitIssuanceReviewRequestTaskActionPayload.getPermit());
        permitIssuanceReviewRequestTaskPayload.setPermitSectionsCompleted(permitIssuanceReviewRequestTaskActionPayload.getPermitSectionsCompleted());
        permitIssuanceReviewRequestTaskPayload.setReviewSectionsCompleted(permitIssuanceReviewRequestTaskActionPayload.getReviewSectionsCompleted());
    }
}
