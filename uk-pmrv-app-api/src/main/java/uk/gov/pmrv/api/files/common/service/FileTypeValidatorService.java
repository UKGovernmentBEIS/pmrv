package uk.gov.pmrv.api.files.common.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.files.common.FileTypesProperties;
import uk.gov.pmrv.api.files.common.domain.dto.FileDTO;

import javax.validation.Valid;

@Component
@Validated
@RequiredArgsConstructor
public class FileTypeValidatorService implements FileValidatorService {
    private final FileTypesProperties fileTypesProperties;

    @Override
    public void validate(@Valid FileDTO fileDTO) {
        if (fileTypesProperties.getAllowedMimeTypes().stream()
                .noneMatch(mimeType -> mimeType.equals(fileDTO.getFileType()))) {
            throw new BusinessException(ErrorCode.INVALID_FILE_TYPE);
        }
    }
}
