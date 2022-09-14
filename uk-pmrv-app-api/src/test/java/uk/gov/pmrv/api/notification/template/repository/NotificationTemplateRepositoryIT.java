package uk.gov.pmrv.api.notification.template.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.testcontainers.junit.jupiter.Testcontainers;
import uk.gov.pmrv.api.AbstractContainerBaseTest;
import uk.gov.pmrv.api.common.domain.enumeration.CompetentAuthority;
import uk.gov.pmrv.api.common.domain.enumeration.RoleType;
import uk.gov.pmrv.api.notification.template.domain.DocumentTemplate;
import uk.gov.pmrv.api.notification.template.domain.NotificationTemplate;
import uk.gov.pmrv.api.notification.template.domain.enumeration.DocumentTemplateType;
import uk.gov.pmrv.api.notification.template.domain.enumeration.NotificationTemplateName;
import uk.gov.pmrv.api.notification.template.domain.enumeration.TemplateOperatorType;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@Testcontainers
@DataJpaTest
@Import(ObjectMapper.class)
class NotificationTemplateRepositoryIT extends AbstractContainerBaseTest {

    @Autowired
    private NotificationTemplateRepository repo;

    @Autowired
    private EntityManager entityManager;
    
    @Test
    void findByNameAndCompetentAuthority() {
        Optional<NotificationTemplate> result = repo.findByNameAndCompetentAuthority(NotificationTemplateName.INVITATION_TO_OPERATOR_ACCOUNT, CompetentAuthority.ENGLAND);
        assertThat(result).isEmpty();
        
        NotificationTemplate notificationTemplate = createNotificationTemplate(
                NotificationTemplateName.INVITATION_TO_OPERATOR_ACCOUNT,
                CompetentAuthority.ENGLAND,
                "subject",
                "content",
                "workflow",
                true,
                TemplateOperatorType.INSTALLATION);
        
        entityManager.persist(notificationTemplate);
        flushAndClear();
        
        result = repo.findByNameAndCompetentAuthority(NotificationTemplateName.INVITATION_TO_OPERATOR_ACCOUNT, CompetentAuthority.ENGLAND);
        assertThat(result).isNotEmpty();
    }
    
    @Test
    void findByNameAndCompetentAuthority_empty_ca() {
        Optional<NotificationTemplate> result = repo.findByNameAndCompetentAuthority(NotificationTemplateName.INVITATION_TO_OPERATOR_ACCOUNT, null);
        assertThat(result).isEmpty();
        
        NotificationTemplate notificationTemplate = createNotificationTemplate(
                NotificationTemplateName.INVITATION_TO_OPERATOR_ACCOUNT,
                null,
                "subject",
                "content",
                "workflow",
                true,
                TemplateOperatorType.INSTALLATION);
        
        entityManager.persist(notificationTemplate);
        flushAndClear();
        
        result = repo.findByNameAndCompetentAuthority(NotificationTemplateName.INVITATION_TO_OPERATOR_ACCOUNT, null);
        assertThat(result).isNotEmpty();
    }

    @Test
    void findManagedNotificationTemplateByIdWithDocumentTemplates() {
        String permitWorkflow = "Permit Workflow";
        NotificationTemplate notificationTemplate1 = createNotificationTemplate(
            NotificationTemplateName.INVITATION_TO_OPERATOR_ACCOUNT,
            CompetentAuthority.WALES,
            "Invitation To Operator Account",
            "Invitation To Operator Account",
            permitWorkflow,
            true,
            TemplateOperatorType.INSTALLATION);

        DocumentTemplate documentTemplate1 = createDocumentTemplate(DocumentTemplateType.IN_RFI, "Operator Account Doc", CompetentAuthority.WALES, permitWorkflow, TemplateOperatorType.INSTALLATION, 1L);

        addDocumentTemplateToNotificationTemplate(documentTemplate1, notificationTemplate1);

        NotificationTemplate notificationTemplate2 = createNotificationTemplate(
            NotificationTemplateName.INVITATION_TO_VERIFIER_ACCOUNT,
            CompetentAuthority.WALES,
            "Invitation To Verifier Account",
            "Invitation To Verifier Account",
            permitWorkflow,
            true,
            TemplateOperatorType.INSTALLATION);

        DocumentTemplate documentTemplate2 = createDocumentTemplate(DocumentTemplateType.IN_RFI, "Verifier Account Doc", CompetentAuthority.WALES, permitWorkflow, TemplateOperatorType.INSTALLATION, 2L);

        addDocumentTemplateToNotificationTemplate(documentTemplate2, notificationTemplate2);

        entityManager.persist(notificationTemplate1);
        entityManager.persist(notificationTemplate2);
        entityManager.persist(documentTemplate1);
        entityManager.persist(documentTemplate2);

        flushAndClear();

        Optional<NotificationTemplate> optionalNotificationTemplate =
            repo.findManagedNotificationTemplateByIdWithDocumentTemplates(notificationTemplate1.getId());

        assertThat(optionalNotificationTemplate).isNotEmpty();

        NotificationTemplate notificationTemplate = optionalNotificationTemplate.get();
        assertEquals(NotificationTemplateName.INVITATION_TO_OPERATOR_ACCOUNT, notificationTemplate.getName());

        Set<DocumentTemplate> documentTemplates = notificationTemplate.getDocumentTemplates();
        assertThat(documentTemplates).hasSize(1);
        assertThat(documentTemplates).containsExactlyInAnyOrder(documentTemplate1);
    }

    @Test
    void findManagedNotificationTemplateByIdWithDocumentTemplates_not_managed() {
        String permitWorkflow = "Permit Workflow";
        NotificationTemplate notificationTemplate1 = createNotificationTemplate(
            NotificationTemplateName.INVITATION_TO_OPERATOR_ACCOUNT,
            CompetentAuthority.WALES,
            "Invitation To Operator Account",
            "Invitation To Operator Account",
            permitWorkflow,
            false,
            TemplateOperatorType.INSTALLATION);

        entityManager.persist(notificationTemplate1);

        flushAndClear();

        Optional<NotificationTemplate> optionalNotificationTemplate =
            repo.findManagedNotificationTemplateByIdWithDocumentTemplates(notificationTemplate1.getId());

        assertThat(optionalNotificationTemplate).isEmpty();
    }

    private NotificationTemplate createNotificationTemplate(NotificationTemplateName name, CompetentAuthority ca, String subject, String text,
                                                            String workflow, boolean managed, TemplateOperatorType operatorType) {
        return NotificationTemplate.builder()
            .name(name)
            .subject(subject)
            .text(text)
            .competentAuthority(ca)
            .workflow(workflow)
            .roleType(RoleType.OPERATOR)
            .managed(managed)
            .operatorType(operatorType)
            .lastUpdatedDate(LocalDateTime.now())
            .build();
    }

    private DocumentTemplate createDocumentTemplate(DocumentTemplateType type, String name, CompetentAuthority ca, String workflow, TemplateOperatorType operatorType, Long fileDocumentTemplateId) {
        return DocumentTemplate.builder()
                .type(type)
                .name(name)
                .competentAuthority(ca)
                .workflow(workflow)
                .operatorType(operatorType)
                .lastUpdatedDate(LocalDateTime.now())
                .fileDocumentTemplateId(fileDocumentTemplateId)
                .build();
    }

    private void addDocumentTemplateToNotificationTemplate(DocumentTemplate documentTemplate, NotificationTemplate notificationTemplate) {
        notificationTemplate.getDocumentTemplates().add(documentTemplate);
        documentTemplate.getNotificationTemplates().add(notificationTemplate);
    }

    private void flushAndClear() {
        entityManager.flush();
        entityManager.clear();
    }
}