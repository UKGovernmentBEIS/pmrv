package uk.gov.pmrv.api.notification.template.service;

import lombok.AllArgsConstructor;

import static uk.gov.pmrv.api.common.exception.ErrorCode.RESOURCE_NOT_FOUND;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.pmrv.api.authorization.rules.services.authorityinfo.providers.NotificationTemplateAuthorityInfoProvider;
import uk.gov.pmrv.api.common.domain.enumeration.CompetentAuthority;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.notification.template.domain.NotificationTemplate;
import uk.gov.pmrv.api.notification.template.domain.dto.NotificationTemplateDTO;
import uk.gov.pmrv.api.notification.template.domain.dto.NotificationTemplateSearchCriteria;
import uk.gov.pmrv.api.notification.template.domain.dto.TemplateSearchResults;
import uk.gov.pmrv.api.notification.template.repository.NotificationTemplateRepository;
import uk.gov.pmrv.api.notification.template.transform.NotificationTemplateMapper;

@Service
@AllArgsConstructor
public class NotificationTemplateQueryService implements NotificationTemplateAuthorityInfoProvider {

    private final NotificationTemplateRepository notificationTemplateRepository;
    private final NotificationTemplateMapper notificationTemplateMapper;
    
    NotificationTemplate getNotificationTemplateById(Long id) {
        return notificationTemplateRepository.findById(id)
            .orElseThrow(() -> new BusinessException(RESOURCE_NOT_FOUND));
    }

    public TemplateSearchResults getNotificationTemplatesByCaAndSearchCriteria(CompetentAuthority competentAuthority,
                                                                               NotificationTemplateSearchCriteria searchCriteria) {
        return notificationTemplateRepository.findByCompetentAuthorityAndSearchCriteria(competentAuthority, searchCriteria);
    }

    @Transactional(readOnly = true)
    public NotificationTemplateDTO getManagedNotificationTemplateById(Long id) {
        return notificationTemplateRepository.findManagedNotificationTemplateByIdWithDocumentTemplates(id)
            .map(notificationTemplateMapper::toNotificationTemplateDTO)
            .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND));
    }

    @Override
    public CompetentAuthority getNotificationTemplateCaById(Long id) {
        return getNotificationTemplateById(id).getCompetentAuthority();
    }
}
