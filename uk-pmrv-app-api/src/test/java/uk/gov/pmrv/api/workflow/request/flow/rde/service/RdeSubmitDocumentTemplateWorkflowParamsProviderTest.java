package uk.gov.pmrv.api.workflow.request.flow.rde.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.pmrv.api.workflow.request.flow.common.service.notification.DocumentTemplateGenerationContextActionType;
import uk.gov.pmrv.api.workflow.request.flow.permitissuance.common.domain.PermitIssuanceRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.rde.domain.RdeData;
import uk.gov.pmrv.api.workflow.request.flow.rde.domain.RdePayload;
import uk.gov.pmrv.api.workflow.request.flow.rde.domain.RequestPayloadRdeable;

@ExtendWith(MockitoExtension.class)
class RdeSubmitDocumentTemplateWorkflowParamsProviderTest {

    @InjectMocks
    private RdeSubmitDocumentTemplateWorkflowParamsProvider provider;
    
    @Test
    void getRequestTaskActionType() {
        assertThat(provider.getContextActionType()).isEqualTo(DocumentTemplateGenerationContextActionType.RDE_SUBMIT);
    }
    
    @Test
    void constructParams() {
        RequestPayloadRdeable payload = PermitIssuanceRequestPayload.builder()
        		.rdeData(RdeData.builder()
        				.rdePayload(RdePayload.builder()
                                .extensionDate(LocalDate.now())
                                .deadline(LocalDate.now())
                                .build())
        				.build())
                .build();
        Map<String, Object> result = provider.constructParams(payload);
        assertThat(result).containsOnlyKeys("extensionDate", "deadline");
    }
    
}
