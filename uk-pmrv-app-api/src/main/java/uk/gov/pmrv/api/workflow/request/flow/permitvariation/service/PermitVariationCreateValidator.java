package uk.gov.pmrv.api.workflow.request.flow.permitvariation.service;

import java.util.Set;

import org.springframework.stereotype.Service;

import uk.gov.pmrv.api.account.domain.enumeration.AccountStatus;
import uk.gov.pmrv.api.account.service.AccountQueryService;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestCreateActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestQueryService;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestCreateAbstractValidator;

@Service
public class PermitVariationCreateValidator extends RequestCreateAbstractValidator {

	public PermitVariationCreateValidator(AccountQueryService accountQueryService,
			RequestQueryService requestQueryService) {
		super(accountQueryService, requestQueryService);
	}

	@Override
	public RequestCreateActionType getType() {
		return RequestCreateActionType.PERMIT_VARIATION;
	}

	@Override
	protected Set<AccountStatus> getApplicableAccountStatuses() {
		return Set.of(AccountStatus.LIVE);
	}

	@Override
	protected Set<RequestType> getMutuallyExclusiveRequests() {
		return Set.of(RequestType.PERMIT_VARIATION, RequestType.PERMIT_TRANSFER, RequestType.PERMIT_SURRENDER);
	}

}
