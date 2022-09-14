package uk.gov.pmrv.api.workflow.request.flow.aer.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.pmrv.api.account.domain.installationoperatordetails.InstallationOperatorDetails;
import uk.gov.pmrv.api.reporting.domain.Aer;
import uk.gov.pmrv.api.permit.domain.additionaldocuments.AdditionalDocuments;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.aer.domain.AerApplicationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.aer.domain.AerSaveApplicationRequestTaskActionPayload;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class RequestAerApplyServiceTest {

    @InjectMocks
    private RequestAerApplyService service;

    @Mock
    private RequestTaskService requestTaskService;

    @Test
    void applySaveAction() {
        AerApplicationSubmitRequestTaskPayload aerApplicationSubmitRequestTaskPayload = AerApplicationSubmitRequestTaskPayload.builder()
                .payloadType(RequestTaskPayloadType.AER_APPLICATION_SUBMIT_PAYLOAD)
                .installationOperatorDetails(InstallationOperatorDetails.builder().installationName("instName").build())
                .aerSectionsCompleted(new HashMap<>())
                .build();

        RequestTask requestTask = RequestTask.builder().payload(aerApplicationSubmitRequestTaskPayload).build();

        AerSaveApplicationRequestTaskActionPayload aerSaveApplicationRequestTaskActionPayload =
                AerSaveApplicationRequestTaskActionPayload.builder()
                        .payloadType(RequestTaskActionPayloadType.AER_SAVE_APPLICATION_PAYLOAD)
                        .aer(Aer.builder()
                                .additionalDocuments(AdditionalDocuments.builder().exist(true).build())
                                .build())
                        .aerSectionsCompleted(Map.of(AdditionalDocuments.class.getName(), List.of(true)))
                        .build();

        // Invoke
        service.applySaveAction(aerSaveApplicationRequestTaskActionPayload, requestTask);

        // Verify
        ArgumentCaptor<RequestTask> requestTaskCaptor = ArgumentCaptor.forClass(RequestTask.class);
        verify(requestTaskService, times(1)).saveRequestTask(requestTaskCaptor.capture());

        RequestTask updatedRequestTask = requestTaskCaptor.getValue();
        assertThat(updatedRequestTask.getPayload()).isInstanceOf(AerApplicationSubmitRequestTaskPayload.class);

        AerApplicationSubmitRequestTaskPayload payloadSaved = (AerApplicationSubmitRequestTaskPayload) updatedRequestTask.getPayload();
        assertThat(payloadSaved.getAer().getAdditionalDocuments().isExist()).isTrue();
        assertThat(payloadSaved.getAer().getAdditionalDocuments().getDocuments()).isNull();
        assertThat(payloadSaved.getInstallationOperatorDetails().getInstallationName()).isEqualTo("instName");
        assertThat(payloadSaved.getAerSectionsCompleted()).containsExactly(Map.entry(AdditionalDocuments.class.getName(), List.of(true)));
    }
}
