package uk.gov.pmrv.api.workflow.request.flow.permitissuance.review.handler;

import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.account.domain.installationoperatordetails.InstallationOperatorDetails;
import uk.gov.pmrv.api.account.service.InstallationOperatorDetailsQueryService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.core.service.InitializeRequestTaskHandler;
import uk.gov.pmrv.api.workflow.request.flow.permitissuance.common.domain.PermitIssuanceRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitissuance.review.domain.PermitIssuanceApplicationReviewRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitissuance.review.mapper.PermitReviewMapper;

@Service
@RequiredArgsConstructor
public class PermitIssuanceApplicationPeerReviewInitializer implements InitializeRequestTaskHandler {

    private static final PermitReviewMapper PERMIT_REVIEW_MAPPER = Mappers.getMapper(PermitReviewMapper.class);

    private final InstallationOperatorDetailsQueryService installationOperatorDetailsQueryService;

    @Override
    public RequestTaskPayload initializePayload(Request request) {
        InstallationOperatorDetails installationOperatorDetails = installationOperatorDetailsQueryService.getInstallationOperatorDetails(
            request.getAccountId());
        PermitIssuanceApplicationReviewRequestTaskPayload permitIssuanceApplicationReviewRequestTaskPayload =
            PERMIT_REVIEW_MAPPER.toPermitIssuanceApplicationReviewRequestTaskPayload((PermitIssuanceRequestPayload) request.getPayload(),
                RequestTaskPayloadType.PERMIT_ISSUANCE_APPLICATION_PEER_REVIEW_PAYLOAD
            );
        permitIssuanceApplicationReviewRequestTaskPayload.setInstallationOperatorDetails(installationOperatorDetails);
        return permitIssuanceApplicationReviewRequestTaskPayload;
    }

    @Override
    public Set<RequestTaskType> getRequestTaskTypes() {
        return Set.of(RequestTaskType.PERMIT_ISSUANCE_APPLICATION_PEER_REVIEW);
    }
}
