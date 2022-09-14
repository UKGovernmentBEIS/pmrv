package uk.gov.pmrv.api.user.core.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import com.j256.simplemagic.ContentInfoUtil;

import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.files.common.domain.dto.FileDTO;
import uk.gov.pmrv.api.files.common.service.FileScanValidatorService;
import uk.gov.pmrv.api.files.common.service.FileValidatorService;

@ExtendWith(MockitoExtension.class)
public class UserSignatureValidatorServiceTest {

    @InjectMocks
    private UserSignatureValidatorService service;

    @Spy
    private ArrayList<FileValidatorService> fileValidators;
    
    @Mock
    private FileScanValidatorService fileScanValidator;

    @BeforeEach
    void setUp() {
        fileValidators.add(fileScanValidator);
    }
    
    @Test
    void validateSignature_valid() throws IOException {
        Path sampleFilePath = Paths.get("src", "test", "resources", "files", "signatures", "signature_valid.bmp");
        FileDTO signature = createFile(sampleFilePath);
        
        service.validateSignature(signature);
        
        verify(fileScanValidator, times(1)).validate(signature);
    }
    
    @Test
    void validateSignature_invalid_type() throws IOException {
        Path sampleFilePath = Paths.get("src", "test", "resources", "files", "sample.pdf");
        FileDTO signature = createFile(sampleFilePath);
        
        BusinessException be = assertThrows(BusinessException.class, () -> service.validateSignature(signature));
        assertThat(be.getErrorCode()).isEqualTo(ErrorCode.INVALID_FILE_TYPE);
        
        verifyNoInteractions(fileScanValidator);
    }

    @Test
    void validateSignature_invalid_size() throws IOException {
        Path sampleFilePath = Paths.get("src", "test", "resources", "files", "signatures", "signature_bad_size.bmp");
        FileDTO signature = createFile(sampleFilePath);
        
        BusinessException be = assertThrows(BusinessException.class, () -> service.validateSignature(signature));
        assertThat(be.getErrorCode()).isEqualTo(ErrorCode.MAX_FILE_SIZE_ERROR);
        
        verifyNoInteractions(fileScanValidator);
    }
    
    @Test
    void validateSignature_invalid_dimensions() throws IOException {
        Path sampleFilePath = Paths.get("src", "test", "resources", "files", "signatures", "signature_bad_dimensions.bmp");
        FileDTO signature = createFile(sampleFilePath);
        
        BusinessException be = assertThrows(BusinessException.class, () -> service.validateSignature(signature));
        assertThat(be.getErrorCode()).isEqualTo(ErrorCode.INVALID_IMAGE_DIMENSIONS);
        
        verifyNoInteractions(fileScanValidator);
    }
    
    private FileDTO createFile(Path sampleFilePath) throws IOException {
        byte[] bytes = Files.readAllBytes(sampleFilePath);
        return FileDTO.builder()
                .fileContent(bytes)
                .fileName(sampleFilePath.getFileName().toString())
                .fileSize(sampleFilePath.toFile().length())
                .fileType(new ContentInfoUtil().findMatch(bytes).getContentType().getMimeType())
                .build();
    }
}
