package uk.gov.pmrv.api.workflow.request.flow.permitnotification.service;

import java.util.Map;
import org.springframework.stereotype.Component;
import uk.gov.pmrv.api.workflow.request.flow.common.service.notification.DocumentTemplateGenerationContextActionType;
import uk.gov.pmrv.api.workflow.request.flow.common.service.notification.DocumentTemplateWorkflowParamsProvider;
import uk.gov.pmrv.api.workflow.request.flow.permitnotification.domain.PermitNotificationRequestPayload;

@Component
public class PermitNotificationRejectedDocumentTemplateWorkflowParamsProvider
    implements DocumentTemplateWorkflowParamsProvider<PermitNotificationRequestPayload> {

    @Override
    public DocumentTemplateGenerationContextActionType getContextActionType() {
        return DocumentTemplateGenerationContextActionType.PERMIT_NOTIFICATION_REJECTED;
    }

    @Override
    public Map<String, Object> constructParams(final PermitNotificationRequestPayload payload) {

        return Map.of("officialNotice", payload.getReviewDecision().getOfficialNotice());
    }
}

