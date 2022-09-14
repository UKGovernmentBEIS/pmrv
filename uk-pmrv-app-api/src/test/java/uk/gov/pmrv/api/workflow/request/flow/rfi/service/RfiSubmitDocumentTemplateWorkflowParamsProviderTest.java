package uk.gov.pmrv.api.workflow.request.flow.rfi.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.pmrv.api.workflow.request.flow.common.service.notification.DocumentTemplateGenerationContextActionType;
import uk.gov.pmrv.api.workflow.request.flow.permitissuance.common.domain.PermitIssuanceRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.rfi.domain.RequestPayloadRfiable;
import uk.gov.pmrv.api.workflow.request.flow.rfi.domain.RfiData;
import uk.gov.pmrv.api.workflow.request.flow.rfi.domain.RfiQuestionPayload;

@ExtendWith(MockitoExtension.class)
public class RfiSubmitDocumentTemplateWorkflowParamsProviderTest {

    @InjectMocks
    private RfiSubmitDocumentTemplateWorkflowParamsProvider provider;
    
    @Test
    void getRequestTaskActionType() {
        assertThat(provider.getContextActionType()).isEqualTo(DocumentTemplateGenerationContextActionType.RFI_SUBMIT);
    }
    
    @Test
    void constructParams() {
        RequestPayloadRfiable payload = PermitIssuanceRequestPayload.builder()
        		.rfiData(RfiData.builder()
        				.rfiDeadline(LocalDate.now().plusDays(10))
                        .rfiQuestionPayload(RfiQuestionPayload.builder()
                                .questions(List.of("quest1", "quest2"))
                                .build())
                		.build())
                .build();
        Map<String, Object> result = provider.constructParams(payload);
        assertThat(result).containsExactlyInAnyOrderEntriesOf(Map.of(
                "deadline", Date.from(payload.getRfiData().getRfiDeadline().atStartOfDay(ZoneId.systemDefault()).toInstant()),
                "questions", payload.getRfiData().getRfiQuestionPayload().getQuestions()
                ));
    }
}
