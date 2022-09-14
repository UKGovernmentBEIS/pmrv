package uk.gov.pmrv.api.workflow.request.flow.rde.domain;

import uk.gov.pmrv.api.workflow.request.core.domain.Payload;

public interface RequestPayloadRdeable extends Payload{
	
	RdeData getRdeData();
	void setRdeData(RdeData rdeData);

    default void cleanRdeData() {
    	getRdeData().setRdePayload(null);
    	getRdeData().setRdeDecisionPayload(null);
    	getRdeData().setRdeForceDecisionPayload(null);
    	getRdeData().getRdeAttachments().clear();
    }
}
