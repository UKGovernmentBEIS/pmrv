package uk.gov.pmrv.api.workflow.request.flow.common.actionhandler;

import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;

@Component
@AllArgsConstructor
public class RequestTaskActionHandlerMapper {
    
    private final List<RequestTaskActionHandler<? extends RequestTaskActionPayload>> handlers;

    public RequestTaskActionHandler get(final RequestTaskActionType requestTaskActionType) {

        return handlers.stream()
            .filter(h -> h.getTypes() != null && h.getTypes().contains(requestTaskActionType))
            .findFirst()
            .orElseThrow(() -> {
                throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND);
            });
    }
}
