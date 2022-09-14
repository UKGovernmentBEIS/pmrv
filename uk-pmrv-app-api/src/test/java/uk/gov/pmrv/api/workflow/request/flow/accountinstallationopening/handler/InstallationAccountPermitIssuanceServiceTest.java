package uk.gov.pmrv.api.workflow.request.flow.accountinstallationopening.handler;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.common.domain.enumeration.CompetentAuthority;
import uk.gov.pmrv.api.workflow.request.StartProcessRequestService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestMetadataType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.accountinstallationopening.domain.AccountPayload;
import uk.gov.pmrv.api.workflow.request.flow.accountinstallationopening.domain.InstallationAccountOpeningRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestParams;
import uk.gov.pmrv.api.workflow.request.flow.permitissuance.common.domain.PermitIssuanceRequestMetadata;
import uk.gov.pmrv.api.workflow.request.flow.permitissuance.common.domain.PermitIssuanceRequestPayload;

@ExtendWith(MockitoExtension.class)
class InstallationAccountPermitIssuanceServiceTest {

    @InjectMocks
    private InstallationAccountPermitIssuanceService installationAccountPermitIssuanceService;

    @Mock
    private RequestService requestService;

    @Mock
    private StartProcessRequestService startProcessRequestService;

    @Test
    void executeTest() {
        final String requestId = "1";
        final Long accountId = 1L;
        final String operatorAssignee = "operatorAssignee";
        final Request existingRequest = Request.builder()
            .id(requestId)
            .accountId(accountId)
            .competentAuthority(CompetentAuthority.WALES)
            .type(RequestType.INSTALLATION_ACCOUNT_OPENING)
            .payload(InstallationAccountOpeningRequestPayload.builder()
                .payloadType(RequestPayloadType.INSTALLATION_ACCOUNT_OPENING_REQUEST_PAYLOAD)
                .accountPayload(AccountPayload.builder().build())
                .operatorAssignee("operatorAssignee")
                .regulatorAssignee(operatorAssignee)
                .build())
            .build();

        RequestParams requestParams = RequestParams.builder()
            .type(RequestType.PERMIT_ISSUANCE)
            .ca(existingRequest.getCompetentAuthority())
            .accountId(existingRequest.getAccountId())
            .verificationBodyId(existingRequest.getVerificationBodyId())
            .requestPayload(PermitIssuanceRequestPayload.builder()
                .payloadType(RequestPayloadType.PERMIT_ISSUANCE_REQUEST_PAYLOAD)
                .operatorAssignee(operatorAssignee)
                .build())
            .requestMetadata(PermitIssuanceRequestMetadata.builder()
                    .type(RequestMetadataType.PERMIT_ISSUANCE)
                    .build())
            .build();

        when(requestService.findRequestById(requestId)).thenReturn(existingRequest);

        // Invoke
        installationAccountPermitIssuanceService.execute(requestId);

        // Verify
        verify(requestService, times(1)).findRequestById(requestId);
        verify(startProcessRequestService, times(1)).startProcess(requestParams);
    }

}
