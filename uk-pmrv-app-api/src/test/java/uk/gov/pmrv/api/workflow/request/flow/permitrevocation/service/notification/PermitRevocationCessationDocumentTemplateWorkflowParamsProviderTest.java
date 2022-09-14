package uk.gov.pmrv.api.workflow.request.flow.permitrevocation.service.notification;

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
import uk.gov.pmrv.api.workflow.request.flow.common.domain.permit.cessation.PermitCessationContainer;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.permit.cessation.PermitCessationDeterminationOutcome;
import uk.gov.pmrv.api.workflow.request.flow.common.service.notification.DocumentTemplateGenerationContextActionType;
import uk.gov.pmrv.api.workflow.request.flow.permitrevocation.domain.PermitRevocationRequestPayload;

@ExtendWith(MockitoExtension.class)
class PermitRevocationCessationDocumentTemplateWorkflowParamsProviderTest {

	@InjectMocks
	private PermitRevocationCessationDocumentTemplateWorkflowParamsProvider provider;

	@Test
	void getContextActionType() {
		assertThat(provider.getContextActionType())
				.isEqualTo(DocumentTemplateGenerationContextActionType.PERMIT_REVOCATION_CESSATION);
	}
	
	@Test
    void constructParams() {
        LocalDate permitCessationCompletedDate = LocalDate.now();
        PermitRevocationRequestPayload payload = PermitRevocationRequestPayload.builder()
                .permitCessationCompletedDate(permitCessationCompletedDate)
                .permitCessationContainer(PermitCessationContainer.builder()
                		.cessation(PermitCessation.builder().determinationOutcome(PermitCessationDeterminationOutcome.APPROVED).build())
                        .build())
                .build();
        
        Map<String, Object> result = provider.constructParams(payload);
        
        assertThat(result).containsExactlyInAnyOrderEntriesOf(Map.of(
                "permitCessationCompletedDate", Date.from(payload.getPermitCessationCompletedDate().atStartOfDay(ZoneId.systemDefault()).toInstant()),
                "determinationOutcome", payload.getPermitCessationContainer().getCessation().getDeterminationOutcome().name()
                ));
    }

}
