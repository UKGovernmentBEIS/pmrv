package uk.gov.pmrv.api.notification.template.service;

import io.micrometer.core.annotation.Timed;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.files.common.domain.dto.FileDTO;
import uk.gov.pmrv.api.files.common.domain.dto.FileInfoDTO;
import uk.gov.pmrv.api.files.documents.service.FileDocumentService;
import uk.gov.pmrv.api.notification.template.domain.dto.templateparams.TemplateParams;
import uk.gov.pmrv.api.notification.template.domain.enumeration.DocumentTemplateType;

@Service
@RequiredArgsConstructor
public class DocumentFileGeneratorService {
    
    private final DocumentTemplateFileService documentTemplateFileService;
    private final DocumentTemplateProcessService documentTemplateProcessService;
    private final FileDocumentService fileDocumentService;
    
    @Transactional
    @Timed(value = "document.generation")
    public FileInfoDTO generateFileDocument(DocumentTemplateType type, TemplateParams templateParams, String fileNameToGenerate) {
        //get file document template
        final FileDTO fileDocumentTemplate = documentTemplateFileService.getFileDocumentTemplateByTypeAndCompetentAuthority(type, 
                templateParams.getCompetentAuthorityParams().getCompetentAuthority());
        
        //generate file from template
        final byte[] generatedFile;
        try {
            generatedFile = documentTemplateProcessService.generateFileDocumentFromTemplate(
                    fileDocumentTemplate, templateParams,fileNameToGenerate);
        } catch (DocumentTemplateProcessException e) {
            throw new BusinessException(ErrorCode.DOCUMENT_TEMPLATE_FILE_GENERATION_ERROR, fileDocumentTemplate.getFileName());
        }
        
        //persist file
        return fileDocumentService.createFileDocument(generatedFile, fileNameToGenerate);
    }

}
