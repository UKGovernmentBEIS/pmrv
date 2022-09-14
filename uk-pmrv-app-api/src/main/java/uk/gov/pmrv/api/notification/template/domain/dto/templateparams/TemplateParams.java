package uk.gov.pmrv.api.notification.template.domain.dto.templateparams;

import javax.validation.Valid;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TemplateParams {
    
    @Valid
    private EmailTemplateParams emailParams;
    
    @Valid
    private CompetentAuthorityTemplateParams competentAuthorityParams;
    
    private String competentAuthorityCentralInfo;
    
    @Valid
    private SignatoryTemplateParams signatoryParams;
    
    @Valid
    private AccountTemplateParams accountParams;
    
    @Valid
    private WorkflowTemplateParams workflowParams;
    
    private String permitId;
    
}
