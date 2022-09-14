package uk.gov.pmrv.api.workflow.request.application.attachment.task;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.user.core.domain.dto.FileToken;
import uk.gov.pmrv.api.files.attachments.service.FileAttachmentTokenService;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;

@Service
@RequiredArgsConstructor
public class RequestTaskAttachmentService {

    private final RequestTaskService requestTaskService;
    private final FileAttachmentTokenService fileAttachmentTokenService;

    @Transactional
    public FileToken generateGetFileAttachmentToken(Long requestTaskId, UUID attachmentUuid) {
        RequestTask requestTask = requestTaskService.findTaskById(requestTaskId);

        if(!requestTask.getPayload().getAttachments().containsKey(attachmentUuid)) {
            throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, attachmentUuid);
        }

        return fileAttachmentTokenService.generateGetFileAttachmentToken(attachmentUuid.toString());
    }
}
