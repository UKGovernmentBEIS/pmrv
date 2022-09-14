package uk.gov.pmrv.api.workflow.request.flow.aer.service;

import org.springframework.stereotype.Service;

import uk.gov.pmrv.api.account.domain.enumeration.AccountStatus;
import uk.gov.pmrv.api.account.service.AccountQueryService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestCreateActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestMetadataType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestQueryService;
import uk.gov.pmrv.api.workflow.request.flow.aer.domain.AerRequestMetadata;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestCreateValidationResult;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestParams;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestCreateAbstractValidator;

import java.time.Year;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AerCreateValidatorService extends RequestCreateAbstractValidator {

    private RequestQueryService requestQueryService;
    private AerRequestIdGenerator aerRequestIdGenerator;

    public AerCreateValidatorService(final AccountQueryService accountQueryService,
                                     final RequestQueryService requestQueryService,
                                     final AerRequestIdGenerator aerRequestIdGenerator) {
        super(accountQueryService, requestQueryService);
        this.requestQueryService = requestQueryService;
        this.aerRequestIdGenerator = aerRequestIdGenerator;
    }

    public RequestCreateValidationResult validate(Long accountId, Year year) {
        // Validate account status
        RequestCreateValidationResult validationResult = this.validateAction(accountId);

        // Validate AERs with same year
        RequestParams params = RequestParams.builder()
                .accountId(accountId)
                .requestMetadata(AerRequestMetadata.builder().type(RequestMetadataType.AER).year(year).build())
                .build();
        String requestId = aerRequestIdGenerator.generate(params);
        Set<RequestType> conflictingRequests = requestQueryService
                .findOpenRequestsByAccount(accountId).stream()
                .filter(request -> request.getId().equals(requestId))
                .map(Request::getType)
                .collect(Collectors.toSet());

        if (!conflictingRequests.isEmpty()) {
            validationResult.setValid(false);
            validationResult.setReportedRequestTypes(conflictingRequests);
        }

        return validationResult;
    }

    @Override
    protected Set<AccountStatus> getApplicableAccountStatuses() {
        return Set.of(AccountStatus.LIVE);
    }

    @Override
    protected Set<RequestType> getMutuallyExclusiveRequests() {
        return Set.of();
    }

    @Override
    public RequestCreateActionType getType() {
        return RequestCreateActionType.AER;
    }
}
