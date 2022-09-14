package uk.gov.pmrv.api.workflow.request.flow.permitvariation.service;

import java.util.Map;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.permit.PermitReviewGroup;
import uk.gov.pmrv.api.workflow.request.flow.common.service.permit.PermitReviewService;
import uk.gov.pmrv.api.workflow.request.flow.permitvariation.domain.PermitVariationApplicationReviewRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitvariation.domain.PermitVariationReviewDecision;
import uk.gov.pmrv.api.workflow.request.flow.permitvariation.domain.PermitVariationSaveApplicationReviewRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitvariation.domain.PermitVariationSaveReviewGroupDecisionRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitvariation.domain.PermitVariationSaveDetailsReviewGroupDecisionRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitvariation.domain.PermitVariationSaveReviewDeterminationRequestTaskActionPayload;

@Service
@RequiredArgsConstructor
public class PermitVariationReviewService {
	
	private final RequestTaskService requestTaskService;
	private final PermitReviewService permitReviewService;

	@Transactional
    public void savePermitVariation(PermitVariationSaveApplicationReviewRequestTaskActionPayload taskActionPayload,
                           RequestTask requestTask) {
        PermitVariationApplicationReviewRequestTaskPayload
            requestTaskPayload = (PermitVariationApplicationReviewRequestTaskPayload) requestTask.getPayload();
        
        permitReviewService.cleanUpDeprecatedReviewGroupDecisions(requestTaskPayload,
        		taskActionPayload.getPermit().getMonitoringApproaches().getMonitoringApproaches().keySet());
        
        permitReviewService.resetDeterminationIfNotDeemedWithdrawn(requestTaskPayload);

        requestTaskPayload.setPermitVariationDetails(taskActionPayload.getPermitVariationDetails());
        requestTaskPayload.setPermitVariationDetailsCompleted(taskActionPayload.getPermitVariationDetailsCompleted());
        requestTaskPayload.setPermit(taskActionPayload.getPermit());
        requestTaskPayload.setPermitSectionsCompleted(taskActionPayload.getPermitSectionsCompleted());
        requestTaskPayload.setReviewSectionsCompleted(taskActionPayload.getReviewSectionsCompleted());
        requestTaskPayload.setPermitVariationDetailsReviewCompleted(taskActionPayload.getPermitVariationDetailsReviewCompleted());

        requestTaskService.saveRequestTask(requestTask);
    }

    @Transactional
    public void saveReviewGroupDecision(PermitVariationSaveReviewGroupDecisionRequestTaskActionPayload payload, RequestTask requestTask) {
        PermitVariationApplicationReviewRequestTaskPayload
            taskPayload = (PermitVariationApplicationReviewRequestTaskPayload) requestTask.getPayload();

        final PermitReviewGroup group = payload.getGroup();
        final PermitVariationReviewDecision decision = payload.getDecision();

        Map<PermitReviewGroup, PermitVariationReviewDecision> reviewGroupDecisions = taskPayload.getReviewGroupDecisions();
        reviewGroupDecisions.put(group, decision);
        taskPayload.setReviewSectionsCompleted(payload.getReviewSectionsCompleted());
        
        permitReviewService.resetDeterminationIfNotValidWithDecisions(taskPayload, requestTask.getRequest().getType());
    }

    @Transactional
    public void saveDetailsReviewGroupDecision(PermitVariationSaveDetailsReviewGroupDecisionRequestTaskActionPayload payload, RequestTask requestTask) {
        PermitVariationApplicationReviewRequestTaskPayload
            taskPayload = (PermitVariationApplicationReviewRequestTaskPayload) requestTask.getPayload();

        taskPayload.setPermitVariationDetailsReviewDecision(payload.getDecision());
        taskPayload.setReviewSectionsCompleted(payload.getReviewSectionsCompleted());
        taskPayload.setPermitVariationDetailsReviewCompleted(payload.getPermitVariationDetailsReviewCompleted());
        
        permitReviewService.resetDeterminationIfNotValidWithDecisions(taskPayload, requestTask.getRequest().getType());
    }
    
	@Transactional
	public void saveDetermination(final PermitVariationSaveReviewDeterminationRequestTaskActionPayload payload,
			final RequestTask requestTask) {
		final PermitVariationApplicationReviewRequestTaskPayload taskPayload = (PermitVariationApplicationReviewRequestTaskPayload) requestTask
				.getPayload();

		taskPayload.setDetermination(payload.getDetermination());
		taskPayload.setReviewSectionsCompleted(payload.getReviewSectionsCompleted());
	}
	
}
