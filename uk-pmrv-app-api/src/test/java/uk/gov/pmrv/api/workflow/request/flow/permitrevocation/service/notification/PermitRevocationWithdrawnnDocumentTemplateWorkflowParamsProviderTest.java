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

import uk.gov.pmrv.api.workflow.request.flow.common.service.notification.DocumentTemplateGenerationContextActionType;
import uk.gov.pmrv.api.workflow.request.flow.permitrevocation.domain.PermitRevocationRequestPayload;

@ExtendWith(MockitoExtension.class)
class PermitRevocationWithdrawnnDocumentTemplateWorkflowParamsProviderTest {

	@InjectMocks
	private PermitRevocationWithdrawnDocumentTemplateWorkflowParamsProvider provider;

	@Test
	void getContextActionType() {
		assertThat(provider.getContextActionType())
				.isEqualTo(DocumentTemplateGenerationContextActionType.PERMIT_REVOCATION_WITHDRAWN);
	}
	
	@Test
    void constructParams() {
        LocalDate withdrawnCompletedDate = LocalDate.now();
        PermitRevocationRequestPayload payload = PermitRevocationRequestPayload.builder()
        		.withdrawCompletedDate(withdrawnCompletedDate)
                .build();
        
        Map<String, Object> result = provider.constructParams(payload);
        
        assertThat(result).containsExactlyInAnyOrderEntriesOf(Map.of(
                "withdrawCompletedDate", Date.from(payload.getWithdrawCompletedDate().atStartOfDay(ZoneId.systemDefault()).toInstant())
                ));
    }

}
