package uk.gov.pmrv.api.workflow.request.flow.permitrevocation.handler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.common.domain.model.PmrvUser;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.permitrevocation.domain.PermitRevocation;
import uk.gov.pmrv.api.workflow.request.flow.permitrevocation.domain.PermitRevocationSaveApplicationRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitrevocation.service.RequestPermitRevocationService;

@ExtendWith(MockitoExtension.class)
class PermitRevocationApplySaveActionHandlerTest {

    @InjectMocks
    private PermitRevocationApplySaveActionHandler handler;

    @Mock
    private RequestPermitRevocationService requestPermitRevocationService;

    @Mock
    private RequestTaskService requestTaskService;

    @Test
    void process() {

        final PermitRevocationSaveApplicationRequestTaskActionPayload actionPayload =
            PermitRevocationSaveApplicationRequestTaskActionPayload.builder()
                .payloadType(RequestTaskActionPayloadType.PERMIT_REVOCATION_SAVE_APPLICATION_PAYLOAD)
                .permitRevocation(
                    PermitRevocation.builder().reason("reason").build())
                .build();

        final RequestTask requestTask = RequestTask.builder().id(1L).build();
        final PmrvUser pmrvUser = PmrvUser.builder().build();

        when(requestTaskService.findTaskById(1L)).thenReturn(requestTask);

        handler.process(requestTask.getId(),
            RequestTaskActionType.PERMIT_REVOCATION_SAVE_APPLICATION,
            pmrvUser,
            actionPayload);

        verify(requestPermitRevocationService, times(1)).applySavePayload(actionPayload, requestTask);
    }

    @Test
    void getTypes() {
        assertThat(handler.getTypes()).isEqualTo(List.of(RequestTaskActionType.PERMIT_REVOCATION_SAVE_APPLICATION));
    }

}
