package uk.gov.pmrv.api.workflow.request.flow.common.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.pmrv.api.common.domain.model.PmrvUser;
import uk.gov.pmrv.api.files.common.domain.FileStatus;
import uk.gov.pmrv.api.files.common.domain.dto.FileUuidDTO;
import uk.gov.pmrv.api.files.common.domain.dto.FileDTO;
import uk.gov.pmrv.api.files.attachments.service.FileAttachmentService;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.flow.common.actionhandler.RequestTaskUploadAttachmentActionHandlerMapper;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class RequestTaskAttachmentUploadService {

    private final FileAttachmentService fileAttachmentService;
    private final RequestTaskUploadAttachmentActionHandlerMapper requestTaskUploadAttachmentActionHandlerMapper;

    @Transactional
    public FileUuidDTO uploadAttachment(Long requestTaskId, RequestTaskActionType requestTaskActionType,
                                        PmrvUser authUser, FileDTO fileDTO) throws IOException {
        final String attachmentUuid = fileAttachmentService.createFileAttachment(fileDTO, FileStatus.PENDING, authUser);
        
		requestTaskUploadAttachmentActionHandlerMapper.get(requestTaskActionType).uploadAttachment(requestTaskId,
				attachmentUuid, fileDTO.getFileName());
		return FileUuidDTO.builder().uuid(attachmentUuid).build();
    }
}
