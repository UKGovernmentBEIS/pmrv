package uk.gov.pmrv.api.notification.template.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.pmrv.api.common.domain.enumeration.CompetentAuthority;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.files.common.domain.dto.FileDTO;
import uk.gov.pmrv.api.files.common.domain.dto.FileInfoDTO;
import uk.gov.pmrv.api.files.documents.service.FileDocumentService;
import uk.gov.pmrv.api.notification.template.domain.dto.templateparams.CompetentAuthorityTemplateParams;
import uk.gov.pmrv.api.notification.template.domain.dto.templateparams.TemplateParams;
import uk.gov.pmrv.api.notification.template.domain.enumeration.DocumentTemplateType;

@ExtendWith(MockitoExtension.class)
class DocumentFileGeneratorServiceTest {

    @InjectMocks
    private DocumentFileGeneratorService service;
    
    @Mock
    private DocumentTemplateFileService documentTemplateFileService;

    @Mock
    private DocumentTemplateProcessService documentTemplateProcessService;

    @Mock
    private FileDocumentService fileDocumentService;

    @Test
    void generateFileDocument() throws DocumentTemplateProcessException {
        CompetentAuthority ca = CompetentAuthority.ENGLAND;
        DocumentTemplateType type = DocumentTemplateType.IN_RFI;
        TemplateParams templateParams = TemplateParams.builder()
                .competentAuthorityParams(CompetentAuthorityTemplateParams.builder()
                        .competentAuthority(ca)
                        .build())
                .build();
        String fileNameToGenerate = "generatedFileName";
        
        FileDTO fileDocumentTemplate = FileDTO.builder()
                .fileName("fileDocTemplate")
                .fileContent("some content".getBytes())
                .fileType("some type")
                .fileSize("some content".length())
                .build();
        when(documentTemplateFileService.getFileDocumentTemplateByTypeAndCompetentAuthority(type, ca))
            .thenReturn(fileDocumentTemplate);
        
        byte[] generatedFileBytes = "generated file content".getBytes();
        when(documentTemplateProcessService.generateFileDocumentFromTemplate(fileDocumentTemplate, templateParams, fileNameToGenerate))
            .thenReturn(generatedFileBytes);
        
        UUID uuid = UUID.randomUUID();
        FileInfoDTO perstistedGeneratedFileInfo = FileInfoDTO.builder().name(fileNameToGenerate).uuid(uuid.toString()).build();
        when(fileDocumentService.createFileDocument(generatedFileBytes, fileNameToGenerate))
            .thenReturn(perstistedGeneratedFileInfo);
        
        //invoke
        FileInfoDTO result = service.generateFileDocument(type, templateParams, fileNameToGenerate);
        
        //assert
        assertThat(result).isEqualTo(perstistedGeneratedFileInfo);
        verify(documentTemplateFileService, times(1)).getFileDocumentTemplateByTypeAndCompetentAuthority(type, ca);
        verify(documentTemplateProcessService, times(1)).generateFileDocumentFromTemplate(fileDocumentTemplate, templateParams, fileNameToGenerate);
        verify(fileDocumentService, times(1)).createFileDocument(generatedFileBytes, fileNameToGenerate);
    }
    
    @Test
    void generateFileDocument_throws_business_exception_when_generate_file_fails() throws DocumentTemplateProcessException {
        CompetentAuthority ca = CompetentAuthority.ENGLAND;
        DocumentTemplateType type = DocumentTemplateType.IN_RFI;
        TemplateParams templateParams = TemplateParams.builder()
                .competentAuthorityParams(CompetentAuthorityTemplateParams.builder()
                        .competentAuthority(ca)
                        .build())
                .build();
        String fileNameToGenerate = "generatedFileName";
        
        FileDTO fileDocumentTemplate = FileDTO.builder()
                .fileName("fileDocTemplate")
                .fileContent("some content".getBytes())
                .fileType("some type")
                .fileSize("some content".length())
                .build();
        when(documentTemplateFileService.getFileDocumentTemplateByTypeAndCompetentAuthority(type, ca))
            .thenReturn(fileDocumentTemplate);
        
        when(documentTemplateProcessService.generateFileDocumentFromTemplate(fileDocumentTemplate, templateParams, fileNameToGenerate))
            .thenThrow(new DocumentTemplateProcessException("process failed"));
        
        //invoke
        BusinessException be = assertThrows(BusinessException.class, () -> {
            service.generateFileDocument(type, templateParams, fileNameToGenerate);    
        });
        
        
        //assert
        assertThat(be.getErrorCode()).isEqualTo(ErrorCode.DOCUMENT_TEMPLATE_FILE_GENERATION_ERROR);
        verify(documentTemplateFileService, times(1)).getFileDocumentTemplateByTypeAndCompetentAuthority(type, ca);
        verify(documentTemplateProcessService, times(1)).generateFileDocumentFromTemplate(fileDocumentTemplate, templateParams, fileNameToGenerate);
        verifyNoInteractions(fileDocumentService);
    }

}
