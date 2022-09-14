package uk.gov.pmrv.api.workflow.request.flow.permitvariation.handler;

import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import uk.gov.pmrv.api.account.service.InstallationOperatorDetailsQueryService;
import uk.gov.pmrv.api.permit.domain.PermitContainer;
import uk.gov.pmrv.api.permit.service.PermitQueryService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.core.service.InitializeRequestTaskHandler;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.ReviewDecisionType;
import uk.gov.pmrv.api.workflow.request.flow.common.utils.PermitReviewUtils;
import uk.gov.pmrv.api.workflow.request.flow.permitvariation.domain.PermitVariationApplicationReviewRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitvariation.domain.PermitVariationRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitvariation.domain.PermitVariationReviewDecision;
import uk.gov.pmrv.api.workflow.request.flow.permitvariation.mapper.PermitVariationMapper;

@Service
@RequiredArgsConstructor
public class PermitVariationApplicationReviewRequestTaskInitializer implements InitializeRequestTaskHandler {
	
	private final InstallationOperatorDetailsQueryService installationOperatorDetailsQueryService;
	private final PermitQueryService permitQueryService;
	private static final PermitVariationMapper permitVariationMapper = Mappers.getMapper(PermitVariationMapper.class);
	
	@Override
	public RequestTaskPayload initializePayload(Request request) {
		final PermitVariationRequestPayload requestPayload = (PermitVariationRequestPayload) request.getPayload();
		final PermitContainer originalPermitContainer = permitQueryService.getPermitContainerByAccountId(request.getAccountId());
		final PermitVariationApplicationReviewRequestTaskPayload requestTaskPayload = permitVariationMapper.toPermitVariationApplicationReviewRequestTaskPayload(requestPayload);
		
		requestTaskPayload.setInstallationOperatorDetails(installationOperatorDetailsQueryService.getInstallationOperatorDetails(request.getAccountId()));
		requestTaskPayload.setOriginalPermitContainer(originalPermitContainer);
		requestTaskPayload.setReviewGroupDecisions(PermitReviewUtils.getPermitReviewGroups(requestPayload.getPermit())
				.stream().collect(Collectors.toMap(Function.identity(), (reviewGroup) -> PermitVariationReviewDecision
						.builder().type(ReviewDecisionType.ACCEPTED).build())));
		
		return requestTaskPayload;
	}

	@Override
	public Set<RequestTaskType> getRequestTaskTypes() {
		return Set.of(RequestTaskType.PERMIT_VARIATION_APPLICATION_REVIEW);
	}

}
