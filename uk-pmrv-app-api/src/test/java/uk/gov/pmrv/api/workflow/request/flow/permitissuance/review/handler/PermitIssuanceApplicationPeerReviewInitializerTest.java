package uk.gov.pmrv.api.workflow.request.flow.permitissuance.review.handler;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.pmrv.api.account.domain.installationoperatordetails.InstallationOperatorDetails;
import uk.gov.pmrv.api.account.service.InstallationOperatorDetailsQueryService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestStatus;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.flow.permitissuance.common.domain.PermitIssuanceRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitissuance.review.domain.PermitIssuanceApplicationReviewRequestTaskPayload;

@ExtendWith(MockitoExtension.class)
class PermitIssuanceApplicationPeerReviewInitializerTest {

    @InjectMocks
    private PermitIssuanceApplicationPeerReviewInitializer initializer;

    @Mock
    private InstallationOperatorDetailsQueryService installationOperatorDetailsQueryService;

    @Test
    void initializePayload() {
        Request request = Request.builder()
            .id("1")
            .accountId(1L)
            .type(RequestType.PERMIT_ISSUANCE)
            .status(RequestStatus.IN_PROGRESS)
            .payload(PermitIssuanceRequestPayload.builder()
                .payloadType(RequestPayloadType.PERMIT_ISSUANCE_REQUEST_PAYLOAD)
                .build())
            .build();

        InstallationOperatorDetails expectedInstallationOperatorDetails = InstallationOperatorDetails.builder().installationName("sample").build();

        Mockito.when(installationOperatorDetailsQueryService.getInstallationOperatorDetails(request.getAccountId()))
            .thenReturn(expectedInstallationOperatorDetails);

        RequestTaskPayload requestTaskPayload = initializer.initializePayload(request);
        assertThat(requestTaskPayload.getPayloadType()).isEqualTo(RequestTaskPayloadType.PERMIT_ISSUANCE_APPLICATION_PEER_REVIEW_PAYLOAD);
        assertThat(requestTaskPayload).isInstanceOf(PermitIssuanceApplicationReviewRequestTaskPayload.class);
        PermitIssuanceApplicationReviewRequestTaskPayload permitIssuanceApplicationReviewRequestTaskPayload =
            (PermitIssuanceApplicationReviewRequestTaskPayload) requestTaskPayload;

        assertThat(permitIssuanceApplicationReviewRequestTaskPayload.getInstallationOperatorDetails()).isEqualTo(expectedInstallationOperatorDetails);
    }

    @Test
    void getRequestTaskTypes() {
        assertThat(initializer.getRequestTaskTypes())
            .containsExactly(RequestTaskType.PERMIT_ISSUANCE_APPLICATION_PEER_REVIEW);
    }
}
