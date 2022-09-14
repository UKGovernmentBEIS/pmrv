package uk.gov.pmrv.api.workflow.request.flow.common.service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.account.domain.enumeration.AccountStatus;
import uk.gov.pmrv.api.account.service.AccountQueryService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestQueryService;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestCreateValidationResult;

@Service
@RequiredArgsConstructor
public abstract class RequestCreateAbstractValidator implements RequestCreateValidator {

    private final AccountQueryService accountQueryService;
    private final RequestQueryService requestQueryService;

    @Override
    public RequestCreateValidationResult validateAction(final Long accountId) {
        
        final RequestCreateValidationResult validationResult = RequestCreateValidationResult.builder().valid(true).build();
        final AccountStatus accountStatus = accountQueryService.getAccountStatus(accountId);

        final Set<AccountStatus> applicableAccountStatuses = this.getApplicableAccountStatuses();
        final boolean validAccountStatus = applicableAccountStatuses.isEmpty() || 
                                           applicableAccountStatuses.contains(accountStatus);
        if (!validAccountStatus) {
            validationResult.setValid(false);
            validationResult.setReportedAccountStatus(accountStatus);
            validationResult.setApplicableAccountStatuses(applicableAccountStatuses);
        }

        if(!this.getMutuallyExclusiveRequests().isEmpty()){
            final List<Request> openRequests = requestQueryService.findOpenRequestsByAccount(accountId);
            final Set<RequestType> conflictingRequests = openRequests.stream()
                    .map(Request::getType)
                    .filter(e -> this.getMutuallyExclusiveRequests().contains(e))
                    .collect(Collectors.toSet());

            if (!conflictingRequests.isEmpty()) {
                validationResult.setValid(false);
                validationResult.setReportedRequestTypes(conflictingRequests);
            }
        }
        
        return validationResult;
    }

    protected abstract Set<AccountStatus> getApplicableAccountStatuses();

    protected abstract Set<RequestType> getMutuallyExclusiveRequests();
}
