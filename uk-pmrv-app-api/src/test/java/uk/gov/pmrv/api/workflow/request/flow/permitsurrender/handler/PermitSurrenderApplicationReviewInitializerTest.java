package uk.gov.pmrv.api.workflow.request.flow.permitsurrender.handler;

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
import uk.gov.pmrv.api.workflow.request.flow.permitsurrender.domain.PermitSurrender;
import uk.gov.pmrv.api.workflow.request.flow.permitsurrender.domain.PermitSurrenderApplicationReviewRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitsurrender.domain.PermitSurrenderRequestPayload;

import java.time.LocalDate;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class PermitSurrenderApplicationReviewInitializerTest {

    @InjectMocks
    private PermitSurrenderApplicationReviewInitializer initializer;

    @Test
    void initializePayload() {
        final String requestId = "1";

        PermitSurrenderRequestPayload requestPayload = PermitSurrenderRequestPayload.builder()
            .payloadType(RequestPayloadType.PERMIT_SURRENDER_REQUEST_PAYLOAD)
            .permitSurrender(PermitSurrender.builder().stopDate(LocalDate.now().minusDays(1L)).justification("justify").documentsExist(Boolean.FALSE).build())
            .build();

        Request request = Request.builder()
            .id(requestId)
            .type(RequestType.PERMIT_SURRENDER)
            .status(RequestStatus.IN_PROGRESS)
            .payload(requestPayload)
            .build();

        RequestTaskPayload requestTaskPayload = initializer.initializePayload(request);
        assertThat(requestTaskPayload.getPayloadType()).isEqualTo(RequestTaskPayloadType.PERMIT_SURRENDER_APPLICATION_REVIEW_PAYLOAD);
        assertThat(requestTaskPayload).isInstanceOf(PermitSurrenderApplicationReviewRequestTaskPayload.class);
    }

    @Test
    void getRequestTaskTypes() {
        assertEquals(initializer.getRequestTaskTypes(), Set.of(RequestTaskType.PERMIT_SURRENDER_APPLICATION_REVIEW));
    }
}
