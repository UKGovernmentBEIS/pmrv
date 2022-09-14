package uk.gov.pmrv.api.files.attachments.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.common.domain.model.PmrvUser;
import uk.gov.pmrv.api.files.attachments.domain.FileAttachment;
import uk.gov.pmrv.api.files.attachments.repository.FileAttachmentRepository;
import uk.gov.pmrv.api.files.common.domain.FileStatus;
import uk.gov.pmrv.api.files.common.domain.dto.FileDTO;
import uk.gov.pmrv.api.files.common.service.FileScanValidatorService;
import uk.gov.pmrv.api.files.common.service.FileValidatorService;

@ExtendWith(MockitoExtension.class)
class FileAttachmentServiceTest {

    @InjectMocks
    private FileAttachmentService service;
    
    @Mock
    private FileAttachmentRepository fileAttachmentRepository;

    @Mock
    private FileScanValidatorService fileScanValidator;

    @Spy
    private ArrayList<FileValidatorService> fileValidators;

    @BeforeEach
    void setUp() {
        fileValidators.add(fileScanValidator);
    }

    @Test
    void createFileAttachment() throws IOException {
        byte[] contentBytes = "dummycontent".getBytes();
        FileDTO fileDTO = FileDTO.builder()
                .fileName("name")
                .fileSize(contentBytes.length)
                .fileType("application/pdf")
                .fileContent(contentBytes)
                .build();
        PmrvUser authUser = PmrvUser.builder().userId("user").build();
        FileStatus status = FileStatus.PENDING;
        
        String attachmentUuid = service.createFileAttachment(fileDTO, status, authUser);
        
        assertThat(attachmentUuid).isNotNull();
        ArgumentCaptor<FileAttachment> attachmentCaptor = ArgumentCaptor.forClass(FileAttachment.class);
        verify(fileAttachmentRepository, times(1)).save(attachmentCaptor.capture());
        FileAttachment attachmentCaptured = attachmentCaptor.getValue();
        assertThat(attachmentCaptured.getFileName()).isEqualTo(fileDTO.getFileName());
        assertThat(attachmentCaptured.getFileSize()).isEqualTo(fileDTO.getFileSize());
        assertThat(attachmentCaptured.getFileType()).isEqualTo(fileDTO.getFileType());
        assertThat(attachmentCaptured.getFileContent()).isEqualTo(contentBytes);
        assertThat(attachmentCaptured.getCreatedBy()).isEqualTo(authUser.getUserId());
        assertThat(attachmentCaptured.getStatus()).isEqualTo(status);
        assertThat(attachmentCaptured.getUuid()).isEqualTo(attachmentUuid);

        verify(fileScanValidator, times(1)).validate(fileDTO);
    }

    @Test
    void getFileDTO() throws IOException {
        String uuid = "uuid";
        FileAttachment fileAttachment = FileAttachment.builder()
            .fileName("name")
            .fileSize(121210)
            .fileType("application/pdf")
            .fileContent(new byte[]{})
            .build();

        when(fileAttachmentRepository.findByUuid(uuid)).thenReturn(Optional.of(fileAttachment));
        FileDTO fileDTO = service.getFileDTO("uuid");

        assertThat(fileDTO.getFileName()).isEqualTo(fileAttachment.getFileName());
        assertThat(fileDTO.getFileType()).isEqualTo(fileAttachment.getFileType());
        assertThat(fileDTO.getFileContent()).isEqualTo(fileAttachment.getFileContent());
    }

    @Test
    void updateFileAttachmentStatus() {
        String uuid = "uuid";
        FileAttachment fileAttachment = FileAttachment.builder()
            .fileName("name")
            .fileSize(121210)
            .fileType("application/pdf")
            .fileContent(new byte[]{})
            .status(FileStatus.PENDING)
            .build();

        when(fileAttachmentRepository.findByUuid(uuid)).thenReturn(Optional.of(fileAttachment));
        service.updateFileAttachmentStatus("uuid", FileStatus.SUBMITTED);

        ArgumentCaptor<FileAttachment> attachmentCaptor = ArgumentCaptor.forClass(FileAttachment.class);
        verify(fileAttachmentRepository, times(1)).save(attachmentCaptor.capture());
        FileAttachment attachmentCaptured = attachmentCaptor.getValue();
        assertThat(attachmentCaptured.getStatus()).isEqualTo(FileStatus.SUBMITTED);
    }

    @Test
    void deletePendingFileAttachment() {
        String uuid = "uuid";
        FileAttachment fileAttachment = FileAttachment.builder()
            .fileName("name")
            .fileSize(121210)
            .fileType("application/pdf")
            .fileContent(new byte[]{})
            .status(FileStatus.PENDING)
            .build();

        when(fileAttachmentRepository.findByUuid(uuid)).thenReturn(Optional.of(fileAttachment));
        boolean deleted = service.deletePendingFileAttachment("uuid");
        
        assertThat(deleted).isTrue();

        verify(fileAttachmentRepository, times(1)).delete(fileAttachment);
    }
    
    @Test
    void deletePendingFileAttachment_not_pending_status() {
        String uuid = "uuid";
        FileAttachment fileAttachment = FileAttachment.builder()
            .fileName("name")
            .fileSize(121210)
            .fileType("application/pdf")
            .fileContent(new byte[]{})
            .status(FileStatus.SUBMITTED)
            .build();

        when(fileAttachmentRepository.findByUuid(uuid)).thenReturn(Optional.of(fileAttachment));
        boolean deleted = service.deletePendingFileAttachment("uuid");
        
        assertThat(deleted).isFalse();

        verify(fileAttachmentRepository, never()).delete(fileAttachment);
    }
    
}
