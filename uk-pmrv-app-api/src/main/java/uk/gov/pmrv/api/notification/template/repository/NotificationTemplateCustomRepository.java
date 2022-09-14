package uk.gov.pmrv.api.notification.template.repository;

import org.springframework.transaction.annotation.Transactional;
import uk.gov.pmrv.api.common.domain.enumeration.CompetentAuthority;
import uk.gov.pmrv.api.notification.template.domain.dto.NotificationTemplateSearchCriteria;
import uk.gov.pmrv.api.notification.template.domain.dto.TemplateSearchResults;

public interface NotificationTemplateCustomRepository {

    @Transactional(readOnly = true)
    TemplateSearchResults findByCompetentAuthorityAndSearchCriteria(CompetentAuthority competentAuthority,
                                                                    NotificationTemplateSearchCriteria searchCriteria);
}
