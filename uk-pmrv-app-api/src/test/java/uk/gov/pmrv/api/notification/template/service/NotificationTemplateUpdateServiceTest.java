package uk.gov.pmrv.api.notification.template.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.common.domain.enumeration.CompetentAuthority;
import uk.gov.pmrv.api.common.domain.enumeration.RoleType;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.notification.template.domain.NotificationTemplate;
import uk.gov.pmrv.api.notification.template.domain.dto.NotificationTemplateUpdateDTO;
import uk.gov.pmrv.api.notification.template.domain.enumeration.NotificationTemplateName;
import uk.gov.pmrv.api.notification.template.domain.enumeration.TemplateOperatorType;
import uk.gov.pmrv.api.notification.template.repository.NotificationTemplateRepository;

@ExtendWith(MockitoExtension.class)
class NotificationTemplateUpdateServiceTest {

    @InjectMocks
    private NotificationTemplateUpdateService notificationTemplateUpdateService;

    @Mock
    private NotificationTemplateRepository notificationTemplateRepository;

    @Test
    void updateNotificationTemplate() {
        Long notificationTemplateId = 1L;
        String updatedNotificationTemplateSubject = "updated subject";
        String updatedNotificationTemplateText = "updated text";
        NotificationTemplate notificationTemplate = NotificationTemplate.builder()
            .id(notificationTemplateId)
            .name(NotificationTemplateName.EMAIL_CONFIRMATION)
            .subject("subject")
            .text("text")
            .competentAuthority(CompetentAuthority.WALES)
            .roleType(RoleType.OPERATOR)
            .managed(true)
            .operatorType(TemplateOperatorType.INSTALLATION)
            .lastUpdatedDate(LocalDateTime.now())
            .build();

        NotificationTemplateUpdateDTO notificationTemplateUpdateDTO = NotificationTemplateUpdateDTO.builder()
            .subject(updatedNotificationTemplateSubject)
            .text(updatedNotificationTemplateText)
            .build();

        when(notificationTemplateRepository.findManagedNotificationTemplateById(notificationTemplateId))
            .thenReturn(Optional.of(notificationTemplate));

        notificationTemplateUpdateService.updateNotificationTemplate(notificationTemplateId, notificationTemplateUpdateDTO);

        ArgumentCaptor<NotificationTemplate> notificationTemplateCaptor = ArgumentCaptor.forClass(NotificationTemplate.class);
        verify(notificationTemplateRepository, times(1)).save(notificationTemplateCaptor.capture());

        NotificationTemplate updatedNotificationTemplate = notificationTemplateCaptor.getValue();
        assertEquals(updatedNotificationTemplateSubject, updatedNotificationTemplate.getSubject());
        assertEquals(updatedNotificationTemplateText, updatedNotificationTemplate.getText());
        assertEquals(notificationTemplate.getName(), updatedNotificationTemplate.getName());
        assertEquals(notificationTemplate.getCompetentAuthority(), updatedNotificationTemplate.getCompetentAuthority());
        assertEquals(notificationTemplate.getOperatorType(), updatedNotificationTemplate.getOperatorType());
        assertEquals(notificationTemplate.getRoleType(), updatedNotificationTemplate.getRoleType());
    }

    @Test
    void updateNotificationTemplate_not_found() {
        Long notificationTemplateId = 1L;
        String updatedNotificationTemplateSubject = "updated subject";
        String updatedNotificationTemplateText = "updated text";
        NotificationTemplateUpdateDTO notificationTemplateUpdateDTO = NotificationTemplateUpdateDTO.builder()
            .subject(updatedNotificationTemplateSubject)
            .text(updatedNotificationTemplateText)
            .build();

        when(notificationTemplateRepository.findManagedNotificationTemplateById(notificationTemplateId))
            .thenReturn(Optional.empty());

        BusinessException be = assertThrows(BusinessException.class, () ->
            notificationTemplateUpdateService.updateNotificationTemplate(notificationTemplateId, notificationTemplateUpdateDTO));

        assertThat(be.getErrorCode()).isEqualTo(ErrorCode.RESOURCE_NOT_FOUND);

        verify(notificationTemplateRepository, times(1))
            .findManagedNotificationTemplateById(notificationTemplateId);
        verifyNoMoreInteractions(notificationTemplateRepository);
    }
}