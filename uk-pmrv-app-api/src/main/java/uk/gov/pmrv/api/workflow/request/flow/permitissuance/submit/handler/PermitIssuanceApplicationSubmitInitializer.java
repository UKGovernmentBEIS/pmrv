package uk.gov.pmrv.api.workflow.request.flow.permitissuance.submit.handler;

import java.util.Set;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import uk.gov.pmrv.api.account.domain.installationoperatordetails.InstallationOperatorDetails;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.core.service.InitializeRequestTaskHandler;
import uk.gov.pmrv.api.account.service.InstallationOperatorDetailsQueryService;
import uk.gov.pmrv.api.workflow.request.flow.permitissuance.submit.domain.PermitIssuanceApplicationSubmitRequestTaskPayload;

/**
 * Handler in permit issuance workflow for initializing installation operator details from installation account.
 */
@Service
@RequiredArgsConstructor
public class PermitIssuanceApplicationSubmitInitializer implements InitializeRequestTaskHandler {

	private final InstallationOperatorDetailsQueryService installationOperatorDetailsQueryService;

    @Override
    public RequestTaskPayload initializePayload(Request request) {
        InstallationOperatorDetails operatorDetailsSection = installationOperatorDetailsQueryService
                .getInstallationOperatorDetails(request.getAccountId());

        return PermitIssuanceApplicationSubmitRequestTaskPayload.builder()
                .payloadType(RequestTaskPayloadType.PERMIT_ISSUANCE_APPLICATION_SUBMIT_PAYLOAD)
                .installationOperatorDetails(operatorDetailsSection)
                .build();
    }

    @Override
    public Set<RequestTaskType> getRequestTaskTypes() {
        return Set.of(RequestTaskType.PERMIT_ISSUANCE_APPLICATION_SUBMIT);
    }
}
