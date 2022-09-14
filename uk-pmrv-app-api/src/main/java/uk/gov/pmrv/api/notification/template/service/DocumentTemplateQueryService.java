package uk.gov.pmrv.api.notification.template.service;

import static uk.gov.pmrv.api.common.exception.ErrorCode.RESOURCE_NOT_FOUND;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import uk.gov.pmrv.api.authorization.rules.services.authorityinfo.providers.DocumentTemplateAuthorityInfoProvider;
import uk.gov.pmrv.api.common.domain.enumeration.CompetentAuthority;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.files.common.domain.dto.FileInfoDTO;
import uk.gov.pmrv.api.files.documents.service.FileDocumentTemplateService;
import uk.gov.pmrv.api.notification.template.domain.DocumentTemplate;
import uk.gov.pmrv.api.notification.template.domain.dto.DocumentTemplateDTO;
import uk.gov.pmrv.api.notification.template.domain.dto.DocumentTemplateSearchCriteria;
import uk.gov.pmrv.api.notification.template.domain.dto.TemplateSearchResults;
import uk.gov.pmrv.api.notification.template.repository.DocumentTemplateRepository;
import uk.gov.pmrv.api.notification.template.transform.DocumentTemplateMapper;

@Service
@RequiredArgsConstructor
public class DocumentTemplateQueryService implements DocumentTemplateAuthorityInfoProvider {

    private final DocumentTemplateRepository documentTemplateRepository;
    private final FileDocumentTemplateService fileDocumentTemplateService;
    private final DocumentTemplateMapper documentTemplateMapper;
    
    DocumentTemplate getDocumentTemplateById(Long id) {
        return documentTemplateRepository.findById(id)
            .orElseThrow(() -> new BusinessException(RESOURCE_NOT_FOUND));
    }

    public TemplateSearchResults getDocumentTemplatesByCaAndSearchCriteria(CompetentAuthority competentAuthority,
                                                                               DocumentTemplateSearchCriteria searchCriteria) {
        return documentTemplateRepository.findByCompetentAuthorityAndSearchCriteria(competentAuthority, searchCriteria);
    }

    @Transactional(readOnly = true)
    public DocumentTemplateDTO getDocumentTemplateDTOById(Long id) {
        DocumentTemplate documentTemplate = getDocumentTemplateById(id);
        FileInfoDTO fileInfoDTO = fileDocumentTemplateService.getFileInfoDocumentTemplateById(documentTemplate.getFileDocumentTemplateId());
        return documentTemplateMapper.toDocumentTemplateDTO(documentTemplate, fileInfoDTO);
    }

    @Override
    public CompetentAuthority getDocumentTemplateCaById(Long id) {
        return getDocumentTemplateById(id).getCompetentAuthority();
    }
}
