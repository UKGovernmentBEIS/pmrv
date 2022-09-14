package uk.gov.pmrv.api.files.attachments.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.files.common.domain.dto.FileDTO;
import uk.gov.pmrv.api.user.core.domain.dto.FileToken;
import uk.gov.pmrv.api.user.core.service.UserFileTokenService;

@Service
@RequiredArgsConstructor
public class FileAttachmentTokenService {

    private final UserFileTokenService userFileTokenService;
    private final FileAttachmentService fileAttachmentService;
    
    public FileToken generateGetFileAttachmentToken(String attachmentUuid) {
        if(!fileAttachmentService.fileAttachmentExist(attachmentUuid)) {
            throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, attachmentUuid);
        }
        
        return userFileTokenService.generateGetFileToken(attachmentUuid);
    }
    
    public FileDTO getFileDTOByToken(String getFileAttachmentToken) {
        String fileAttachmentUuid = userFileTokenService.resolveGetFileUuid(getFileAttachmentToken);
        return fileAttachmentService.getFileDTO(fileAttachmentUuid);
    }
    
}
