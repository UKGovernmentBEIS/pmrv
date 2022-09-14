package uk.gov.pmrv.api.workflow.request.flow.rfi.domain;

import uk.gov.pmrv.api.workflow.request.core.domain.Payload;

public interface RequestPayloadRfiable extends Payload {
	
	RfiData getRfiData();
	void setRfiData(RfiData rfiData);
    
    default void cleanRfiData() {
    	getRfiData().setRfiQuestionPayload(null);
    	getRfiData().setRfiDeadline(null);
    	getRfiData().setRfiResponsePayload(null);
    	getRfiData().getRfiAttachments().clear();
    }
}
