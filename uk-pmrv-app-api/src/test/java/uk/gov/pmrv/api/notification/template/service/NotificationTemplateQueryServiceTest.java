package uk.gov.pmrv.api.notification.template.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;

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
import uk.gov.pmrv.api.notification.template.domain.DocumentTemplate;
import uk.gov.pmrv.api.notification.template.domain.NotificationTemplate;
import uk.gov.pmrv.api.notification.template.domain.dto.NotificationTemplateDTO;
import uk.gov.pmrv.api.notification.template.domain.dto.TemplateInfoDTO;
import uk.gov.pmrv.api.notification.template.domain.enumeration.DocumentTemplateType;
import uk.gov.pmrv.api.notification.template.domain.enumeration.NotificationTemplateName;
import uk.gov.pmrv.api.notification.template.domain.enumeration.TemplateOperatorType;
import uk.gov.pmrv.api.notification.template.repository.NotificationTemplateRepository;
import uk.gov.pmrv.api.notification.template.transform.NotificationTemplateMapper;
import uk.gov.pmrv.api.notification.template.transform.TemplateInfoMapper;

@ExtendWith(MockitoExtension.class)
class NotificationTemplateQueryServiceTest {

    @InjectMocks
    private NotificationTemplateQueryService service;

    @Mock
    private NotificationTemplateRepository notificationTemplateRepository;
    
    private static NotificationTemplateMapper notificationTemplateMapper = Mappers.getMapper(NotificationTemplateMapper.class);
    private static TemplateInfoMapper templateInfoMapper = Mappers.getMapper(TemplateInfoMapper.class);

    @BeforeEach
    public void init() {
        ReflectionTestUtils.setField(service, "notificationTemplateMapper", notificationTemplateMapper);
        ReflectionTestUtils.setField(notificationTemplateMapper, "templateInfoMapper", templateInfoMapper);
    }
    
    @Test
    void getNotificationTemplateById() {
        Long notificatioTemplateId = 1L;
        NotificationTemplate expectedNotificationTemplate = NotificationTemplate.builder()
                .id(notificatioTemplateId)
                .operatorType(TemplateOperatorType.INSTALLATION)
                .build();
        
        when(notificationTemplateRepository.findById(notificatioTemplateId)).thenReturn(Optional.of(expectedNotificationTemplate));
        
        NotificationTemplate actualNotificationTemplate = service.getNotificationTemplateById(notificatioTemplateId);
        
        assertThat(actualNotificationTemplate).isEqualTo(expectedNotificationTemplate);
        verify(notificationTemplateRepository, times(1)).findById(notificatioTemplateId);
    }
    
    @Test
    void getNotificationTemplateById_not_found() {
        Long notificatioTemplateId = 1L;
        
        when(notificationTemplateRepository.findById(notificatioTemplateId)).thenReturn(Optional.empty());
        
        BusinessException be = assertThrows(BusinessException.class,
                () -> service.getNotificationTemplateById(notificatioTemplateId));

        assertThat(be.getErrorCode()).isEqualTo(ErrorCode.RESOURCE_NOT_FOUND);
        
        verify(notificationTemplateRepository, times(1)).findById(notificatioTemplateId);
    }
    
    @Test
    void getManagedNotificationTemplateById() {
        Long notificationTemplateId = 1L;
        NotificationTemplateName notificationTemplateName = NotificationTemplateName.INVITATION_TO_OPERATOR_ACCOUNT;
        String notificationTemplateSubject = "notification template subject";
        String notificationTemplateText = "notification template text";
        String workflow = " workflow";
        String eventTrigger = "event trigger the notification";
        Long documentTemplateId = 2L;
        String documentTemplateName = "Operator Account Doc";
        DocumentTemplate documentTemplate = DocumentTemplate.builder()
            .id(documentTemplateId)
            .type(DocumentTemplateType.IN_RFI)
            .name(documentTemplateName)
            .workflow(workflow)
            .operatorType(TemplateOperatorType.INSTALLATION)
            .lastUpdatedDate(LocalDateTime.now())
            .build();
        NotificationTemplate notificationTemplate = NotificationTemplate.builder()
            .id(notificationTemplateId)
            .name(notificationTemplateName)
            .subject(notificationTemplateSubject)
            .text(notificationTemplateText)
            .workflow(workflow)
            .eventTrigger(eventTrigger)
            .operatorType(TemplateOperatorType.INSTALLATION)
            .documentTemplates(Set.of(documentTemplate))
            .build();
        TemplateInfoDTO templateInfoDTO = createTemplateInfoDTO(documentTemplateId, documentTemplateName);
        NotificationTemplateDTO notificationTemplateDTO = NotificationTemplateDTO.builder()
            .id(notificationTemplateId)
            .name(notificationTemplateName.getName())
            .subject(notificationTemplateSubject)
            .text(notificationTemplateText)
            .eventTrigger(eventTrigger)
            .workflow(workflow)
            .operatorType(TemplateOperatorType.INSTALLATION)
            .documentTemplates(Set.of(templateInfoDTO))
            .build();

        when(notificationTemplateRepository.findManagedNotificationTemplateByIdWithDocumentTemplates(notificationTemplateId))
            .thenReturn(Optional.of(notificationTemplate));

        NotificationTemplateDTO result = service.getManagedNotificationTemplateById(notificationTemplateId);
        assertEquals(notificationTemplateDTO, result);

        verify(notificationTemplateRepository, times(1))
            .findManagedNotificationTemplateByIdWithDocumentTemplates(notificationTemplateId);
    }

    @Test
    void getManagedNotificationTemplateById_not_found() {
        Long notificationTemplateId = 1L;

        when(notificationTemplateRepository.findManagedNotificationTemplateByIdWithDocumentTemplates(notificationTemplateId))
            .thenReturn(Optional.empty());

        BusinessException be = assertThrows(BusinessException.class, () ->
            service.getManagedNotificationTemplateById(notificationTemplateId));

        assertThat(be.getErrorCode()).isEqualTo(ErrorCode.RESOURCE_NOT_FOUND);
        verify(notificationTemplateRepository, times(1))
            .findManagedNotificationTemplateByIdWithDocumentTemplates(notificationTemplateId);
    }

    @Test
    void getNotificationTemplateCaById() {
        CompetentAuthority competentAuthority = CompetentAuthority.WALES;
        Long notificatioTemplateId = 1L;
        NotificationTemplate expectedNotificationTemplate = NotificationTemplate.builder()
                .id(notificatioTemplateId)
                .competentAuthority(competentAuthority)
                .build();

        when(notificationTemplateRepository.findById(notificatioTemplateId)).thenReturn(Optional.of(expectedNotificationTemplate));

        assertEquals(competentAuthority, service.getNotificationTemplateCaById(notificatioTemplateId));
        verify(notificationTemplateRepository, times(1)).findById(notificatioTemplateId);
    }

    @Test
    void getNotificationTemplateCaById_not_found() {
        Long notificatioTemplateId = 1L;

        when(notificationTemplateRepository.findById(notificatioTemplateId)).thenReturn(Optional.empty());

        BusinessException be = assertThrows(BusinessException.class, () -> service.getNotificationTemplateCaById(notificatioTemplateId));

        assertThat(be.getErrorCode()).isEqualTo(ErrorCode.RESOURCE_NOT_FOUND);
        verify(notificationTemplateRepository, times(1)).findById(notificatioTemplateId);
    }
    
    private TemplateInfoDTO createTemplateInfoDTO(Long id, String name) {
        TemplateInfoDTO templateInfoDTO = new TemplateInfoDTO();
        templateInfoDTO.setId(id);
        templateInfoDTO.setName(name);

        return templateInfoDTO;
    }
}