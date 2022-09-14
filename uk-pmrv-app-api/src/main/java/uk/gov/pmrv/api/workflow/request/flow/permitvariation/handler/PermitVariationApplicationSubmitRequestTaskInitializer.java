package uk.gov.pmrv.api.workflow.request.flow.permitvariation.handler;

import java.util.Set;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import uk.gov.pmrv.api.account.domain.installationoperatordetails.InstallationOperatorDetails;
import uk.gov.pmrv.api.permit.domain.PermitContainer;
import uk.gov.pmrv.api.permit.service.PermitQueryService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.core.service.InitializeRequestTaskHandler;
import uk.gov.pmrv.api.account.service.InstallationOperatorDetailsQueryService;
import uk.gov.pmrv.api.workflow.request.flow.permitvariation.domain.PermitVariationApplicationSubmitRequestTaskPayload;

@Service
@RequiredArgsConstructor
public class PermitVariationApplicationSubmitRequestTaskInitializer implements InitializeRequestTaskHandler {

	private final InstallationOperatorDetailsQueryService installationOperatorDetailsQueryService;
	private final PermitQueryService permitQueryService;
	@Override
	public RequestTaskPayload initializePayload(Request request) {
		final PermitContainer permitContainer = permitQueryService
				.getPermitContainerByAccountId(request.getAccountId());
		final InstallationOperatorDetails installationOperatorDetails = installationOperatorDetailsQueryService
				.getInstallationOperatorDetails(request.getAccountId());

		return PermitVariationApplicationSubmitRequestTaskPayload.builder()
                .payloadType(RequestTaskPayloadType.PERMIT_VARIATION_APPLICATION_SUBMIT_PAYLOAD)
                .installationOperatorDetails(installationOperatorDetails)
                .permitType(permitContainer.getPermitType())
                .permit(permitContainer.getPermit())
                .permitAttachments(permitContainer.getPermitAttachments())
                .build();
	}

	@Override
	public Set<RequestTaskType> getRequestTaskTypes() {
		return Set.of(RequestTaskType.PERMIT_VARIATION_OPERATOR_APPLICATION_SUBMIT,
				RequestTaskType.PERMIT_VARIATION_REGULATOR_APPLICATION_SUBMIT);
	}

}
