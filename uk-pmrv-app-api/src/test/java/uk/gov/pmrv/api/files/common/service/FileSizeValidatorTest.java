package uk.gov.pmrv.api.files.common.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.files.common.domain.dto.FileDTO;

class FileSizeValidatorTest {

    private final FileSizeValidatorService fileSizeValidator = new FileSizeValidatorService();

    @Test
    void validate_max_size_reached() {
        FileDTO fileDTO = createFileDTO(30000000);
        BusinessException exception = assertThrows(BusinessException.class, () ->
            fileSizeValidator.validate(fileDTO));

        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.MAX_FILE_SIZE_ERROR);
    }

    @Test
    void validate_zero_size() {
        FileDTO fileDTO = createFileDTO(0);
        BusinessException exception = assertThrows(BusinessException.class, () ->
            fileSizeValidator.validate(fileDTO));

        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.MIN_FILE_SIZE_ERROR);
    }

    private FileDTO createFileDTO(long fileSize) {
        return FileDTO.builder()
            .fileName("name")
            .fileSize(fileSize)
            .fileType("application/pdf")
            .fileContent(new byte[]{})
            .build();
    }
}