package uk.gov.pmrv.api.workflow.request.flow.permitrevocation.service;

import lombok.RequiredArgsConstructor;

import java.time.LocalDate;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.flow.permitrevocation.domain.PermitRevocationApplicationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitrevocation.domain.PermitRevocationApplicationWithdrawRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitrevocation.domain.PermitRevocationRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitrevocation.domain.PermitRevocationSaveApplicationRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitrevocation.domain.PermitRevocationWaitForAppealRequestTaskPayload;

@Service
@RequiredArgsConstructor
public class RequestPermitRevocationService {

    @Transactional
    public void applySavePayload(final PermitRevocationSaveApplicationRequestTaskActionPayload actionPayload,
                                 final RequestTask requestTask) {

        final PermitRevocationApplicationSubmitRequestTaskPayload taskPayload =
            (PermitRevocationApplicationSubmitRequestTaskPayload) requestTask.getPayload();
        
        taskPayload.setSectionsCompleted(actionPayload.getSectionsCompleted());
        taskPayload.setPermitRevocation(actionPayload.getPermitRevocation());
    }

    @Transactional
    public void requestPeerReview(final RequestTask requestTask,
                                  final String selectedPeerReviewer,
                                  final String regulatorReviewer) {

        final PermitRevocationApplicationSubmitRequestTaskPayload taskPayload =
            (PermitRevocationApplicationSubmitRequestTaskPayload) requestTask.getPayload();
        final Request request = requestTask.getRequest();
        final PermitRevocationRequestPayload requestPayload = (PermitRevocationRequestPayload) request.getPayload();

        requestPayload.setPermitRevocation(taskPayload.getPermitRevocation());
        requestPayload.setSectionsCompleted(taskPayload.getSectionsCompleted());
        requestPayload.setRegulatorReviewer(regulatorReviewer);
        requestPayload.setRegulatorPeerReviewer(selectedPeerReviewer);
    }

    @Transactional
    public void applyWithdrawPayload(final PermitRevocationApplicationWithdrawRequestTaskActionPayload actionPayload,
                                     final RequestTask requestTask) {

        final PermitRevocationWaitForAppealRequestTaskPayload taskPayload =
            (PermitRevocationWaitForAppealRequestTaskPayload) requestTask.getPayload();
        final PermitRevocationRequestPayload requestPayload = (PermitRevocationRequestPayload) requestTask.getRequest().getPayload();

        taskPayload.setReason(actionPayload.getReason());
        taskPayload.setWithdrawFiles(actionPayload.getFiles());
        requestPayload.setWithdrawCompletedDate(LocalDate.now());
    }
}
