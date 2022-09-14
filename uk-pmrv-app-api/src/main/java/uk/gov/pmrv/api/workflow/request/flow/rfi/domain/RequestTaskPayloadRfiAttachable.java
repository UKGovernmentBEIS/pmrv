package uk.gov.pmrv.api.workflow.request.flow.rfi.domain;

import java.util.Map;
import java.util.UUID;

public interface RequestTaskPayloadRfiAttachable {
    
    Map<UUID, String> getRfiAttachments();
}
