package uk.gov.pmrv.api.authorization.rules.services.authorityinfo.providers;

import uk.gov.pmrv.api.common.domain.enumeration.CompetentAuthority;

public interface NotificationTemplateAuthorityInfoProvider {
    CompetentAuthority getNotificationTemplateCaById(Long templateId);

}
