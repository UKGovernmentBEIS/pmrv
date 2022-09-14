package uk.gov.pmrv.api.workflow.request.flow.aer.handler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.pmrv.api.reporting.domain.Aer;
import uk.gov.pmrv.api.common.domain.model.PmrvUser;
import uk.gov.pmrv.api.permit.domain.additionaldocuments.AdditionalDocuments;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.aer.domain.AerSaveApplicationRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aer.service.RequestAerApplyService;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AerApplySaveActionHandlerTest {

    @InjectMocks
    private AerApplySaveActionHandler handler;

    @Mock
    private RequestAerApplyService requestAerApplyService;

    @Mock
    private RequestTaskService requestTaskService;

    @Test
    void doProcess() {
        AerSaveApplicationRequestTaskActionPayload aerApplySavePayload =
                AerSaveApplicationRequestTaskActionPayload.builder().payloadType(RequestTaskActionPayloadType.AER_SAVE_APPLICATION_PAYLOAD)
                        .aer(Aer.builder()
                                .additionalDocuments(AdditionalDocuments.builder().exist(false).build())
                                .build())
                        .build();

        RequestTask requestTask = RequestTask.builder().id(1L).build();
        PmrvUser pmrvUser = PmrvUser.builder().build();

        when(requestTaskService.findTaskById(1L)).thenReturn(requestTask);

        // Invoke
        handler.process(requestTask.getId(), RequestTaskActionType.AER_SAVE_APPLICATION, pmrvUser, aerApplySavePayload);

        // Verify
        verify(requestAerApplyService, times(1)).applySaveAction(aerApplySavePayload, requestTask);
    }

    @Test
    void getTypes() {
        List<RequestTaskActionType> actual = handler.getTypes();

        assertThat(actual).isEqualTo(List.of(RequestTaskActionType.AER_SAVE_APPLICATION));
    }
}
