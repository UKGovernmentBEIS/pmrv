package uk.gov.pmrv.api.notification.message.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.common.domain.enumeration.CompetentAuthority;
import uk.gov.pmrv.api.common.exception.BusinessCheckedException;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.notification.message.domain.SystemMessageNotificationInfo;
import uk.gov.pmrv.api.notification.message.domain.enumeration.SystemMessageNotificationType;
import uk.gov.pmrv.api.notification.template.domain.NotificationContent;
import uk.gov.pmrv.api.notification.template.domain.enumeration.NotificationTemplateName;
import uk.gov.pmrv.api.notification.template.service.NotificationTemplateProcessService;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static uk.gov.pmrv.api.notification.message.domain.enumeration.SystemMessageNotificationType.ACCOUNT_USERS_SETUP;

@ExtendWith(MockitoExtension.class)
class SystemMessageNotificationServiceTest {

    @InjectMocks
    private SystemMessageNotificationService systemMessageNotificationService;

    @Mock
    private SystemMessageNotificationSendService systemMessageNotificationSendService;

    @Mock
    private NotificationTemplateProcessService notificationTemplateProcessService;

    @Test
    void generateAndSendSystemMessage() {
        final SystemMessageNotificationType systemMessageNotificationType = ACCOUNT_USERS_SETUP;
        final NotificationTemplateName templateName = systemMessageNotificationType.getNotificationTemplateName();
        final String messageSubject = "message_subject";
        final String messageText = "message_text";
        final String receiver = "receiver";
        final Long accountId = 1L;
        final Long vbId = 1L;
        SystemMessageNotificationInfo msgInfo = SystemMessageNotificationInfo.builder()
            .messageType(systemMessageNotificationType)
            .accountId(accountId)
            .competentAuthority(CompetentAuthority.ENGLAND)
            .verificationBodyId(vbId)
            .receiver(receiver)
            .messageParameters(Map.of())
            .build();

        NotificationContent notificationContent = NotificationContent.builder()
            .text(messageText)
            .subject(messageSubject)
            .build();

        //mock
        when(notificationTemplateProcessService.processMessageNotificationTemplate(templateName, msgInfo.getMessageParameters()))
            .thenReturn(notificationContent);

        //invoke
        systemMessageNotificationService.generateAndSendNotificationSystemMessage(msgInfo);

        //verify
        verify(notificationTemplateProcessService, times(1))
            .processMessageNotificationTemplate(templateName, msgInfo.getMessageParameters());
        verify(systemMessageNotificationSendService, times(1))
            .sendNotificationSystemMessage(msgInfo, notificationContent);
    }

    @Test
    void generateAndSendSystemMessage_exception_when_template_processing_fails() {
        final SystemMessageNotificationType systemMessageNotificationType = ACCOUNT_USERS_SETUP;
        final NotificationTemplateName templateName = systemMessageNotificationType.getNotificationTemplateName();
        final String receiver = "receiver";
        final Long accountId = 1L;
        final Long vbId = 1L;
        SystemMessageNotificationInfo msgInfo = SystemMessageNotificationInfo.builder()
            .messageType(systemMessageNotificationType)
            .accountId(accountId)
            .competentAuthority(CompetentAuthority.ENGLAND)
            .verificationBodyId(vbId)
            .receiver(receiver)
            .messageParameters(Map.of())
            .build();

        //mock
        when(notificationTemplateProcessService.processMessageNotificationTemplate(templateName, msgInfo.getMessageParameters()))
            .thenThrow(BusinessException.class);

        //invoke
        assertThrows(BusinessException.class, () -> systemMessageNotificationService.generateAndSendNotificationSystemMessage(msgInfo));

        //verify
        verify(notificationTemplateProcessService, times(1))
            .processMessageNotificationTemplate(templateName, msgInfo.getMessageParameters());
        verifyNoInteractions(systemMessageNotificationSendService);
    }
}