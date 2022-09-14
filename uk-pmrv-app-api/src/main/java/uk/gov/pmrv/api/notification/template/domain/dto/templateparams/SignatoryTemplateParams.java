package uk.gov.pmrv.api.notification.template.domain.dto.templateparams;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SignatoryTemplateParams {

    @NotBlank
    private String fullName;
    
    private String jobTitle;
    
    @NotEmpty
    private byte[] signature;
}
