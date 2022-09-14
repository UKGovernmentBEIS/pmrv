package uk.gov.pmrv.api.notification.template.repository.impl;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import java.util.List;
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
import uk.gov.pmrv.api.notification.template.domain.NotificationTemplate;
import uk.gov.pmrv.api.notification.template.domain.dto.TemplateInfoDTO;
import uk.gov.pmrv.api.notification.template.domain.dto.NotificationTemplateSearchCriteria;
import uk.gov.pmrv.api.notification.template.domain.dto.TemplateSearchResults;
import uk.gov.pmrv.api.notification.template.domain.enumeration.NotificationTemplateName;
import uk.gov.pmrv.api.notification.template.domain.enumeration.TemplateOperatorType;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@Testcontainers
@DataJpaTest
@Import(ObjectMapper.class)
class NotificationTemplateCustomRepositoryIT extends AbstractContainerBaseTest {

    @Autowired
    private NotificationTemplateCustomRepositoryImpl repo;

    @Autowired
    private EntityManager entityManager;

    @Test
    void findByCompetentAuthority_with_search_term() {
        String permitWorkflow = "Permit Workflow";
        String accountOpeningWorkflow = "Account Opening Workflow";
        CompetentAuthority competentAuthority = CompetentAuthority.ENGLAND;

        NotificationTemplate notificationTemplate1 = createNotificationTemplate(
            NotificationTemplateName.INVITATION_TO_REGULATOR_ACCOUNT, competentAuthority, permitWorkflow, RoleType.OPERATOR, true);
        NotificationTemplate notificationTemplate2 = createNotificationTemplate(
            NotificationTemplateName.INVITATION_TO_OPERATOR_ACCOUNT, competentAuthority, permitWorkflow, RoleType.OPERATOR, true);
        NotificationTemplate notificationTemplate3 = createNotificationTemplate(
            NotificationTemplateName.USER_ACCOUNT_CREATED, competentAuthority, accountOpeningWorkflow, RoleType.OPERATOR, true);
        NotificationTemplate notificationTemplate4 = createNotificationTemplate(
            NotificationTemplateName.USER_ACCOUNT_ACTIVATION, competentAuthority, accountOpeningWorkflow, RoleType.REGULATOR, true);
        NotificationTemplate notificationTemplate5 = createNotificationTemplate(
            NotificationTemplateName.USER_ACCOUNT_ACTIVATION, CompetentAuthority.WALES, accountOpeningWorkflow, RoleType.OPERATOR, true);
        NotificationTemplate notificationTemplate6 = createNotificationTemplate(
            NotificationTemplateName.CHANGE_2FA, null, null, null, false);

        flushAndClear();

        NotificationTemplateSearchCriteria searchCriteria = NotificationTemplateSearchCriteria.builder()
            .roleType(RoleType.OPERATOR)
            .term("account")
            .page(0L)
            .pageSize(30L)
            .build();

        TemplateSearchResults
            searchResults = repo.findByCompetentAuthorityAndSearchCriteria(competentAuthority, searchCriteria);

        assertThat(searchResults.getTotal()).isEqualTo(3);

        List<TemplateInfoDTO> notificationTemplates = searchResults.getTemplates();
        assertThat(notificationTemplates).hasSize(3);
        assertThat(notificationTemplates).extracting(TemplateInfoDTO::getName)
            .containsExactly(
                notificationTemplate2.getName().getName(),
                notificationTemplate1.getName().getName(),
                notificationTemplate3.getName().getName()
            );
    }

    @Test
    void findByCompetentAuthority_without_search_term() {
        String permitWorkflow = "Permit Workflow";
        String accountOpeningWorkflow = "Account Opening Workflow";
        CompetentAuthority competentAuthority = CompetentAuthority.ENGLAND;

        NotificationTemplate notificationTemplate1 = createNotificationTemplate(
            NotificationTemplateName.INVITATION_TO_EMITTER_CONTACT, competentAuthority, permitWorkflow, RoleType.OPERATOR, true);
        NotificationTemplate notificationTemplate2 = createNotificationTemplate(
            NotificationTemplateName.USER_ACCOUNT_CREATED, competentAuthority, accountOpeningWorkflow, RoleType.REGULATOR, true);
        NotificationTemplate notificationTemplate3 = createNotificationTemplate(
            NotificationTemplateName.USER_ACCOUNT_ACTIVATION, competentAuthority, accountOpeningWorkflow, RoleType.REGULATOR, true);
        NotificationTemplate notificationTemplate4 = createNotificationTemplate(
            NotificationTemplateName.USER_ACCOUNT_ACTIVATION, CompetentAuthority.WALES, accountOpeningWorkflow, RoleType.OPERATOR, true);
        NotificationTemplate notificationTemplate5 = createNotificationTemplate(
            NotificationTemplateName.CHANGE_2FA, null, null, null, false);

        flushAndClear();

        NotificationTemplateSearchCriteria searchCriteria = NotificationTemplateSearchCriteria.builder()
            .roleType(RoleType.REGULATOR)
            .page(0L)
            .pageSize(30L)
            .build();

        TemplateSearchResults
            searchResults = repo.findByCompetentAuthorityAndSearchCriteria(competentAuthority, searchCriteria);

        assertThat(searchResults.getTotal()).isEqualTo(2);

        List<TemplateInfoDTO> notificationTemplates = searchResults.getTemplates();
        assertThat(notificationTemplates).hasSize(2);
        assertThat(notificationTemplates).extracting(TemplateInfoDTO::getName)
            .containsExactly(
                notificationTemplate3.getName().getName(),
                notificationTemplate2.getName().getName()
            );
    }


    private NotificationTemplate createNotificationTemplate(NotificationTemplateName name, CompetentAuthority ca, String workflow,
                                                            RoleType roleType, boolean managed) {
        NotificationTemplate notificationTemplate = NotificationTemplate.builder()
            .name(name)
            .subject("subject")
            .text("text")
            .competentAuthority(ca)
            .workflow(workflow)
            .roleType(roleType)
            .managed(managed)
            .operatorType(TemplateOperatorType.INSTALLATION)
            .lastUpdatedDate(LocalDateTime.now())
            .build();

        entityManager.persist(notificationTemplate);

        return notificationTemplate;
    }

    private void flushAndClear() {
        entityManager.flush();
        entityManager.clear();
    }
}