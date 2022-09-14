package uk.gov.pmrv.api.notification.template.domain.dto.templateparams;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WorkflowTemplateParams {

    @NotBlank
    private String requestId;
    
    @NotBlank
    private String requestTypeInfo;

    @NotNull
    private Date requestSubmissionDate;
    
    /**
     * workflow-specific params
     */
    @Builder.Default
    private Map<String, Object> params = new HashMap<>();
}
