package uk.gov.pmrv.api.workflow.request.flow.permitsurrender.service;

import java.util.Set;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.account.domain.enumeration.AccountStatus;
import uk.gov.pmrv.api.account.service.AccountQueryService;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestCreateActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestQueryService;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestCreateAbstractValidator;

@Service
public class PermitSurrenderCreateValidator extends RequestCreateAbstractValidator {

    public PermitSurrenderCreateValidator(final AccountQueryService accountQueryService,
                                          final RequestQueryService requestQueryService) {
        super(accountQueryService, requestQueryService);
    }

    @Override
    public RequestCreateActionType getType() {
        return RequestCreateActionType.PERMIT_SURRENDER;
    }

    @Override
    public Set<AccountStatus> getApplicableAccountStatuses() {
        return Set.of(AccountStatus.LIVE);
    }
    
    @Override
    public Set<RequestType> getMutuallyExclusiveRequests() {
        return Set.of(RequestType.PERMIT_SURRENDER, RequestType.PERMIT_VARIATION, RequestType.PERMIT_TRANSFER);
    }

}
