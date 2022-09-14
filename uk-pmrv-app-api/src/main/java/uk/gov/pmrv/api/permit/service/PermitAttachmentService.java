package uk.gov.pmrv.api.permit.service;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.files.attachments.service.FileAttachmentTokenService;
import uk.gov.pmrv.api.permit.domain.PermitContainer;
import uk.gov.pmrv.api.user.core.domain.dto.FileToken;

@RequiredArgsConstructor
@Service
public class PermitAttachmentService {

    private final PermitQueryService permitQueryService;
    private final FileAttachmentTokenService fileAttachmentTokenService;

    public FileToken generateGetFileAttachmentToken(String permitId, UUID attachmentUuid) {
        PermitContainer permitContainer = permitQueryService.getPermitContainerById(permitId);

        //validate
        if (!permitContainer.getPermitAttachments().containsKey(attachmentUuid)) {
            throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, attachmentUuid);
        }

        return fileAttachmentTokenService.generateGetFileAttachmentToken(attachmentUuid.toString());
    }

}
