package uk.gov.pmrv.api.notification.template.transform;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import uk.gov.pmrv.api.common.domain.enumeration.CompetentAuthority;
import uk.gov.pmrv.api.common.domain.enumeration.RoleType;
import uk.gov.pmrv.api.files.common.domain.dto.FileInfoDTO;
import uk.gov.pmrv.api.notification.template.domain.DocumentTemplate;
import uk.gov.pmrv.api.notification.template.domain.NotificationTemplate;
import uk.gov.pmrv.api.notification.template.domain.dto.DocumentTemplateDTO;
import uk.gov.pmrv.api.notification.template.domain.dto.TemplateInfoDTO;
import uk.gov.pmrv.api.notification.template.domain.enumeration.DocumentTemplateType;
import uk.gov.pmrv.api.notification.template.domain.enumeration.NotificationTemplateName;
import uk.gov.pmrv.api.notification.template.domain.enumeration.TemplateOperatorType;

@ExtendWith(MockitoExtension.class)
@SpringBootTest(classes = {DocumentTemplateMapperImpl.class, TemplateInfoMapperImpl.class})
class DocumentTemplateMapperTest {

    @Autowired
    private DocumentTemplateMapper documentTemplateMapper;

    @Test
    void toDocumentTemplateDTO() {
        Long documentTemplateId = 1L;
        String documentTemplateName = "document template name";
        String workflow = " workflow";
        Long firstNotificationTemplateId = 11L;
        NotificationTemplateName firstNotificationTemplateName = NotificationTemplateName.EMAIL_CONFIRMATION;
        Long secondNotificationTemplateId = 12L;
        NotificationTemplateName secondNotificationTemplateName = NotificationTemplateName.CHANGE_2FA;
        String fileUuid = UUID.randomUUID().toString();
        String filename = "filename";

        NotificationTemplate notificationTemplate1 = createNotificationTemplate(firstNotificationTemplateId, firstNotificationTemplateName);
        NotificationTemplate notificationTemplate2 = createNotificationTemplate(secondNotificationTemplateId, secondNotificationTemplateName);
        DocumentTemplate documentTemplate = DocumentTemplate.builder()
            .id(documentTemplateId)
            .type(DocumentTemplateType.IN_RFI)
            .name(documentTemplateName)
            .competentAuthority(CompetentAuthority.WALES)
            .workflow(workflow)
            .operatorType(TemplateOperatorType.INSTALLATION)
            .notificationTemplates(Set.of(notificationTemplate1, notificationTemplate2))
            .lastUpdatedDate(LocalDateTime.now())
            .build();

        FileInfoDTO fileDocumentDTO = FileInfoDTO.builder().uuid(fileUuid).name(filename).build();

        TemplateInfoDTO notificationTemplateInfoDTO1 = createTemplateInfoDTO(firstNotificationTemplateId, firstNotificationTemplateName.getName());
        TemplateInfoDTO notificationTemplateInfoDTO2 = createTemplateInfoDTO(secondNotificationTemplateId, secondNotificationTemplateName.getName());

        DocumentTemplateDTO documentTemplateDTO = documentTemplateMapper.toDocumentTemplateDTO(documentTemplate, fileDocumentDTO);

        assertNotNull(documentTemplateDTO);
        assertEquals(documentTemplateId, documentTemplateDTO.getId());
        assertEquals(documentTemplateName, documentTemplateDTO.getName());
        assertEquals(workflow, documentTemplateDTO.getWorkflow());
        assertEquals(TemplateOperatorType.INSTALLATION, documentTemplateDTO.getOperatorType());
        assertEquals(fileUuid, documentTemplateDTO.getFileUuid());
        assertEquals(filename, documentTemplateDTO.getFilename());
        assertThat(documentTemplateDTO.getNotificationTemplates())
            .hasSize(2)
            .containsExactlyInAnyOrder(notificationTemplateInfoDTO1, notificationTemplateInfoDTO2);
    }

    private NotificationTemplate createNotificationTemplate(Long notificationTemplateId, NotificationTemplateName name) {
        return NotificationTemplate.builder()
            .id(notificationTemplateId)
            .name(name)
            .subject("subject")
            .text("text")
            .competentAuthority(CompetentAuthority.WALES)
            .workflow("workflow")
            .roleType(RoleType.OPERATOR)
            .managed(true)
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