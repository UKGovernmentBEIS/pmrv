package uk.gov.pmrv.api.notification.template.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import uk.gov.pmrv.api.common.domain.enumeration.CompetentAuthority;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.files.common.domain.dto.FileInfoDTO;
import uk.gov.pmrv.api.files.documents.service.FileDocumentTemplateService;
import uk.gov.pmrv.api.notification.template.domain.DocumentTemplate;
import uk.gov.pmrv.api.notification.template.domain.dto.DocumentTemplateDTO;
import uk.gov.pmrv.api.notification.template.domain.enumeration.DocumentTemplateType;
import uk.gov.pmrv.api.notification.template.domain.enumeration.TemplateOperatorType;
import uk.gov.pmrv.api.notification.template.repository.DocumentTemplateRepository;
import uk.gov.pmrv.api.notification.template.transform.DocumentTemplateMapper;
import uk.gov.pmrv.api.notification.template.transform.TemplateInfoMapper;

@ExtendWith({MockitoExtension.class})
class DocumentTemplateQueryServiceTest {

    @InjectMocks
    private DocumentTemplateQueryService service;

    @Mock
    private DocumentTemplateRepository documentTemplateRepository;

    @Mock
    private FileDocumentTemplateService fileDocumentTemplateService;
    
    private static DocumentTemplateMapper documentTemplateMapper = Mappers.getMapper(DocumentTemplateMapper.class);
    private static TemplateInfoMapper templateInfoMapper = Mappers.getMapper(TemplateInfoMapper.class);

    @BeforeEach
    public void init() {
        ReflectionTestUtils.setField(service, "documentTemplateMapper", documentTemplateMapper);
        ReflectionTestUtils.setField(documentTemplateMapper, "templateInfoMapper", templateInfoMapper);
    }
    
    @Test
    void getDocumentTemplateById() {
        Long documentTemplateId = 1L;
        String documentTemplateName = "Document template name";
        String workflow = "workflow";
        DocumentTemplate expectedDocumentTemplate = DocumentTemplate.builder()
                .id(documentTemplateId)
                .type(DocumentTemplateType.IN_RFI)
                .name(documentTemplateName)
                .workflow(workflow)
                .operatorType(TemplateOperatorType.INSTALLATION)
                .build();
        
        when(documentTemplateRepository.findById(documentTemplateId)).thenReturn(Optional.of(expectedDocumentTemplate));
        
        DocumentTemplate actualDocumentTemplate = service.getDocumentTemplateById(documentTemplateId);
        
        assertThat(actualDocumentTemplate).isEqualTo(expectedDocumentTemplate);
        verify(documentTemplateRepository, times(1)).findById(documentTemplateId);
    }
    
    @Test
    void getDocumentTemplateById_not_found() {
        Long documentTemplateId = 1L;
        
        when(documentTemplateRepository.findById(documentTemplateId)).thenReturn(Optional.empty());
        
        BusinessException be = assertThrows(BusinessException.class,
                () -> service.getDocumentTemplateById(documentTemplateId));

        assertThat(be.getErrorCode()).isEqualTo(ErrorCode.RESOURCE_NOT_FOUND);
        
        verify(documentTemplateRepository, times(1)).findById(documentTemplateId);
    }

    @Test
    void getDocumentTemplateDTOById() {
        Long id = 1L;
        Long fileDocumentTemplateId = 2L;
        String documentTemplateName = "Document template name";
        String workflow = "workflow";
        DocumentTemplate documentTemplate = DocumentTemplate.builder()
                .id(id)
                .type(DocumentTemplateType.IN_RFI)
                .name(documentTemplateName)
                .workflow(workflow)
                .operatorType(TemplateOperatorType.INSTALLATION)
                .fileDocumentTemplateId(fileDocumentTemplateId)
                .build();
        
        String fileUuid = UUID.randomUUID().toString();
        String filename = "filename";

        FileInfoDTO fileDocumentDTO = FileInfoDTO.builder().uuid(fileUuid).name(filename).build();
        
        DocumentTemplateDTO expectedDocumentTemplateDTO = DocumentTemplateDTO.builder()
            .id(id)
            .name(documentTemplateName)
            .workflow(workflow)
            .operatorType(TemplateOperatorType.INSTALLATION)
            .fileUuid(fileUuid)
            .filename(filename)
            .build();

        when(documentTemplateRepository.findById(id)).thenReturn(Optional.of(documentTemplate));
        when(fileDocumentTemplateService.getFileInfoDocumentTemplateById(fileDocumentTemplateId)).thenReturn(fileDocumentDTO);

        DocumentTemplateDTO result = service.getDocumentTemplateDTOById(id);
        assertThat(result).isEqualTo(expectedDocumentTemplateDTO);

        verify(documentTemplateRepository, times(1)).findById(id);
        verify(fileDocumentTemplateService, times(1)).getFileInfoDocumentTemplateById(fileDocumentTemplateId);
    }

    @Test
    void getDocumentTemplateDTOById_template_not_found() {
        Long documentTemplateId = 1L;

        when(documentTemplateRepository.findById(documentTemplateId)).thenReturn(Optional.empty());

        BusinessException be = assertThrows(BusinessException.class, () ->
            service.getDocumentTemplateById(documentTemplateId));

        assertThat(be.getErrorCode()).isEqualTo(ErrorCode.RESOURCE_NOT_FOUND);

        verify(documentTemplateRepository, times(1)).findById(documentTemplateId);
        verifyNoInteractions(fileDocumentTemplateService);
    }

    @Test
    void getDocumentTemplateCaById() {
        Long documentTemplateId = 1L;
        CompetentAuthority competentAuthority = CompetentAuthority.WALES;
        DocumentTemplate documentTemplate = DocumentTemplate.builder()
            .id(documentTemplateId)
            .type(DocumentTemplateType.IN_RFI)
            .competentAuthority(competentAuthority)
            .build();

        when(documentTemplateRepository.findById(documentTemplateId)).thenReturn(Optional.of(documentTemplate));

        assertEquals(competentAuthority, service.getDocumentTemplateCaById(documentTemplateId));
        verify(documentTemplateRepository, times(1)).findById(documentTemplateId);
    }

    @Test
    void getDocumentTemplateCaById_not_found() {
        Long documentTemplateId = 1L;

        when(documentTemplateRepository.findById(documentTemplateId)).thenReturn(Optional.empty());

        BusinessException be = assertThrows(BusinessException.class, () -> service.getDocumentTemplateDTOById(documentTemplateId));

        assertThat(be.getErrorCode()).isEqualTo(ErrorCode.RESOURCE_NOT_FOUND);
        verify(documentTemplateRepository, times(1)).findById(documentTemplateId);
    }
}