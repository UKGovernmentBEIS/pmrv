package uk.gov.pmrv.api.workflow.request.flow.permitnotification.service;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uk.gov.pmrv.api.common.domain.model.PmrvUser;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.permitnotification.domain.PermitNotificationApplicationReviewRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitnotification.domain.PermitNotificationRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitnotification.domain.PermitNotificationReviewDecision;
import uk.gov.pmrv.api.workflow.request.flow.permitnotification.domain.PermitNotificationSaveReviewGroupDecisionRequestTaskActionPayload;

@Service
@RequiredArgsConstructor
public class RequestPermitNotificationReviewService {

    private final RequestTaskService requestTaskService;

    @Transactional
    public void saveReviewDecision(PermitNotificationSaveReviewGroupDecisionRequestTaskActionPayload taskActionPayload,
                                   RequestTask requestTask) {
        final PermitNotificationReviewDecision reviewDecision = taskActionPayload.getReviewDecision();

        final PermitNotificationApplicationReviewRequestTaskPayload taskPayload =
                (PermitNotificationApplicationReviewRequestTaskPayload) requestTask.getPayload();

        taskPayload.setReviewDecision(reviewDecision);

        requestTaskService.saveRequestTask(requestTask);
    }

    @Transactional
    public void saveRequestPeerReviewAction(RequestTask requestTask, String selectedPeerReview, PmrvUser pmrvUser) {
        final Request request = requestTask.getRequest();
        final PermitNotificationApplicationReviewRequestTaskPayload taskPayload =
                (PermitNotificationApplicationReviewRequestTaskPayload) requestTask.getPayload();
        final PermitNotificationRequestPayload requestPayload = (PermitNotificationRequestPayload) request.getPayload();

        requestPayload.setReviewDecision(taskPayload.getReviewDecision());
        requestPayload.setRegulatorReviewer(pmrvUser.getUserId());
        requestPayload.setRegulatorPeerReviewer(selectedPeerReview);
    }
}
