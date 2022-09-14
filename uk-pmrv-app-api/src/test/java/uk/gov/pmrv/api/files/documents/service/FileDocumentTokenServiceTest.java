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
import uk.gov.pmrv.api.files.documents.domain.FileDocument;
import uk.gov.pmrv.api.files.documents.repository.FileDocumentRepository;
import uk.gov.pmrv.api.user.core.domain.dto.FileToken;
import uk.gov.pmrv.api.user.core.service.UserFileTokenService;

@ExtendWith(MockitoExtension.class)
class FileDocumentTokenServiceTest {

    @InjectMocks
    private FileDocumentTokenService service;

    @Mock
    private FileDocumentRepository fileDocumentRepository;

    @Mock
    private UserFileTokenService userFileTokenService;

    @Test
    void generateGetFileDocumentToken() {
        String uuid = UUID.randomUUID().toString();
        FileToken expectedFileToken = FileToken.builder()
            .token("token")
            .tokenExpirationMinutes(10L)
            .build();

        when(fileDocumentRepository.existsByUuid(uuid)).thenReturn(true);
        when(userFileTokenService.generateGetFileToken(uuid))
            .thenReturn(expectedFileToken);

        FileToken result = service.generateGetFileDocumentToken(uuid);
        
        assertEquals(expectedFileToken, result);
        verify(userFileTokenService, times(1)).generateGetFileToken(uuid);
    }

    @Test
    void generateGetFileDocumentToken_not_found() {
        String uuid = UUID.randomUUID().toString();

        when(fileDocumentRepository.existsByUuid(uuid)).thenReturn(false);

        BusinessException be = assertThrows(BusinessException.class,
            () -> service.generateGetFileDocumentToken(uuid));

        assertEquals(ErrorCode.RESOURCE_NOT_FOUND, be.getErrorCode());
        verify(fileDocumentRepository, times(1)).existsByUuid(uuid);
        verifyNoInteractions(userFileTokenService);
    }

    @Test
    void getFileDTOByToken() throws IOException {
        String token = "token";
        String fileUuid = "fileUuid";
        String filename = "filename";
        FileDocument fileDocument = FileDocument.builder()
            .fileName(filename)
            .fileContent(filename.getBytes())
            .fileSize(filename.length())
            .fileType("docx")
            .build();

        FileDTO expectedFileDTO = FileDTO.builder()
            .fileName(fileDocument.getFileName())
            .fileContent(fileDocument.getFileContent())
            .fileSize(fileDocument.getFileSize())
            .fileType(fileDocument.getFileType())
            .build();

        when(userFileTokenService.resolveGetFileUuid(token)).thenReturn(fileUuid);
        when(fileDocumentRepository.findByUuid(fileUuid)).thenReturn(Optional.of(fileDocument));

        FileDTO result = service.getFileDTOByToken(token);
        assertEquals(expectedFileDTO, result);

        verify(userFileTokenService, times(1)).resolveGetFileUuid(token);
        verify(fileDocumentRepository, times(1)).findByUuid(fileUuid);
    }
}