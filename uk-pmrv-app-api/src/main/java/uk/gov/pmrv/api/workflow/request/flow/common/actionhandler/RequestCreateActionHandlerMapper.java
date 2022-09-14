package uk.gov.pmrv.api.workflow.request.flow.common.actionhandler;

import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestCreateActionPayload;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestCreateActionType;
import uk.gov.pmrv.api.workflow.request.core.validation.EnabledWorkflowValidator;

/**
 * The RequestCreateActionHandlerMapper for all request create actions.
 */
@Component
@AllArgsConstructor
public class RequestCreateActionHandlerMapper {

    private final List<RequestCreateActionHandler<? extends RequestCreateActionPayload>> handlers;
    private final EnabledWorkflowValidator enabledWorkflowValidator;

    public RequestCreateActionHandler get(final RequestCreateActionType requestCreateActionType) {

        return handlers.stream()
            .filter(handler -> enabledWorkflowValidator.isWorkflowEnabled(requestCreateActionType.getType()))
            .filter(handler -> requestCreateActionType.equals(handler.getType()))
            .findFirst()
            .orElseThrow(() -> {
                throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND);
            });
    }
}

