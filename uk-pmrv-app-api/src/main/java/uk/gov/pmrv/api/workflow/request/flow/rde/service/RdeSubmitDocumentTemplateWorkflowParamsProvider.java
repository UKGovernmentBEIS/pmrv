package uk.gov.pmrv.api.workflow.request.flow.rde.service;

import java.time.ZoneId;
import java.util.Date;
import java.util.Map;

import org.springframework.stereotype.Component;

import uk.gov.pmrv.api.workflow.request.flow.common.service.notification.DocumentTemplateGenerationContextActionType;
import uk.gov.pmrv.api.workflow.request.flow.common.service.notification.DocumentTemplateWorkflowParamsProvider;
import uk.gov.pmrv.api.workflow.request.flow.rde.domain.RequestPayloadRdeable;

@Component
public class RdeSubmitDocumentTemplateWorkflowParamsProvider implements DocumentTemplateWorkflowParamsProvider<RequestPayloadRdeable> {

    @Override
    public DocumentTemplateGenerationContextActionType getContextActionType() {
        return DocumentTemplateGenerationContextActionType.RDE_SUBMIT;
    }
    
    @Override
    public Map<String, Object> constructParams(RequestPayloadRdeable payload) {
        return Map.of(
                "extensionDate", Date.from(payload.getRdeData().getRdePayload().getExtensionDate().atStartOfDay(ZoneId.systemDefault()).toInstant()),
                "deadline", Date.from(payload.getRdeData().getRdePayload().getDeadline().atStartOfDay(ZoneId.systemDefault()).toInstant())
                );
    }

}

