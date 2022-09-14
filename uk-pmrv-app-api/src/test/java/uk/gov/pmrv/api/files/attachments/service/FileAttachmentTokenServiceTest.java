package uk.gov.pmrv.api.files.attachments.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.io.IOException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.files.attachments.domain.FileAttachment;
import uk.gov.pmrv.api.user.core.domain.dto.FileToken;
import uk.gov.pmrv.api.files.common.domain.dto.FileDTO;
import uk.gov.pmrv.api.user.core.service.UserFileTokenService;

@ExtendWith(MockitoExtension.class)
class FileAttachmentTokenServiceTest {
    
    @InjectMocks
    private FileAttachmentTokenService service;
    
    @Mock
    private FileAttachmentService fileAttachmentService;
    
    @Mock
    private UserFileTokenService userFileTokenService;
    
    @Test
    void generateGetFileAttachmentToken() {
        String attachmentUuid = "attachmentUuid";
        FileToken fileToken = FileToken.builder()
                .token("roken")
                .tokenExpirationMinutes(1l)
                .build();
        when(fileAttachmentService.fileAttachmentExist(attachmentUuid)).thenReturn(true);
        when(userFileTokenService.generateGetFileToken(attachmentUuid))
            .thenReturn(fileToken);
        
        FileToken result = service.generateGetFileAttachmentToken(attachmentUuid);
        assertThat(result).isEqualTo(fileToken);
        verify(fileAttachmentService, times(1)).fileAttachmentExist(attachmentUuid);
        verify(userFileTokenService, times(1)).generateGetFileToken(attachmentUuid);
    }
    
    @Test
    void generateGetFileAttachmentToken_attachment_not_found() {
        String attachmentUuid = "attachmentUuid";
        when(fileAttachmentService.fileAttachmentExist(attachmentUuid)).thenReturn(false);
        
        BusinessException be = assertThrows(BusinessException.class, () -> {
            service.generateGetFileAttachmentToken(attachmentUuid);
        });
        assertThat(be.getErrorCode()).isEqualTo(ErrorCode.RESOURCE_NOT_FOUND);
        
        verify(fileAttachmentService, times(1)).fileAttachmentExist(attachmentUuid);
        verifyNoInteractions(userFileTokenService);
    }
    
    @Test
    void getFileDTOByToken() throws IOException {
        String getFileAttachmentToken = "token";
        String fileAttachmentUuid = "fileAttachmentUuid";
        FileAttachment fileAttachment = FileAttachment.builder()
                .fileName("file")
                .fileContent("content".getBytes())
                .fileSize(1l)
                .fileType("type")
                .build();
        
        FileDTO fileDTO = FileDTO.builder().fileName(fileAttachment.getFileName())
                .fileContent(fileAttachment.getFileContent())
                .fileSize(fileAttachment.getFileSize())
                .fileType(fileAttachment.getFileType())
                .build();
        
        when(userFileTokenService.resolveGetFileUuid(getFileAttachmentToken))
            .thenReturn(fileAttachmentUuid);
        when(fileAttachmentService.getFileDTO(fileAttachmentUuid))
            .thenReturn(fileDTO);
        
        FileDTO result = service.getFileDTOByToken(getFileAttachmentToken);
        assertThat(result.getFileContent()).isEqualTo(fileAttachment.getFileContent());
        assertThat(result.getFileName()).isEqualTo(fileAttachment.getFileName());
        assertThat(result.getFileSize()).isEqualTo(fileAttachment.getFileSize());
        assertThat(result.getFileType()).isEqualTo(fileAttachment.getFileType());
        
        verify(userFileTokenService, times(1)).resolveGetFileUuid(getFileAttachmentToken);
        verify(fileAttachmentService, times(1)).getFileDTO(fileAttachmentUuid);
    }
    
}
