package uk.gov.pmrv.api.workflow.request.flow.rde.handler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.flow.permitissuance.common.domain.PermitIssuanceRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.rde.domain.RdeData;
import uk.gov.pmrv.api.workflow.request.flow.rde.domain.RdeForceDecisionRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.rde.domain.RdePayload;
import uk.gov.pmrv.api.workflow.request.flow.rde.domain.RdeResponsePayload;

import java.time.LocalDate;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class WaitForRdeResponseInitializerTest {

    @InjectMocks
    private WaitForRdeResponseInitializer initializer;

    @Test
    void initializePayload() {
        LocalDate dueDate = LocalDate.now().plusDays(3);
        LocalDate proposedDate = LocalDate.now().plusDays(5);
        LocalDate deadlineDate = LocalDate.now().plusDays(1);

        final RdeResponsePayload rdeResponsePayload = RdeResponsePayload.builder()
                .currentDueDate(dueDate)
                .proposedDueDate(proposedDate).build();
        final RdePayload rdePayload = RdePayload.builder()
                .extensionDate(rdeResponsePayload.getProposedDueDate())
                .deadline(deadlineDate)
                .operators(Set.of("operator"))
                .signatory("signatory")
                .build();
        final PermitIssuanceRequestPayload permitRequestPayload = PermitIssuanceRequestPayload.builder()
                .payloadType(RequestPayloadType.PERMIT_ISSUANCE_REQUEST_PAYLOAD)
                .rdeData(RdeData.builder()
                		.rdePayload(rdePayload)
                        .currentDueDate(dueDate)
                		.build())
                .build();
        final Request request = Request.builder()
                .type(RequestType.PERMIT_ISSUANCE)
                .payload(permitRequestPayload)
                .build();

        // Invoke
        RequestTaskPayload actual = initializer.initializePayload(request);

        // Verify
        assertThat(actual.getPayloadType()).isEqualTo(RequestTaskPayloadType.RDE_WAIT_FOR_RESPONSE_PAYLOAD);
        assertThat(actual).isInstanceOf(RdeForceDecisionRequestTaskPayload.class);
        assertThat(((RdeForceDecisionRequestTaskPayload) actual).getRdeResponsePayload()).isEqualTo(rdeResponsePayload);
    }

    @Test
    void getRequestTaskTypes() {
        assertEquals(initializer.getRequestTaskTypes(), Set.of(RequestTaskType.PERMIT_ISSUANCE_WAIT_FOR_RDE_RESPONSE,
                RequestTaskType.PERMIT_SURRENDER_WAIT_FOR_RDE_RESPONSE, RequestTaskType.PERMIT_VARIATION_WAIT_FOR_RDE_RESPONSE));
    }
}
