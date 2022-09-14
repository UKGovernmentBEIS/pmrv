package uk.gov.pmrv.api.workflow.request.flow.permitsurrender.handler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Set;
import org.junit.jupiter.api.Test;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.permit.cessation.PermitCessationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitsurrender.domain.PermitSurrenderRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitsurrender.domain.PermitSurrenderReviewDeterminationGrant;
import uk.gov.pmrv.api.workflow.request.flow.permitsurrender.domain.PermitSurrenderReviewDeterminationType;

class PermitSurrenderCessationSubmitInitializerTest {

    private PermitSurrenderCessationSubmitInitializer initializer = new PermitSurrenderCessationSubmitInitializer();

    @Test
    void initializePayload_permit_surrender_determination_granted() {
        PermitSurrenderRequestPayload requestPayload = PermitSurrenderRequestPayload.builder()
            .payloadType(RequestPayloadType.PERMIT_SURRENDER_REQUEST_PAYLOAD)
            .reviewDetermination(PermitSurrenderReviewDeterminationGrant.builder()
                .type(PermitSurrenderReviewDeterminationType.GRANTED)
                .allowancesSurrenderRequired(Boolean.TRUE)
                .build())
            .build();

        Request request = Request.builder()
            .type(RequestType.PERMIT_SURRENDER)
            .payload(requestPayload)
            .build();

        RequestTaskPayload requestTaskPayload = initializer.initializePayload(request);
        assertThat(requestTaskPayload.getPayloadType()).isEqualTo(RequestTaskPayloadType.PERMIT_SURRENDER_CESSATION_SUBMIT_PAYLOAD);
        assertThat(requestTaskPayload).isInstanceOf(PermitCessationSubmitRequestTaskPayload.class);

        PermitCessationSubmitRequestTaskPayload cessationSubmitRequestTaskPayload =
            (PermitCessationSubmitRequestTaskPayload) requestTaskPayload;

        assertNotNull(cessationSubmitRequestTaskPayload.getCessationContainer());
        assertTrue(cessationSubmitRequestTaskPayload.getCessationContainer().isAllowancesSurrenderRequired());
    }

    @Test
    void initializePayload_permit_surrender_determination_rejected() {
        PermitSurrenderRequestPayload requestPayload = PermitSurrenderRequestPayload.builder()
            .payloadType(RequestPayloadType.PERMIT_SURRENDER_REQUEST_PAYLOAD)
            .reviewDetermination(PermitSurrenderReviewDeterminationGrant.builder()
                .type(PermitSurrenderReviewDeterminationType.GRANTED)
                .allowancesSurrenderRequired(Boolean.FALSE)
                .build())
            .build();

        Request request = Request.builder()
            .type(RequestType.PERMIT_SURRENDER)
            .payload(requestPayload)
            .build();

        RequestTaskPayload requestTaskPayload = initializer.initializePayload(request);
        assertThat(requestTaskPayload.getPayloadType()).isEqualTo(RequestTaskPayloadType.PERMIT_SURRENDER_CESSATION_SUBMIT_PAYLOAD);
        assertThat(requestTaskPayload).isInstanceOf(PermitCessationSubmitRequestTaskPayload.class);

        PermitCessationSubmitRequestTaskPayload cessationSubmitRequestTaskPayload =
            (PermitCessationSubmitRequestTaskPayload) requestTaskPayload;

        assertNotNull(cessationSubmitRequestTaskPayload.getCessationContainer());
        assertFalse(cessationSubmitRequestTaskPayload.getCessationContainer().isAllowancesSurrenderRequired());
    }

    @Test
    void getRequestTaskTypes() {
        assertEquals(initializer.getRequestTaskTypes(), Set.of(RequestTaskType.PERMIT_SURRENDER_CESSATION_SUBMIT));

    }
}