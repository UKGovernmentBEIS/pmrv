package uk.gov.pmrv.api.files.common.service;

import org.junit.Before;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.TestPropertySource;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.files.common.FileTypesProperties;
import uk.gov.pmrv.api.files.common.domain.dto.FileDTO;

import java.util.List;

import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@TestPropertySource("classpath:application.properties")
class FileTypeValidatorServiceTest {
    @InjectMocks
    private FileTypeValidatorService service;

    @Mock
    FileTypesProperties fileTypesProperties;

    @Test
    void createFileDocument_invalid_file_type() {
        String filename = "filename.lala";
        String fileContent = "some content";
        FileDTO fileDTO = FileDTO.builder()
                .fileName(filename)
                .fileSize(2)
                .fileType("application/octet-stream")
                .fileContent(fileContent.getBytes())
                .build();

        BusinessException be = assertThrows(BusinessException.class,
                () -> service.validate(fileDTO));

        Assertions.assertEquals(ErrorCode.INVALID_FILE_TYPE, be.getErrorCode());
    }

    @Test
    void createFileDocument_valid_file_type() {
        String filename = "filename";
        String fileContent = "some content";
        FileDTO fileDTO = FileDTO.builder()
                .fileName(filename)
                .fileSize(2)
                .fileType("application/msword")
                .fileContent(fileContent.getBytes())
                .build();
        when(fileTypesProperties.getAllowedMimeTypes()).thenReturn(List.of("application/msword"));

        assertDoesNotThrow(() -> service.validate(fileDTO));
    }
}