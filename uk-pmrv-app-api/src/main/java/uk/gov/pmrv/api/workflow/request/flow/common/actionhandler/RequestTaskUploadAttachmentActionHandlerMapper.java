package uk.gov.pmrv.api.workflow.request.flow.common.actionhandler;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;

@Component
@RequiredArgsConstructor
public class RequestTaskUploadAttachmentActionHandlerMapper {

    private final List<RequestTaskUploadAttachmentActionHandler> requestTaskUploadAttachmentActionHandlers;

    public RequestTaskUploadAttachmentActionHandler get(final RequestTaskActionType requestTaskActionType) {
        return requestTaskUploadAttachmentActionHandlers.stream()
            .filter(handler -> handler.getType().equals(requestTaskActionType))
            .findFirst()
            .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND));
    }
}
