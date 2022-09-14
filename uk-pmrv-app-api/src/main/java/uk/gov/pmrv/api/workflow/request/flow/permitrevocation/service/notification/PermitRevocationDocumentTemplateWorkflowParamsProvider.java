package uk.gov.pmrv.api.workflow.request.flow.permitrevocation.service.notification;

import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

import uk.gov.pmrv.api.workflow.request.flow.common.service.notification.DocumentTemplateGenerationContextActionType;
import uk.gov.pmrv.api.workflow.request.flow.common.service.notification.DocumentTemplateWorkflowParamsProvider;
import uk.gov.pmrv.api.workflow.request.flow.permitrevocation.domain.PermitRevocationRequestPayload;

@Component
public class PermitRevocationDocumentTemplateWorkflowParamsProvider implements DocumentTemplateWorkflowParamsProvider<PermitRevocationRequestPayload> {

    @Override
    public DocumentTemplateGenerationContextActionType getContextActionType() {
        return DocumentTemplateGenerationContextActionType.PERMIT_REVOCATION;
    }
    
    @Override
    public Map<String, Object> constructParams(PermitRevocationRequestPayload payload) {
    	Map<String, Object> params = new HashMap<>();
    	params.put("feeCharged", payload.getPermitRevocation().getFeeCharged().booleanValue());
    	params.put("feeAmount", payload.getPaymentAmount());
    	params.put("reason", payload.getPermitRevocation().getReason());
    	params.put("effectiveDate", Date.from(payload.getPermitRevocation().getEffectiveDate().atStartOfDay(ZoneId.systemDefault()).toInstant()));
    	if(payload.getPermitRevocation().getAnnualEmissionsReportDate() != null) {
    		params.put("annualEmissionsReportDate", Date.from(payload.getPermitRevocation().getAnnualEmissionsReportDate().atStartOfDay(ZoneId.systemDefault()).toInstant()));
    	}
    	if(payload.getPermitRevocation().getSurrenderDate() != null) {
    		params.put("surrenderDate", Date.from(payload.getPermitRevocation().getSurrenderDate().atStartOfDay(ZoneId.systemDefault()).toInstant()));
    	}
    	if(payload.getPermitRevocation().getFeeDate() != null) {
    		params.put("feeDate", Date.from(payload.getPermitRevocation().getFeeDate().atStartOfDay(ZoneId.systemDefault()).toInstant()));
    	}
    	
    	return params;
    }

}
