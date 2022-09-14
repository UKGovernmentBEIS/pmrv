package uk.gov.pmrv.api.notification.template.domain.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TemplateSearchResults {

    private List<TemplateInfoDTO> templates;
    private Long total;
}
