package uk.gov.pmrv.api.notification.template.domain.dto;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.pmrv.api.notification.template.domain.enumeration.TemplateOperatorType;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DocumentTemplateDTO {

    private Long id;
    private String name;
    private TemplateOperatorType operatorType;
    private String workflow;
    private LocalDateTime lastUpdatedDate;

    private String fileUuid;
    private String filename;
    
    @Builder.Default
    private Set<TemplateInfoDTO> notificationTemplates = new HashSet<>();
}
