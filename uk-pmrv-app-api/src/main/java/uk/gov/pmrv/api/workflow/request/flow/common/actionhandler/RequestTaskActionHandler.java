package uk.gov.pmrv.api.workflow.request.flow.common.actionhandler;

import java.util.List;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.pmrv.api.common.domain.model.PmrvUser;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;

public interface RequestTaskActionHandler<T extends RequestTaskActionPayload> {

    @Transactional
    void process(Long requestTaskId, RequestTaskActionType requestTaskActionType, PmrvUser pmrvUser, T payload);
    
    List<RequestTaskActionType> getTypes();
}
