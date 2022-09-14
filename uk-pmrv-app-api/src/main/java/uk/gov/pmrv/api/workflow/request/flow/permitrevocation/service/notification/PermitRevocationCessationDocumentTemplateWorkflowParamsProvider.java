package uk.gov.pmrv.api.workflow.request.flow.permitrevocation.service.notification;

import java.time.ZoneId;
import java.util.Date;
import java.util.Map;

import org.springframework.stereotype.Component;

import uk.gov.pmrv.api.workflow.request.flow.common.service.notification.DocumentTemplateGenerationContextActionType;
import uk.gov.pmrv.api.workflow.request.flow.common.service.notification.DocumentTemplateWorkflowParamsProvider;
import uk.gov.pmrv.api.workflow.request.flow.permitrevocation.domain.PermitRevocationRequestPayload;

@Component
public class PermitRevocationCessationDocumentTemplateWorkflowParamsProvider implements DocumentTemplateWorkflowParamsProvider<PermitRevocationRequestPayload> {

    @Override
    public DocumentTemplateGenerationContextActionType getContextActionType() {
        return DocumentTemplateGenerationContextActionType.PERMIT_REVOCATION_CESSATION;
    }
    
    @Override
    public Map<String, Object> constructParams(PermitRevocationRequestPayload payload) {
    	return Map.of(
    			"permitCessationCompletedDate", Date.from(payload.getPermitCessationCompletedDate().atStartOfDay(ZoneId.systemDefault()).toInstant()),
    			"determinationOutcome", payload.getPermitCessationContainer().getCessation().getDeterminationOutcome().name()
    			);
    }

}
