package uk.gov.pmrv.api.files.common.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;
import uk.gov.pmrv.api.files.common.domain.dto.FileDTO;

import javax.validation.Valid;
import java.io.ByteArrayInputStream;

@Component
@Validated
@RequiredArgsConstructor
public class FileScanValidatorService implements FileValidatorService {

    private final FileScanService fileScanService;

    @Override
    public void validate(@Valid FileDTO fileDTO) {
        fileScanService.scan(new ByteArrayInputStream(fileDTO.getFileContent()));
    }
}
