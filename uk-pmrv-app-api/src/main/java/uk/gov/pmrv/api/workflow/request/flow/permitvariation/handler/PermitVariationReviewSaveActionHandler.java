package uk.gov.pmrv.api.workflow.request.flow.permitvariation.handler;

import java.util.List;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import uk.gov.pmrv.api.common.domain.model.PmrvUser;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.common.actionhandler.RequestTaskActionHandler;
import uk.gov.pmrv.api.workflow.request.flow.permitvariation.domain.PermitVariationSaveApplicationReviewRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitvariation.service.PermitVariationReviewService;

@Component
@RequiredArgsConstructor
public class PermitVariationReviewSaveActionHandler
		implements RequestTaskActionHandler<PermitVariationSaveApplicationReviewRequestTaskActionPayload> {

	private final RequestTaskService requestTaskService;
	private final PermitVariationReviewService permitVariationReviewService;
	
	@Override
	public void process(Long requestTaskId, RequestTaskActionType requestTaskActionType, PmrvUser pmrvUser,
			PermitVariationSaveApplicationReviewRequestTaskActionPayload payload) {
		final RequestTask requestTask = requestTaskService.findTaskById(requestTaskId);
		permitVariationReviewService.savePermitVariation(payload, requestTask);
	}

	@Override
	public List<RequestTaskActionType> getTypes() {
		return List.of(RequestTaskActionType.PERMIT_VARIATION_SAVE_APPLICATION_REVIEW);
	}

}
