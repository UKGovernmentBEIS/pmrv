package uk.gov.pmrv.api.workflow.request.flow.permitrevocation.handler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestStatus;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.permit.cessation.PermitCessationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitrevocation.domain.PermitRevocation;
import uk.gov.pmrv.api.workflow.request.flow.permitrevocation.domain.PermitRevocationRequestPayload;

@ExtendWith(MockitoExtension.class)
class PermitRevocationCessationSubmitInitializerTest {

    @InjectMocks
    private PermitRevocationCessationSubmitInitializer initializer;

    @Test
    void initializePayload() {
        
        final String requestId = "1";
        final Request request = Request.builder()
            .id(requestId)
            .payload(PermitRevocationRequestPayload.builder()
                .permitRevocation(PermitRevocation.builder().surrenderRequired(true).build())
                .build())
            .type(RequestType.PERMIT_REVOCATION)
            .status(RequestStatus.IN_PROGRESS)
            .build();

        final RequestTaskPayload requestTaskPayload = initializer.initializePayload(request);

        assertThat(requestTaskPayload.getPayloadType()).isEqualTo(RequestTaskPayloadType.PERMIT_REVOCATION_CESSATION_SUBMIT_PAYLOAD);
        assertTrue(((PermitCessationSubmitRequestTaskPayload)requestTaskPayload).getCessationContainer().isAllowancesSurrenderRequired());
        assertThat(requestTaskPayload).isInstanceOf(PermitCessationSubmitRequestTaskPayload.class);
    }

    @Test
    void getRequestTaskTypes() {
        assertEquals(initializer.getRequestTaskTypes(), Set.of(RequestTaskType.PERMIT_REVOCATION_CESSATION_SUBMIT));
    }
}
