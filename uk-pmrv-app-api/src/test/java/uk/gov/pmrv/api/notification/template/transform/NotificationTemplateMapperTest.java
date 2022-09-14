package uk.gov.pmrv.api.notification.template.transform;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.LocalDateTime;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import uk.gov.pmrv.api.common.domain.enumeration.CompetentAuthority;
import uk.gov.pmrv.api.common.domain.enumeration.RoleType;
import uk.gov.pmrv.api.notification.template.domain.DocumentTemplate;
import uk.gov.pmrv.api.notification.template.domain.NotificationTemplate;
import uk.gov.pmrv.api.notification.template.domain.dto.NotificationTemplateDTO;
import uk.gov.pmrv.api.notification.template.domain.dto.TemplateInfoDTO;
import uk.gov.pmrv.api.notification.template.domain.enumeration.DocumentTemplateType;
import uk.gov.pmrv.api.notification.template.domain.enumeration.NotificationTemplateName;
import uk.gov.pmrv.api.notification.template.domain.enumeration.TemplateOperatorType;

@ExtendWith(MockitoExtension.class)
@SpringBootTest(classes = {NotificationTemplateMapperImpl.class, TemplateInfoMapperImpl.class})
class NotificationTemplateMapperTest {

    @Autowired
    private NotificationTemplateMapper notificationTemplateMapper;

    @Test
    void toNotificationTemplateDTO() {
        Long notificationTemplateId = 1L;
        NotificationTemplateName notificationTemplateName = NotificationTemplateName.EMAIL_CONFIRMATION;
        String notificationTemplateSubject = "notification template subject";
        String notificationTemplateText = "notification template text";
        String workflow = " workflow";
        String eventTrigger = "event trigger the notification";
        Long firstDocumentTemplateId = 11L;
        String firstDocumentTemplateName = "first document template";

        DocumentTemplate documentTemplate1 = createDocumentTemplate(firstDocumentTemplateId, firstDocumentTemplateName);
        NotificationTemplate notificationTemplate = NotificationTemplate.builder()
            .id(notificationTemplateId)
            .name(notificationTemplateName)
            .subject(notificationTemplateSubject)
            .text(notificationTemplateText)
            .competentAuthority(CompetentAuthority.WALES)
            .workflow(workflow)
            .roleType(RoleType.OPERATOR)
            .eventTrigger(eventTrigger)
            .managed(true)
            .operatorType(TemplateOperatorType.INSTALLATION)
            .documentTemplates(Set.of(documentTemplate1))
            .lastUpdatedDate(LocalDateTime.now())
            .build();

        TemplateInfoDTO documentTemplateInfoDTO1 = createTemplateInfoDTO(firstDocumentTemplateId, firstDocumentTemplateName);

        NotificationTemplateDTO notificationTemplateDTO = notificationTemplateMapper.toNotificationTemplateDTO(notificationTemplate);

        assertNotNull(notificationTemplateDTO);
        assertEquals(notificationTemplateId, notificationTemplateDTO.getId());
        assertEquals(notificationTemplateName.getName(), notificationTemplateDTO.getName());
        assertEquals(notificationTemplateSubject, notificationTemplateDTO.getSubject());
        assertEquals(notificationTemplateText, notificationTemplateDTO.getText());
        assertEquals(workflow, notificationTemplateDTO.getWorkflow());
        assertEquals(eventTrigger, notificationTemplateDTO.getEventTrigger());
        assertEquals(TemplateOperatorType.INSTALLATION, notificationTemplateDTO.getOperatorType());
        assertThat(notificationTemplateDTO.getDocumentTemplates()).hasSize(1).containsExactly(documentTemplateInfoDTO1);
    }

    private DocumentTemplate createDocumentTemplate(Long id, String name) {
        return DocumentTemplate.builder()
            .id(id)
            .type(DocumentTemplateType.IN_RFI)
            .name(name)
            .competentAuthority(CompetentAuthority.WALES)
            .workflow("workflow")
            .operatorType(TemplateOperatorType.INSTALLATION)
            .lastUpdatedDate(LocalDateTime.now())
            .build();
    }

    private TemplateInfoDTO createTemplateInfoDTO(Long id, String name) {
        TemplateInfoDTO templateInfoDTO = new TemplateInfoDTO();
        templateInfoDTO.setId(id);
        templateInfoDTO.setName(name);

        return templateInfoDTO;
    }
}