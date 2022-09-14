package uk.gov.pmrv.api.notification.template.domain.dto;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import uk.gov.pmrv.api.notification.template.domain.enumeration.TemplateOperatorType;

import java.time.LocalDateTime;

@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
public class TemplateInfoDTO {

    private Long id;
    private String name;
    private TemplateOperatorType operatorType;
    private String workflow;
    private LocalDateTime lastUpdatedDate;

    public TemplateInfoDTO(Long id, String name, String operatorType, String workflow, LocalDateTime lastUpdatedDate) {
        this.id = id;
        this.name = name;
        this.operatorType = !StringUtils.isEmpty(operatorType) ? TemplateOperatorType.valueOf(operatorType) : null;
        this.workflow = workflow;
        this.lastUpdatedDate = lastUpdatedDate;
    }
}
