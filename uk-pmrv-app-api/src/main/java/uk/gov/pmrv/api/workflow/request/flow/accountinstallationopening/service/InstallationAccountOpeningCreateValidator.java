package uk.gov.pmrv.api.workflow.request.flow.accountinstallationopening.service;

import java.util.Set;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.account.domain.enumeration.AccountStatus;
import uk.gov.pmrv.api.account.service.AccountQueryService;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestCreateActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestQueryService;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestCreateValidationResult;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestCreateAbstractValidator;

@Service
public class InstallationAccountOpeningCreateValidator extends RequestCreateAbstractValidator {

    public InstallationAccountOpeningCreateValidator(final AccountQueryService accountQueryService,
                                                     final RequestQueryService requestQueryService) {
        super(accountQueryService, requestQueryService);
    }

    @Override
    public RequestCreateValidationResult validateAction(Long accountId) {
        return RequestCreateValidationResult.builder().valid(accountId == null).build();
    }

    @Override
    public RequestCreateActionType getType() {
        return RequestCreateActionType.INSTALLATION_ACCOUNT_OPENING_SUBMIT_APPLICATION;
    }

    @Override
    public Set<AccountStatus> getApplicableAccountStatuses() {
        return Set.of();
    }

    @Override
    public Set<RequestType> getMutuallyExclusiveRequests() {
        return Set.of();
    }

}
