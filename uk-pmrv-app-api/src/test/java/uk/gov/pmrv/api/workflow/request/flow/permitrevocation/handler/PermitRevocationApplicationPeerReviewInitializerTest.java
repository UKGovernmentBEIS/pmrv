package uk.gov.pmrv.api.workflow.request.flow.permitrevocation.handler;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestStatus;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.flow.permitrevocation.domain.PermitRevocation;
import uk.gov.pmrv.api.workflow.request.flow.permitrevocation.domain.PermitRevocationApplicationPeerReviewRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitrevocation.domain.PermitRevocationRequestPayload;

@ExtendWith(MockitoExtension.class)
class PermitRevocationApplicationPeerReviewInitializerTest {

    @InjectMocks
    private PermitRevocationApplicationPeerReviewInitializer initializer;

    @Test
    void initializePayload() {

        final PermitRevocation permitRevocation = PermitRevocation.builder()
            .reason("reason")
            .effectiveDate(LocalDate.of(2023, 1, 2))
            .build();
        final Request request = Request.builder()
            .id("1")
            .type(RequestType.PERMIT_REVOCATION)
            .status(RequestStatus.IN_PROGRESS)
            .payload(PermitRevocationRequestPayload.builder()
                .payloadType(RequestPayloadType.PERMIT_REVOCATION_REQUEST_PAYLOAD)
                .permitRevocation(permitRevocation)
                .sectionsCompleted(Map.of("abc", true))
                .build())
            .build();

        final RequestTaskPayload requestTaskPayload = initializer.initializePayload(request);
        assertThat(requestTaskPayload.getPayloadType()).isEqualTo(
            RequestTaskPayloadType.PERMIT_REVOCATION_APPLICATION_PEER_REVIEW_PAYLOAD);
        assertThat(requestTaskPayload).isInstanceOf(PermitRevocationApplicationPeerReviewRequestTaskPayload.class);
        assertThat(((PermitRevocationApplicationPeerReviewRequestTaskPayload) requestTaskPayload).getPermitRevocation())
            .isEqualTo(permitRevocation);
        assertThat(((PermitRevocationApplicationPeerReviewRequestTaskPayload) requestTaskPayload).getSectionsCompleted())
            .isEqualTo(Map.of("abc", true));
    }

    @Test
    void getRequestTaskTypes() {
        assertThat(initializer.getRequestTaskTypes()).containsExactly(
            RequestTaskType.PERMIT_REVOCATION_APPLICATION_PEER_REVIEW);
    }
}
