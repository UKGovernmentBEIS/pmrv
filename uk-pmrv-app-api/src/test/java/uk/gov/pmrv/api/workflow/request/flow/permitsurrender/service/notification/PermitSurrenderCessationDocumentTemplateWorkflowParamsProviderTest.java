package uk.gov.pmrv.api.workflow.request.flow.permitsurrender.service.notification;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.pmrv.api.workflow.request.flow.common.domain.permit.cessation.PermitCessation;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.permit.cessation.PermitCessationOfficialNoticeType;
import uk.gov.pmrv.api.workflow.request.flow.common.service.notification.DocumentTemplateGenerationContextActionType;
import uk.gov.pmrv.api.workflow.request.flow.permitsurrender.domain.PermitSurrenderRequestPayload;

@ExtendWith(MockitoExtension.class)
public class PermitSurrenderCessationDocumentTemplateWorkflowParamsProviderTest {
    
    @InjectMocks
    private PermitSurrenderCessationDocumentTemplateWorkflowParamsProvider provider;
    
    @Test
    void getContextActionType() {
        assertThat(provider.getContextActionType()).isEqualTo(DocumentTemplateGenerationContextActionType.PERMIT_SURRENDER_CESSATION);
    }
    
    @Test
    void constructParams() {
        LocalDate reviewDeterminationCompletedDate = LocalDate.now();
        PermitSurrenderRequestPayload payload = PermitSurrenderRequestPayload.builder()
                .reviewDeterminationCompletedDate(reviewDeterminationCompletedDate)
                .permitCessation(PermitCessation.builder()
                        .noticeType(PermitCessationOfficialNoticeType.NO_PROSPECT_OF_FURTHER_ALLOWANCES)
                        .build())
                .build();
        
        Map<String, Object> result = provider.constructParams(payload);
        
        assertThat(result).containsExactlyInAnyOrderEntriesOf(Map.of(
                "reviewDeterminationCompletedDate", Date.from(reviewDeterminationCompletedDate.atStartOfDay(ZoneId.systemDefault()).toInstant()),
                "noticeType", PermitCessationOfficialNoticeType.NO_PROSPECT_OF_FURTHER_ALLOWANCES
                ));
    }
}
