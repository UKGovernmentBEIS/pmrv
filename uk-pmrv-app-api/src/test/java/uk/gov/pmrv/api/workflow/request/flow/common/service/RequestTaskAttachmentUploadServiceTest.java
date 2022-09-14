package uk.gov.pmrv.api.workflow.request.flow.common.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.common.config.property.AppProperties;
import uk.gov.pmrv.api.common.domain.model.PmrvUser;
import uk.gov.pmrv.api.files.attachments.service.FileAttachmentService;
import uk.gov.pmrv.api.files.common.domain.FileStatus;
import uk.gov.pmrv.api.files.common.domain.dto.FileDTO;
import uk.gov.pmrv.api.files.common.domain.dto.FileUuidDTO;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.flow.common.actionhandler.RequestTaskUploadAttachmentActionHandlerMapper;
import uk.gov.pmrv.api.workflow.request.flow.permitissuance.common.handler.PermitSectionUploadAttachmentHandler;

@ExtendWith(MockitoExtension.class)
class RequestTaskAttachmentUploadServiceTest {

    @InjectMocks
    private RequestTaskAttachmentUploadService requestTaskAttachmentUploadService;

    @Mock
    private AppProperties appProperties;

    @Mock
    private FileAttachmentService fileAttachmentService;

    @Mock
    private RequestTaskUploadAttachmentActionHandlerMapper requestTaskUploadAttachmentActionHandlerMapper;

    @Mock
    private PermitSectionUploadAttachmentHandler permitSectionUploadAttachmentHandler;

    @Test
    void uploadAttachment() throws IOException {
        Long requestTaskId = 1L;
        RequestTaskActionType requestTaskActionType = RequestTaskActionType.PERMIT_ISSUANCE_UPLOAD_SECTION_ATTACHMENT;
        PmrvUser authUser = PmrvUser.builder().userId("userid").build();
        byte[] contentBytes = "dummycontent".getBytes();
        FileDTO attachmentFileDTO = FileDTO.builder()
            .fileName("name")
            .fileSize(contentBytes.length)
            .fileType("application/pdf")
            .fileContent(contentBytes)
            .build();

        String attachmentUuid = UUID.randomUUID().toString();
        FileUuidDTO fileUuidDTO = FileUuidDTO.builder().uuid(attachmentUuid).build();

        when(fileAttachmentService.createFileAttachment(attachmentFileDTO, FileStatus.PENDING, authUser))
            .thenReturn(attachmentUuid);
        when(requestTaskUploadAttachmentActionHandlerMapper.get(requestTaskActionType)).thenReturn(permitSectionUploadAttachmentHandler);

        //invoke
        FileUuidDTO result = requestTaskAttachmentUploadService
            .uploadAttachment(requestTaskId, requestTaskActionType, authUser, attachmentFileDTO);

        //verify
        assertEquals(fileUuidDTO, result);
        verify(fileAttachmentService, times(1))
            .createFileAttachment(attachmentFileDTO, FileStatus.PENDING, authUser);
        verify(requestTaskUploadAttachmentActionHandlerMapper, times(1)).get(requestTaskActionType);
        verify(permitSectionUploadAttachmentHandler, times(1))
            .uploadAttachment(requestTaskId, attachmentUuid, attachmentFileDTO.getFileName());
        verifyNoInteractions(appProperties);
    }
}