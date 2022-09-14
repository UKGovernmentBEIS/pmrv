package uk.gov.pmrv.api.notification.template.domain.dto.templateparams;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.pmrv.api.common.domain.enumeration.CompetentAuthority;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompetentAuthorityTemplateParams {

    @NotNull
    private CompetentAuthority competentAuthority;
    
    @NotEmpty
    private byte[] logo;
    
    public String getName() {
        return competentAuthority.getName();
    }
    
    public String getEmail() {
        return competentAuthority.getEmail();
    }
}
