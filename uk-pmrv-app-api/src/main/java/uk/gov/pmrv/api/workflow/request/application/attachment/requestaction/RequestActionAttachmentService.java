package uk.gov.pmrv.api.workflow.request.application.attachment.requestaction;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.user.core.domain.dto.FileToken;
import uk.gov.pmrv.api.files.attachments.service.FileAttachmentTokenService;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestAction;
import uk.gov.pmrv.api.workflow.request.core.repository.RequestActionRepository;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RequestActionAttachmentService {

    private final RequestActionRepository requestActionRepository;
    private final FileAttachmentTokenService fileAttachmentTokenService;

    @Transactional
    public FileToken generateGetFileAttachmentToken(Long requestActionId, UUID attachmentUuid) {
        RequestAction requestAction = requestActionRepository.findById(requestActionId)
            .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND));

        if(!requestAction.getPayload().getAttachments().containsKey(attachmentUuid)) {
            throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, attachmentUuid);
        }

        return fileAttachmentTokenService.generateGetFileAttachmentToken(attachmentUuid.toString());
    }
}
