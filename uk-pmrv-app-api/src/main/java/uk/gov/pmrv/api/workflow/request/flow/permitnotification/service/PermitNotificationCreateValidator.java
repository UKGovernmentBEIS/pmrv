package uk.gov.pmrv.api.workflow.request.flow.permitnotification.service;

import org.springframework.stereotype.Service;

import uk.gov.pmrv.api.account.domain.enumeration.AccountStatus;
import uk.gov.pmrv.api.account.service.AccountQueryService;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestCreateActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestQueryService;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestCreateAbstractValidator;

import java.util.Set;

@Service
public class PermitNotificationCreateValidator extends RequestCreateAbstractValidator {

    public PermitNotificationCreateValidator(final AccountQueryService accountQueryService,
                                          final RequestQueryService requestQueryService) {
        super(accountQueryService, requestQueryService);
    }

    @Override
    protected Set<AccountStatus> getApplicableAccountStatuses() {
        return Set.of(
                AccountStatus.LIVE,
                AccountStatus.AWAITING_SURRENDER,
                AccountStatus.SURRENDERED,
                AccountStatus.AWAITING_REVOCATION,
                AccountStatus.REVOKED,
                AccountStatus.AWAITING_TRANSFER,
                AccountStatus.TRANSFERRED,
                AccountStatus.RATIONALISED,
                AccountStatus.AWAITING_RATIONALISATION);
    }

    @Override
    protected Set<RequestType> getMutuallyExclusiveRequests() {
        return  Set.of();
    }

    @Override
    public RequestCreateActionType getType() {
        return RequestCreateActionType.PERMIT_NOTIFICATION;
    }
}
