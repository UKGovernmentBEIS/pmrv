package uk.gov.pmrv.api.workflow.request.flow.permitrevocation.handler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.common.domain.model.PmrvUser;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.permitrevocation.domain.PermitRevocationApplicationWithdrawRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitrevocation.service.RequestPermitRevocationService;

@ExtendWith(MockitoExtension.class)
class PermitRevocationWithdrawActionHandlerTest {

    @InjectMocks
    private PermitRevocationWithdrawActionHandler handler;

    @Mock
    private RequestTaskService requestTaskService;

    @Mock
    private RequestPermitRevocationService requestPermitRevocationService;

    @Test
    void process() {

        final Long requestTaskId = 1L;
        final PmrvUser pmrvUser = PmrvUser.builder().userId("userId").build();
        final PermitRevocationApplicationWithdrawRequestTaskActionPayload taskActionPayload = 
            PermitRevocationApplicationWithdrawRequestTaskActionPayload.builder()
            .reason("the reason")
            .files(Set.of(UUID.randomUUID()))
            .build();
        final RequestTask requestTask = RequestTask.builder()
            .id(requestTaskId)
            .build();

        when(requestTaskService.findTaskById(requestTaskId)).thenReturn(requestTask);

        handler.process(
            requestTaskId,
            RequestTaskActionType.PERMIT_REVOCATION_WITHDRAW_APPLICATION,
            pmrvUser,
            taskActionPayload);

        verify(requestTaskService, times(1)).findTaskById(requestTaskId);
        verify(requestPermitRevocationService, times(1)).applyWithdrawPayload(taskActionPayload, requestTask);
    }

    @Test
    void getTypes() {
        assertThat(handler.getTypes()).containsExactly(RequestTaskActionType.PERMIT_REVOCATION_WITHDRAW_APPLICATION);
    }
}
