package uk.gov.pmrv.api.workflow.request.flow.permitsurrender.service;

import lombok.RequiredArgsConstructor;

import java.time.LocalDate;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uk.gov.pmrv.api.common.domain.model.PmrvUser;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.DecisionNotification;
import uk.gov.pmrv.api.workflow.request.flow.permitsurrender.domain.PermitSurrenderApplicationReviewRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitsurrender.domain.PermitSurrenderRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitsurrender.domain.PermitSurrenderReviewDecision;
import uk.gov.pmrv.api.workflow.request.flow.permitsurrender.domain.PermitSurrenderReviewDetermination;
import uk.gov.pmrv.api.workflow.request.flow.permitsurrender.domain.PermitSurrenderSaveReviewDeterminationRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitsurrender.domain.PermitSurrenderSaveReviewGroupDecisionRequestTaskActionPayload;

@Service
@RequiredArgsConstructor
public class RequestPermitSurrenderReviewService {
    
    private final RequestService requestService;
    private final RequestTaskService requestTaskService;
    private final PermitSurrenderReviewDeterminationHandlerService determinationHandlerService;

    @Transactional
    public void saveReviewDecision(PermitSurrenderSaveReviewGroupDecisionRequestTaskActionPayload taskActionPayload,
            RequestTask requestTask) {
        final PermitSurrenderReviewDecision reviewDecision = taskActionPayload.getReviewDecision();

        final PermitSurrenderApplicationReviewRequestTaskPayload taskPayload = 
            (PermitSurrenderApplicationReviewRequestTaskPayload) requestTask.getPayload();
        
        taskPayload.setReviewDecision(reviewDecision);
        taskPayload.setReviewDeterminationCompleted(taskActionPayload.getReviewDeterminationCompleted());
        
        PermitSurrenderReviewDetermination reviewDetermination = taskPayload.getReviewDetermination();
        if(reviewDetermination != null) {
            determinationHandlerService.handleDeterminationUponDecision(reviewDetermination.getType(), taskPayload, reviewDecision);    
        }
        
        requestTaskService.saveRequestTask(requestTask);
    }
    
    @Transactional
    public void saveReviewDetermination(PermitSurrenderSaveReviewDeterminationRequestTaskActionPayload taskActionPayload, 
            RequestTask requestTask) {
        final PermitSurrenderApplicationReviewRequestTaskPayload taskPayload = 
                (PermitSurrenderApplicationReviewRequestTaskPayload) requestTask.getPayload();

        determinationHandlerService.validateDecisionUponDetermination(taskPayload, taskActionPayload.getReviewDetermination());
        
        taskPayload.setReviewDetermination(taskActionPayload.getReviewDetermination());
        taskPayload.setReviewDeterminationCompleted(taskActionPayload.getReviewDeterminationCompleted());
        
        requestTaskService.saveRequestTask(requestTask);
    }

    @Transactional
    public void saveRequestPeerReviewAction(RequestTask requestTask, String selectedPeerReviewer, String regulatorReviewer) {
        Request request = requestTask.getRequest();
        PermitSurrenderApplicationReviewRequestTaskPayload taskPayload =
            (PermitSurrenderApplicationReviewRequestTaskPayload) requestTask.getPayload();

        PermitSurrenderRequestPayload permitSurrenderRequestPayload =
            (PermitSurrenderRequestPayload) request.getPayload();

        permitSurrenderRequestPayload.setRegulatorReviewer(regulatorReviewer);
        permitSurrenderRequestPayload.setRegulatorPeerReviewer(selectedPeerReviewer);
        permitSurrenderRequestPayload.setReviewDecision(taskPayload.getReviewDecision());
        permitSurrenderRequestPayload.setReviewDetermination(taskPayload.getReviewDetermination());
        permitSurrenderRequestPayload.setReviewDeterminationCompleted(taskPayload.getReviewDeterminationCompleted());

        requestService.saveRequest(request);
    }
    
    @Transactional
    public void saveReviewDecisionNotification(final RequestTask requestTask, final DecisionNotification decisionNotification,
            final PmrvUser reviewer) {
        final Request request = requestTask.getRequest();
        final PermitSurrenderRequestPayload requestPayload = (PermitSurrenderRequestPayload) request.getPayload();
        final PermitSurrenderApplicationReviewRequestTaskPayload taskPayload = 
                (PermitSurrenderApplicationReviewRequestTaskPayload) requestTask.getPayload();
        
        requestPayload.setReviewDecision(taskPayload.getReviewDecision());
        requestPayload.setReviewDetermination(taskPayload.getReviewDetermination());
        requestPayload.setReviewDeterminationCompletedDate(LocalDate.now());
        requestPayload.setReviewDecisionNotification(decisionNotification);
        requestPayload.setRegulatorReviewer(reviewer.getUserId());
        requestService.saveRequest(request);
    }

}
