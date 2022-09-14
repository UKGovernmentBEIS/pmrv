package uk.gov.pmrv.api.workflow.request.flow.accountinstallationopening.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.StartProcessRequestService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestPayload;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestMetadataType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestParams;
import uk.gov.pmrv.api.workflow.request.flow.permitissuance.common.domain.PermitIssuanceRequestMetadata;
import uk.gov.pmrv.api.workflow.request.flow.permitissuance.common.domain.PermitIssuanceRequestPayload;

/**
 * Service for starting PERMIT_ISSUANCE workflow process.
 */
@Service
@RequiredArgsConstructor
public class InstallationAccountPermitIssuanceService {

    private final RequestService requestService;
    private final StartProcessRequestService startProcessRequestService;

    public void execute(String requestId) {
        Request instAccOpeningRequest = requestService.findRequestById(requestId);

        RequestParams requestParams = RequestParams.builder()
                .type(RequestType.PERMIT_ISSUANCE)
                .ca(instAccOpeningRequest.getCompetentAuthority())
                .accountId(instAccOpeningRequest.getAccountId())
                .verificationBodyId(instAccOpeningRequest.getVerificationBodyId())
                .requestPayload(initializePermitIssuanceRequestPayload(instAccOpeningRequest.getPayload()))
                .requestMetadata(PermitIssuanceRequestMetadata.builder()
                        .type(RequestMetadataType.PERMIT_ISSUANCE)
                        .build())
            .build();
        startProcessRequestService.startProcess(requestParams);
    }

    private PermitIssuanceRequestPayload initializePermitIssuanceRequestPayload(RequestPayload existingRequestPayload) {
        return PermitIssuanceRequestPayload.builder()
            .payloadType(RequestPayloadType.PERMIT_ISSUANCE_REQUEST_PAYLOAD)
            .operatorAssignee(existingRequestPayload.getOperatorAssignee())
            .build();
    }
}
