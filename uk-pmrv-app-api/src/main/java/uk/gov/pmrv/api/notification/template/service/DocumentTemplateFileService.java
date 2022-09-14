package uk.gov.pmrv.api.notification.template.service;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import uk.gov.pmrv.api.common.domain.enumeration.CompetentAuthority;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.files.common.domain.dto.FileDTO;
import uk.gov.pmrv.api.files.common.domain.dto.FileInfoDTO;
import uk.gov.pmrv.api.files.documents.service.FileDocumentTemplateService;
import uk.gov.pmrv.api.files.documents.service.FileDocumentTemplateTokenService;
import uk.gov.pmrv.api.notification.template.domain.DocumentTemplate;
import uk.gov.pmrv.api.notification.template.domain.enumeration.DocumentTemplateType;
import uk.gov.pmrv.api.notification.template.repository.DocumentTemplateRepository;
import uk.gov.pmrv.api.user.core.domain.dto.FileToken;

@Service
@RequiredArgsConstructor
public class DocumentTemplateFileService {

    private final DocumentTemplateQueryService documentTemplateQueryService;
    private final DocumentTemplateRepository documentTemplateRepository;
    private final FileDocumentTemplateService fileDocumentTemplateService;
    private final FileDocumentTemplateTokenService fileDocumentTemplateTokenService;

    @Transactional
    public FileToken generateGetFileDocumentTemplateToken(Long documentTemplateId, UUID fileUuid) {
        DocumentTemplate documentTemplate = documentTemplateQueryService.getDocumentTemplateById(documentTemplateId);
        validateFileDocumentTemplate(fileUuid, documentTemplate);
        
        return fileDocumentTemplateTokenService.generateGetFileDocumentTemplateToken(fileUuid.toString());
    }
    
    public FileDTO getFileDocumentTemplateByTypeAndCompetentAuthority(DocumentTemplateType type,
            CompetentAuthority competentAuthority) {
        DocumentTemplate documentTemplate = documentTemplateRepository.findByTypeAndCompetentAuthority(type, competentAuthority)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND));
        
        return fileDocumentTemplateService.getFileDocumentTemplateById(documentTemplate.getFileDocumentTemplateId());
    }

    private void validateFileDocumentTemplate(UUID fileUuid, DocumentTemplate documentTemplate) {
        final FileInfoDTO fileDocumentTemplate = fileDocumentTemplateService.getFileInfoDocumentTemplateById(documentTemplate.getFileDocumentTemplateId());
        
        if(!fileDocumentTemplate.getUuid().equals(fileUuid.toString())) {
            throw new BusinessException(ErrorCode.DOCUMENT_TEMPLATE_FILE_NOT_FOUND);
        }
    }
}
