package uk.gov.pmrv.api.files.documents.service;

import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.files.common.domain.dto.FileDTO;
import uk.gov.pmrv.api.files.common.transform.FileMapper;
import uk.gov.pmrv.api.files.documents.repository.FileDocumentTemplateRepository;
import uk.gov.pmrv.api.user.core.domain.dto.FileToken;
import uk.gov.pmrv.api.user.core.service.UserFileTokenService;

@Service
@RequiredArgsConstructor
public class FileDocumentTemplateTokenService {

    private final FileDocumentTemplateRepository fileDocumentTemplateRepository;
    private final UserFileTokenService userFileTokenService;
    private static final FileMapper fileMapper = Mappers.getMapper(FileMapper.class);

    public FileToken generateGetFileDocumentTemplateToken(String fileUuid) {
        if(!fileDocumentTemplateRepository.existsByUuid(fileUuid)) {
            throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, fileUuid);
        }

        return userFileTokenService.generateGetFileToken(fileUuid);
    }

    @Transactional(readOnly = true)
    public FileDTO getFileDTOByToken(String getFileToken) {
        String fileUuid = userFileTokenService.resolveGetFileUuid(getFileToken);
        return fileDocumentTemplateRepository.findByUuid(fileUuid)
                .map(fileMapper::toFileDTO)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND));
    }
}
