package uk.gov.pmrv.api.workflow.request.flow.permitsurrender.handler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestStatus;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.flow.permitsurrender.domain.PermitSurrenderApplicationReviewRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitsurrender.domain.PermitSurrenderRequestPayload;

class PermitSurrenderWaitForPeerReviewInitializerTest {

    private final PermitSurrenderWaitForPeerReviewInitializer initializer = new PermitSurrenderWaitForPeerReviewInitializer();

    @Test
    void initializePayload() {
        Request request = Request.builder()
            .id("1")
            .type(RequestType.PERMIT_SURRENDER)
            .status(RequestStatus.IN_PROGRESS)
            .payload(PermitSurrenderRequestPayload.builder()
                .payloadType(RequestPayloadType.PERMIT_SURRENDER_REQUEST_PAYLOAD)
                .build())
            .build();

        RequestTaskPayload requestTaskPayload = initializer.initializePayload(request);
        assertThat(requestTaskPayload.getPayloadType()).isEqualTo(RequestTaskPayloadType.PERMIT_SURRENDER_WAIT_FOR_PEER_REVIEW_PAYLOAD);
        assertThat(requestTaskPayload).isInstanceOf(PermitSurrenderApplicationReviewRequestTaskPayload.class);
    }

    @Test
    void getRequestTaskTypes() {
        assertThat(initializer.getRequestTaskTypes())
            .containsExactly(RequestTaskType.PERMIT_SURRENDER_WAIT_FOR_PEER_REVIEW);
    }
}
