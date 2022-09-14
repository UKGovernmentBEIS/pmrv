package uk.gov.pmrv.api.files.documents.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.files.common.FileType;
import uk.gov.pmrv.api.files.common.domain.FileStatus;
import uk.gov.pmrv.api.files.common.domain.dto.FileDTO;
import uk.gov.pmrv.api.files.common.domain.dto.FileInfoDTO;
import uk.gov.pmrv.api.files.common.service.FileScanValidatorService;
import uk.gov.pmrv.api.files.common.service.FileValidatorService;
import uk.gov.pmrv.api.files.documents.domain.FileDocumentTemplate;
import uk.gov.pmrv.api.files.documents.repository.FileDocumentTemplateRepository;

@ExtendWith(MockitoExtension.class)
class FileDocumentTemplateServiceTest {

    @InjectMocks
    private FileDocumentTemplateService service;

    @Mock
    private FileDocumentTemplateRepository fileDocumentTemplateRepository;

    @Mock
    private FileScanValidatorService fileScanValidator;

    @Spy
    private ArrayList<FileValidatorService> fileValidators;

    @BeforeEach
    void setUp() {
        fileValidators.add(fileScanValidator);
    }

    @Test
    void getFileInfoDocumentTemplateById() {
        Long fileDocumentId = 1L;
        
        String fileUuid = UUID.randomUUID().toString();
        String filename = "filename";

        FileDocumentTemplate fileDocumentTemplate = FileDocumentTemplate.builder()
            .id(fileDocumentId)
            .fileName(filename)
            .uuid(fileUuid)
            .status(FileStatus.SUBMITTED)
            .build();
        
        FileInfoDTO fileInfoDTO = FileInfoDTO.builder().uuid(fileUuid).name(filename).build();

        when(fileDocumentTemplateRepository.findById(fileDocumentId))
            .thenReturn(Optional.of((fileDocumentTemplate)));

        FileInfoDTO result = service.getFileInfoDocumentTemplateById(fileDocumentId);

        assertEquals(fileInfoDTO, result);
        verify(fileDocumentTemplateRepository, times(1)).findById(fileDocumentId);
    }

    @Test
    void getFileInfoDocumentTemplateById_not_found() {
        Long fileDocumentId = 1L;

        when(fileDocumentTemplateRepository.findById(fileDocumentId))
            .thenReturn(Optional.empty());

        BusinessException be = assertThrows(BusinessException.class,
            () -> service.getFileInfoDocumentTemplateById(fileDocumentId));
        assertThat(be.getErrorCode()).isEqualTo(ErrorCode.RESOURCE_NOT_FOUND);
    }
    
    @Test
    void getFileDocumentTemplateById() {
        Long fileDocumentTemplateId = 1L;
        String fileContent = "file_content";
        
        FileDocumentTemplate fileDocumentTemplate = FileDocumentTemplate.builder()
                .id(fileDocumentTemplateId)
                .fileName("filename")
                .fileContent(fileContent.getBytes())
                .uuid(UUID.randomUUID().toString())
                .fileType("fileType")
                .fileSize(fileContent.length())
                .status(FileStatus.SUBMITTED)
                .build();
        
        when(fileDocumentTemplateRepository.findById(fileDocumentTemplateId))
            .thenReturn(Optional.of((fileDocumentTemplate)));
        
        FileDTO result = service.getFileDocumentTemplateById(fileDocumentTemplateId);
        
        assertThat(result.getFileContent()).isEqualTo(fileContent.getBytes());
        assertThat(result.getFileName()).isEqualTo("filename");
        assertThat(result.getFileSize()).isEqualTo(fileContent.length());
        assertThat(result.getFileType()).isEqualTo("fileType");
        
        verify(fileDocumentTemplateRepository, times(1)).findById(fileDocumentTemplateId);
    }
    
    @Test
    void getFileDocumentTemplateById_not_found() {
        Long fileDocumentTemplateId = 1L;
        
        when(fileDocumentTemplateRepository.findById(fileDocumentTemplateId))
            .thenReturn(Optional.empty());
        
        BusinessException be = assertThrows(BusinessException.class,
                () -> service.getFileDocumentTemplateById(fileDocumentTemplateId));
            assertThat(be.getErrorCode()).isEqualTo(ErrorCode.RESOURCE_NOT_FOUND);
    }

    @Test
    void createFileDocumentTemplate() {
        String authUserId = "authUserId";
        String filename = "filename";
        String fileContent = "some content";
        FileDTO fileDTO = FileDTO.builder()
            .fileName(filename)
            .fileSize(2)
            .fileType(FileType.DOCX.getMimeTypes().iterator().next())
            .fileContent(fileContent.getBytes())
            .build();

        service.createFileDocumentTemplate(fileDTO, authUserId);

        ArgumentCaptor<FileDocumentTemplate> fileDocumentTemplateCaptor = ArgumentCaptor.forClass(FileDocumentTemplate.class);
        verify(fileDocumentTemplateRepository, times(1)).save(fileDocumentTemplateCaptor.capture());
        FileDocumentTemplate fileDocumentCaptured = fileDocumentTemplateCaptor.getValue();
        assertThat(fileDocumentCaptured.getFileName()).isEqualTo(fileDTO.getFileName());
        assertThat(fileDocumentCaptured.getFileSize()).isEqualTo(fileDTO.getFileSize());
        assertThat(fileDocumentCaptured.getFileType()).isEqualTo(fileDTO.getFileType());
        assertThat(fileDocumentCaptured.getCreatedBy()).isEqualTo(authUserId);
        assertThat(fileDocumentCaptured.getFileContent()).isEqualTo(fileDTO.getFileContent());
        assertThat(fileDocumentCaptured.getStatus()).isEqualTo(FileStatus.SUBMITTED);
        assertNotNull(fileDocumentCaptured.getUuid());

        verify(fileScanValidator, times(1)).validate(fileDTO);
    }

    @Test
    void createFileDocumentTemplate_invalid_file_type() {
        String authUserId = "authUserId";
        String filename = "filename";
        String fileContent = "some content";
        FileDTO fileDTO = FileDTO.builder()
            .fileName(filename)
            .fileSize(2)
            .fileType("application/pdf")
            .fileContent(fileContent.getBytes())
            .build();

        BusinessException be = assertThrows(BusinessException.class,
            () -> service.createFileDocumentTemplate(fileDTO, authUserId));

        assertEquals(ErrorCode.INVALID_FILE_TYPE, be.getErrorCode());

        verifyNoInteractions(fileDocumentTemplateRepository, fileScanValidator);
    }
    
    @Test
    void deleteFileDocumentTemplateById() {
        Long fileDocumentTemplateId = 1L;
        service.deleteFileDocumentTemplateById(fileDocumentTemplateId);
        
        verify(fileDocumentTemplateRepository, times(1)).deleteById(fileDocumentTemplateId);
    }
}