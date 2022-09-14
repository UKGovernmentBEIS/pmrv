package uk.gov.pmrv.api.files.common.service;

import uk.gov.pmrv.api.files.common.domain.dto.FileDTO;

import javax.validation.Valid;

public interface FileValidatorService {

    void validate(@Valid FileDTO fileDTO);
}
