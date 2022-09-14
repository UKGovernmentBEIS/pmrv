package uk.gov.pmrv.api.workflow.request.flow.permitrevocation.handler;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.flow.permitrevocation.domain.PermitRevocationWaitForAppealRequestTaskPayload;

@ExtendWith(MockitoExtension.class)
class PermitRevocationWaitForAppealInitializerTest {

    @InjectMocks
    private PermitRevocationWaitForAppealInitializer initializer;

    @Test
    void initializePayload() {


        final Request request = Request.builder().id("1").build();

        final RequestTaskPayload requestTaskPayload = initializer.initializePayload(request);

        assertThat(requestTaskPayload.getPayloadType()).isEqualTo(RequestTaskPayloadType.PERMIT_REVOCATION_WAIT_FOR_APPEAL_PAYLOAD);
        assertThat(requestTaskPayload).isInstanceOf(PermitRevocationWaitForAppealRequestTaskPayload.class);
    }

    @Test
    void getRequestTaskTypes() {
        assertThat(initializer.getRequestTaskTypes()).containsExactly(RequestTaskType.PERMIT_REVOCATION_WAIT_FOR_APPEAL);
    }
}
