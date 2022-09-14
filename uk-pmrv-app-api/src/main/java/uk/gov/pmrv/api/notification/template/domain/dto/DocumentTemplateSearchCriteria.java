package uk.gov.pmrv.api.notification.template.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DocumentTemplateSearchCriteria {

    private String term;
    private Long page;
    private Long pageSize;
}
