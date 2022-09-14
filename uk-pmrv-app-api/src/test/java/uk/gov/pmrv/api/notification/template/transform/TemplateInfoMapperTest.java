package uk.gov.pmrv.api.notification.template.transform;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import uk.gov.pmrv.api.notification.template.domain.DocumentTemplate;
import uk.gov.pmrv.api.notification.template.domain.NotificationTemplate;
import uk.gov.pmrv.api.notification.template.domain.dto.TemplateInfoDTO;
import uk.gov.pmrv.api.notification.template.domain.enumeration.DocumentTemplateType;
import uk.gov.pmrv.api.notification.template.domain.enumeration.NotificationTemplateName;
import uk.gov.pmrv.api.notification.template.domain.enumeration.TemplateOperatorType;

class TemplateInfoMapperTest {

    private TemplateInfoMapper mapper;

    @BeforeEach
    void init() {
        mapper = Mappers.getMapper(TemplateInfoMapper.class);
    }

    @Test
    void documentTemplateToTemplateInfoDTO() {
        Long templateId = 1L;
        String templateName = "templateName";
        DocumentTemplate documentTemplate = DocumentTemplate.builder()
            .id(templateId)
            .type(DocumentTemplateType.IN_RFI)
            .name(templateName)
            .workflow("permitWorkflow")
            .operatorType(TemplateOperatorType.INSTALLATION)
            .lastUpdatedDate(LocalDateTime.now())
            .build();

        TemplateInfoDTO templateInfoDTO = mapper.documentTemplateToTemplateInfoDTO(documentTemplate);

        assertNotNull(templateInfoDTO);
        assertEquals(templateId, templateInfoDTO.getId());
        assertEquals(templateName, templateInfoDTO.getName());
        assertNull(templateInfoDTO.getOperatorType());
        assertNull(templateInfoDTO.getWorkflow());
        assertNull(templateInfoDTO.getLastUpdatedDate());
    }

    @Test
    void notificationTemplateToTemplateInfoDTO() {
        Long templateId = 1L;
        NotificationTemplateName templateName = NotificationTemplateName.EMAIL_CONFIRMATION;
        NotificationTemplate notificationTemplate = NotificationTemplate.builder()
            .id(templateId)
            .name(templateName)
            .subject("subject")
            .text("text")
            .eventTrigger("evnt triggered the notification")
            .workflow("permitWorkflow")
            .operatorType(TemplateOperatorType.INSTALLATION)
            .lastUpdatedDate(LocalDateTime.now())
            .build();

        TemplateInfoDTO templateInfoDTO = mapper.notificationTemplateToTemplateInfoDTO(notificationTemplate);

        assertNotNull(templateInfoDTO);
        assertEquals(templateId, templateInfoDTO.getId());
        assertEquals(templateName.getName(), templateInfoDTO.getName());
        assertNull(templateInfoDTO.getOperatorType());
        assertNull(templateInfoDTO.getWorkflow());
        assertNull(templateInfoDTO.getLastUpdatedDate());
    }
}