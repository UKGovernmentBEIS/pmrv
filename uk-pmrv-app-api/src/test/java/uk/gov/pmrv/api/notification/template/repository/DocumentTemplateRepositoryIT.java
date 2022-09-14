package uk.gov.pmrv.api.notification.template.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import javax.persistence.EntityManager;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.fasterxml.jackson.databind.ObjectMapper;

import uk.gov.pmrv.api.AbstractContainerBaseTest;
import uk.gov.pmrv.api.common.domain.enumeration.CompetentAuthority;
import uk.gov.pmrv.api.notification.template.domain.DocumentTemplate;
import uk.gov.pmrv.api.notification.template.domain.enumeration.DocumentTemplateType;
import uk.gov.pmrv.api.notification.template.domain.enumeration.TemplateOperatorType;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@Testcontainers
@DataJpaTest
@Import(ObjectMapper.class)
class DocumentTemplateRepositoryIT extends AbstractContainerBaseTest {

    @Autowired
    private DocumentTemplateRepository repo;

    @Autowired
    private EntityManager entityManager;

    @Test
    void findByTypeAndCompetentAuthority() {
        DocumentTemplate documentTemplate = DocumentTemplate.builder()
                .type(DocumentTemplateType.IN_RFI)
                .competentAuthority(CompetentAuthority.ENGLAND)
                .fileDocumentTemplateId(1L)
                .name("doc template name")
                .operatorType(TemplateOperatorType.INSTALLATION)
                .workflow("workflow")
                .build();
        
        entityManager.persist(documentTemplate);

        flushAndClear();
        
        Optional<DocumentTemplate> resultOpt = repo.findByTypeAndCompetentAuthority(DocumentTemplateType.IN_RFI, CompetentAuthority.ENGLAND);
        assertThat(resultOpt).isNotEmpty();
        assertThat(resultOpt.get().getName()).isEqualTo("doc template name");
    }
    
    private void flushAndClear() {
        entityManager.flush();
        entityManager.clear();
    }
}
