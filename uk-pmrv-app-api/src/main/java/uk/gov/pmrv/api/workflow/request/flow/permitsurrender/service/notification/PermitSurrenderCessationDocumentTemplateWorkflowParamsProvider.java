package uk.gov.pmrv.api.workflow.request.flow.permitsurrender.service.notification;

import java.time.ZoneId;
import java.util.Date;
import java.util.Map;

import org.springframework.stereotype.Component;

import uk.gov.pmrv.api.workflow.request.flow.common.service.notification.DocumentTemplateGenerationContextActionType;
import uk.gov.pmrv.api.workflow.request.flow.common.service.notification.DocumentTemplateWorkflowParamsProvider;
import uk.gov.pmrv.api.workflow.request.flow.permitsurrender.domain.PermitSurrenderRequestPayload;

@Component
public class PermitSurrenderCessationDocumentTemplateWorkflowParamsProvider implements DocumentTemplateWorkflowParamsProvider<PermitSurrenderRequestPayload> {

    @Override
    public DocumentTemplateGenerationContextActionType getContextActionType() {
        return DocumentTemplateGenerationContextActionType.PERMIT_SURRENDER_CESSATION;
    }
    
    @Override
    public Map<String, Object> constructParams(PermitSurrenderRequestPayload payload) {
        return Map.of(
                "reviewDeterminationCompletedDate", Date.from(payload.getReviewDeterminationCompletedDate().atStartOfDay(ZoneId.systemDefault()).toInstant()),
                "noticeType", payload.getPermitCessation().getNoticeType()
                );
    }

}

