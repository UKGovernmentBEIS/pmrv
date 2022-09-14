package uk.gov.pmrv.api.workflow.request.flow.aer.service;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.aer.domain.AerApplicationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.aer.domain.AerSaveApplicationRequestTaskActionPayload;

@Service
@RequiredArgsConstructor
public class RequestAerApplyService {

    private final RequestTaskService requestTaskService;

    @Transactional
    public void applySaveAction(
            AerSaveApplicationRequestTaskActionPayload taskActionPayload, RequestTask requestTask) {
        AerApplicationSubmitRequestTaskPayload taskPayload = (AerApplicationSubmitRequestTaskPayload) requestTask.getPayload();
        taskPayload.setAer(taskActionPayload.getAer());
        taskPayload.setAerSectionsCompleted(taskActionPayload.getAerSectionsCompleted());

        requestTaskService.saveRequestTask(requestTask);
    }
}
