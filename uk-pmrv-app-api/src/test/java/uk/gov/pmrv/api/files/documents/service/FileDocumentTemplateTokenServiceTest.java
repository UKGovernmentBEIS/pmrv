package uk.gov.pmrv.api.files.documents.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.files.common.domain.dto.FileDTO;
import uk.gov.pmrv.api.files.documents.domain.FileDocumentTemplate;
import uk.gov.pmrv.api.files.documents.repository.FileDocumentTemplateRepository;
import uk.gov.pmrv.api.user.core.domain.dto.FileToken;
import uk.gov.pmrv.api.user.core.service.UserFileTokenService;

@ExtendWith(MockitoExtension.class)
class FileDocumentTemplateTokenServiceTest {

    @InjectMocks
    private FileDocumentTemplateTokenService service;

    @Mock
    private FileDocumentTemplateRepository fileDocumentTemplateRepository;

    @Mock
    private UserFileTokenService userFileTokenService;

    @Test
    void generateGetFileDocumentTemplateToken() {
        String uuid = UUID.randomUUID().toString();
        FileToken expectedFileToken = FileToken.builder()
            .token("token")
            .tokenExpirationMinutes(10L)
            .build();

        when(fileDocumentTemplateRepository.existsByUuid(uuid)).thenReturn(true);
        when(userFileTokenService.generateGetFileToken(uuid))
            .thenReturn(expectedFileToken);

        FileToken result = service.generateGetFileDocumentTemplateToken(uuid);
        
        assertEquals(expectedFileToken, result);
        verify(userFileTokenService, times(1)).generateGetFileToken(uuid);
    }

    @Test
    void generateGetFileDocumentTemplateToken_not_found() {
        String uuid = UUID.randomUUID().toString();

        when(fileDocumentTemplateRepository.existsByUuid(uuid)).thenReturn(false);

        BusinessException be = assertThrows(BusinessException.class,
            () -> service.generateGetFileDocumentTemplateToken(uuid));

        assertEquals(ErrorCode.RESOURCE_NOT_FOUND, be.getErrorCode());
        verify(fileDocumentTemplateRepository, times(1)).existsByUuid(uuid);
        verifyNoInteractions(userFileTokenService);
    }

    @Test
    void getFileDTOByToken() throws IOException {
        String token = "token";
        String fileUuid = "fileUuid";
        String filename = "filename";
        FileDocumentTemplate fileDocumentTemplate = FileDocumentTemplate.builder()
            .fileName(filename)
            .fileContent(filename.getBytes())
            .fileSize(filename.length())
            .fileType("docx")
            .build();

        FileDTO expectedFileDTO = FileDTO.builder()
            .fileName(fileDocumentTemplate.getFileName())
            .fileContent(fileDocumentTemplate.getFileContent())
            .fileSize(fileDocumentTemplate.getFileSize())
            .fileType(fileDocumentTemplate.getFileType())
            .build();

        when(userFileTokenService.resolveGetFileUuid(token)).thenReturn(fileUuid);
        when(fileDocumentTemplateRepository.findByUuid(fileUuid)).thenReturn(Optional.of(fileDocumentTemplate));

        FileDTO result = service.getFileDTOByToken(token);
        assertEquals(expectedFileDTO, result);

        verify(userFileTokenService, times(1)).resolveGetFileUuid(token);
        verify(fileDocumentTemplateRepository, times(1)).findByUuid(fileUuid);
    }
}