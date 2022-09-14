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

import uk.gov.pmrv.api.workflow.request.flow.common.service.notification.DocumentTemplateGenerationContextActionType;
import uk.gov.pmrv.api.workflow.request.flow.permitsurrender.domain.PermitSurrenderRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitsurrender.domain.PermitSurrenderReviewDeterminationGrant;

@ExtendWith(MockitoExtension.class)
class PermitSurrenderGrantedDocumentTemplateWorkflowParamsProviderTest {

    @InjectMocks
    private PermitSurrenderGrantedDocumentTemplateWorkflowParamsProvider provider;
    
    @Test
    void getContextActionType() {
        assertThat(provider.getContextActionType()).isEqualTo(DocumentTemplateGenerationContextActionType.PERMIT_SURRENDER_GRANTED);
    }
    
    @Test
    void constructParams() {
        LocalDate noticeDate = LocalDate.now().plusDays(10);
        LocalDate reportDate = LocalDate.now().plusDays(11);
        LocalDate allowancesSurrenderDate = LocalDate.now().plusDays(10);
        PermitSurrenderRequestPayload payload = PermitSurrenderRequestPayload.builder()
                .reviewDetermination(PermitSurrenderReviewDeterminationGrant.builder()
                        .noticeDate(noticeDate)
                        .reportDate(reportDate)
                        .allowancesSurrenderDate(allowancesSurrenderDate)
                        .build())
                .build();
        
        Map<String, Object> result = provider.constructParams(payload);
        
        assertThat(result).containsExactlyInAnyOrderEntriesOf(Map.of(
                "noticeDate", Date.from(noticeDate.atStartOfDay(ZoneId.systemDefault()).toInstant()),
                "reportDate", Date.from(reportDate.atStartOfDay(ZoneId.systemDefault()).toInstant()),
                "allowancesSurrenderDate", Date.from(allowancesSurrenderDate.atStartOfDay(ZoneId.systemDefault()).toInstant())
                ));
    }
    
    @Test
    void constructParams_no_allowances_date() {
        LocalDate noticeDate = LocalDate.now().plusDays(10);
        LocalDate reportDate = LocalDate.now().plusDays(11);
        PermitSurrenderRequestPayload payload = PermitSurrenderRequestPayload.builder()
                .reviewDetermination(PermitSurrenderReviewDeterminationGrant.builder()
                        .noticeDate(noticeDate)
                        .reportDate(reportDate)
                        .build())
                .build();
        
        Map<String, Object> result = provider.constructParams(payload);
        
        assertThat(result).containsExactlyInAnyOrderEntriesOf(Map.of(
                "noticeDate", Date.from(noticeDate.atStartOfDay(ZoneId.systemDefault()).toInstant()),
                "reportDate", Date.from(reportDate.atStartOfDay(ZoneId.systemDefault()).toInstant())
                ));
    }
    
    
}
