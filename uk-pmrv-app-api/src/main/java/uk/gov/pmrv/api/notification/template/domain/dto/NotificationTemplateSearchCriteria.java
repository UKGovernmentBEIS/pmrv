package uk.gov.pmrv.api.notification.template.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.pmrv.api.common.domain.enumeration.RoleType;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationTemplateSearchCriteria {

    private String term;
    private RoleType roleType;
    private Long page;
    private Long pageSize;
}
