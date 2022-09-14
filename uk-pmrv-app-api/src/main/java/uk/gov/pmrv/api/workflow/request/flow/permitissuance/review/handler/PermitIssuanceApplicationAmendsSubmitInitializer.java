package uk.gov.pmrv.api.workflow.request.flow.permitissuance.review.handler;

import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.account.domain.installationoperatordetails.InstallationOperatorDetails;
import uk.gov.pmrv.api.account.service.InstallationOperatorDetailsQueryService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.core.service.InitializeRequestTaskHandler;
import uk.gov.pmrv.api.workflow.request.flow.permitissuance.common.domain.PermitIssuanceRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitissuance.review.domain.PermitIssuanceApplicationAmendsSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitissuance.review.mapper.PermitReviewMapper;

@Service
@RequiredArgsConstructor
public class PermitIssuanceApplicationAmendsSubmitInitializer implements InitializeRequestTaskHandler {

    private static final PermitReviewMapper permitReviewMapper = Mappers.getMapper(PermitReviewMapper.class);

    private final InstallationOperatorDetailsQueryService installationOperatorDetailsQueryService;

    @Override
    public RequestTaskPayload initializePayload(Request request) {
        InstallationOperatorDetails installationOperatorDetails = installationOperatorDetailsQueryService.getInstallationOperatorDetails(
            request.getAccountId());

        PermitIssuanceApplicationAmendsSubmitRequestTaskPayload permitIssuanceApplicationAmendsSubmitRequestTaskPayload =
            permitReviewMapper.toPermitIssuanceApplicationAmendsSubmitRequestTaskPayload((PermitIssuanceRequestPayload) request.getPayload());
        permitIssuanceApplicationAmendsSubmitRequestTaskPayload.setInstallationOperatorDetails(installationOperatorDetails);
        return permitIssuanceApplicationAmendsSubmitRequestTaskPayload;
    }

    @Override
    public Set<RequestTaskType> getRequestTaskTypes() {
        return Set.of(RequestTaskType.PERMIT_ISSUANCE_APPLICATION_AMENDS_SUBMIT);
    }
}
