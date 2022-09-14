package uk.gov.pmrv.api.notification.template.repository.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.testcontainers.junit.jupiter.Testcontainers;
import uk.gov.pmrv.api.AbstractContainerBaseTest;
import uk.gov.pmrv.api.common.domain.enumeration.CompetentAuthority;
import uk.gov.pmrv.api.notification.template.domain.DocumentTemplate;
import uk.gov.pmrv.api.notification.template.domain.dto.DocumentTemplateSearchCriteria;
import uk.gov.pmrv.api.notification.template.domain.dto.TemplateInfoDTO;
import uk.gov.pmrv.api.notification.template.domain.dto.TemplateSearchResults;
import uk.gov.pmrv.api.notification.template.domain.enumeration.DocumentTemplateType;
import uk.gov.pmrv.api.notification.template.domain.enumeration.TemplateOperatorType;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@Testcontainers
@DataJpaTest
@Import(ObjectMapper.class)
class DocumentTemplateCustomRepositoryIT extends AbstractContainerBaseTest {

    @Autowired
    private DocumentTemplateCustomRepositoryImpl repo;

    @Autowired
    private EntityManager entityManager;

    @Test
    void findByCompetentAuthority_without_search_term() {
        String workflow = "workflow";
        CompetentAuthority competentAuthority = CompetentAuthority.ENGLAND;

        DocumentTemplate docTemplate1 = createDocumentTemplate(DocumentTemplateType.IN_RFI, "IN Request for Further Information", competentAuthority, workflow, 1L);
        createDocumentTemplate(DocumentTemplateType.IN_RFI, "IN Request for Further Information", CompetentAuthority.WALES, workflow, 2L);

        flushAndClear();

        DocumentTemplateSearchCriteria searchCriteria = DocumentTemplateSearchCriteria.builder()
            .page(0L)
            .pageSize(30L)
            .build();

        TemplateSearchResults
            searchResults = repo.findByCompetentAuthorityAndSearchCriteria(competentAuthority, searchCriteria);

        assertThat(searchResults.getTotal()).isEqualTo(1);

        List<TemplateInfoDTO> notificationTemplates = searchResults.getTemplates();
        assertThat(notificationTemplates).hasSize(1);
        assertThat(notificationTemplates).extracting(TemplateInfoDTO::getName)
            .containsExactly(
                docTemplate1.getName()
            );
    }

    @Test
    void findByCompetentAuthority_with_search_term_in_workflow() {
        CompetentAuthority competentAuthority = CompetentAuthority.ENGLAND;

        DocumentTemplate docTemplate1 = createDocumentTemplate(DocumentTemplateType.IN_RFI, "IN Request for Further Information", competentAuthority, "RFI sub-process", 1L);
        createDocumentTemplate(DocumentTemplateType.IN_RFI, "IN Request for Further Information", CompetentAuthority.WALES, "RFI sub-process", 2L);

        flushAndClear();

        DocumentTemplateSearchCriteria searchCriteria = DocumentTemplateSearchCriteria.builder()
            .term("process")
            .page(0L)
            .pageSize(30L)
            .build();

        TemplateSearchResults
            searchResults = repo.findByCompetentAuthorityAndSearchCriteria(competentAuthority, searchCriteria);

        assertThat(searchResults.getTotal()).isEqualTo(1);

        List<TemplateInfoDTO> notificationTemplates = searchResults.getTemplates();
        assertThat(notificationTemplates).hasSize(1);
        assertThat(notificationTemplates).extracting(TemplateInfoDTO::getName)
            .containsExactly(
                docTemplate1.getName()
            );
    }
    
    @Test
    void findByCompetentAuthority_with_search_term_in_name() {
        CompetentAuthority competentAuthority = CompetentAuthority.ENGLAND;

        DocumentTemplate docTemplate1 = createDocumentTemplate(DocumentTemplateType.IN_RFI, "IN Request for Further Information", competentAuthority, "RFI sub-process", 1L);
        createDocumentTemplate(DocumentTemplateType.IN_RFI, "IN Request for Further Information", CompetentAuthority.WALES, "RFI sub-process", 2L);

        flushAndClear();

        DocumentTemplateSearchCriteria searchCriteria = DocumentTemplateSearchCriteria.builder()
            .term("further")
            .page(0L)
            .pageSize(30L)
            .build();

        TemplateSearchResults
            searchResults = repo.findByCompetentAuthorityAndSearchCriteria(competentAuthority, searchCriteria);

        assertThat(searchResults.getTotal()).isEqualTo(1);

        List<TemplateInfoDTO> notificationTemplates = searchResults.getTemplates();
        assertThat(notificationTemplates).hasSize(1);
        assertThat(notificationTemplates).extracting(TemplateInfoDTO::getName)
            .containsExactly(
                docTemplate1.getName()
            );
    }

    private DocumentTemplate createDocumentTemplate(DocumentTemplateType type, String name, CompetentAuthority ca, String workflow, Long fileDocumentTemplateId) {
        DocumentTemplate documentTemplate = DocumentTemplate.builder()
            .type(type)
            .name(name)
            .competentAuthority(ca)
            .workflow(workflow)
            .operatorType(TemplateOperatorType.INSTALLATION)
            .lastUpdatedDate(LocalDateTime.now())
            .fileDocumentTemplateId(fileDocumentTemplateId)
            .build();

        entityManager.persist(documentTemplate);

        return documentTemplate;
    }

    private void flushAndClear() {
        entityManager.flush();
        entityManager.clear();
    }
}
