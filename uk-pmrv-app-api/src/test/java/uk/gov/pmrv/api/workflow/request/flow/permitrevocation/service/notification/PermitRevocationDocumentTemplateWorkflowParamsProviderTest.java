package uk.gov.pmrv.api.workflow.request.flow.permitrevocation.service.notification;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.pmrv.api.workflow.request.flow.common.service.notification.DocumentTemplateGenerationContextActionType;
import uk.gov.pmrv.api.workflow.request.flow.permitrevocation.domain.PermitRevocation;
import uk.gov.pmrv.api.workflow.request.flow.permitrevocation.domain.PermitRevocationRequestPayload;

@ExtendWith(MockitoExtension.class)
class PermitRevocationDocumentTemplateWorkflowParamsProviderTest {

	@InjectMocks
	private PermitRevocationDocumentTemplateWorkflowParamsProvider provider;

	@Test
	void getContextActionType() {
		assertThat(provider.getContextActionType())
				.isEqualTo(DocumentTemplateGenerationContextActionType.PERMIT_REVOCATION);
	}
	
	@Test
    void constructParams() {
		LocalDate effectiveDate = LocalDate.now().plusDays(1);
		LocalDate annualEmissionsReportDate = LocalDate.now().plusDays(2);
		LocalDate surrenderDate = LocalDate.now().plusDays(3);
		LocalDate feeDate = LocalDate.now().plusDays(4);
        PermitRevocationRequestPayload payload = PermitRevocationRequestPayload.builder()
        		.paymentAmount(BigDecimal.valueOf(123.22))
        		.permitRevocation(PermitRevocation.builder()
        				.feeCharged(Boolean.TRUE)
        				.reason("reason")
        				.effectiveDate(effectiveDate)
        				.annualEmissionsReportDate(annualEmissionsReportDate)
        				.surrenderDate(surrenderDate)
        				.feeDate(feeDate)
        				.build())
                .build();
        
        Map<String, Object> result = provider.constructParams(payload);
        
        assertThat(result).containsExactlyInAnyOrderEntriesOf(Map.of(
        		"feeCharged", Boolean.TRUE,
        		"feeAmount", BigDecimal.valueOf(123.22),
        		"reason", "reason",
                "effectiveDate", Date.from(payload.getPermitRevocation().getEffectiveDate().atStartOfDay(ZoneId.systemDefault()).toInstant()),
                "annualEmissionsReportDate", Date.from(payload.getPermitRevocation().getAnnualEmissionsReportDate().atStartOfDay(ZoneId.systemDefault()).toInstant()),
                "surrenderDate", Date.from(payload.getPermitRevocation().getSurrenderDate().atStartOfDay(ZoneId.systemDefault()).toInstant()),
                "feeDate", Date.from(payload.getPermitRevocation().getFeeDate().atStartOfDay(ZoneId.systemDefault()).toInstant())
                ));
    }
	
	@Test
    void constructParams_no_required_dates_filled() {
		LocalDate effectiveDate = LocalDate.now().plusDays(1);
        PermitRevocationRequestPayload payload = PermitRevocationRequestPayload.builder()
        		.paymentAmount(BigDecimal.valueOf(123.22))
        		.permitRevocation(PermitRevocation.builder()
        				.feeCharged(Boolean.TRUE)
        				.reason("reason")
        				.effectiveDate(effectiveDate)
        				.build())
                .build();
        
        Map<String, Object> result = provider.constructParams(payload);
        
        assertThat(result).containsExactlyInAnyOrderEntriesOf(Map.of(
        		"feeCharged", Boolean.TRUE,
        		"feeAmount", BigDecimal.valueOf(123.22),
        		"reason", "reason",
                "effectiveDate", Date.from(payload.getPermitRevocation().getEffectiveDate().atStartOfDay(ZoneId.systemDefault()).toInstant())
                ));
    }
}
