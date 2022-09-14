package uk.gov.pmrv.api.notification.template.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.notification.template.domain.NotificationTemplate;
import uk.gov.pmrv.api.notification.template.domain.dto.NotificationTemplateUpdateDTO;
import uk.gov.pmrv.api.notification.template.repository.NotificationTemplateRepository;

@Service
@RequiredArgsConstructor
public class NotificationTemplateUpdateService {

    private final NotificationTemplateRepository notificationTemplateRepository;

    @Transactional
    public void updateNotificationTemplate(Long notificationTemplateId, NotificationTemplateUpdateDTO notificationTemplateUpdateDTO) {
        NotificationTemplate notificationTemplate =
            notificationTemplateRepository.findManagedNotificationTemplateById(notificationTemplateId)
            .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND));

        notificationTemplate.setSubject(notificationTemplateUpdateDTO.getSubject());
        notificationTemplate.setText(notificationTemplateUpdateDTO.getText());

        notificationTemplateRepository.save(notificationTemplate);
    }
}
