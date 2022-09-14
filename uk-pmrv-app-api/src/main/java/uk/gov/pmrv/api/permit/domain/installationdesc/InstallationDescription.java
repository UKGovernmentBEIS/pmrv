package uk.gov.pmrv.api.permit.domain.installationdesc;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.pmrv.api.permit.domain.PermitSection;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InstallationDescription implements PermitSection {

    @NotBlank
    @Size(max=10000)
    private String mainActivitiesDesc;

    @NotBlank
    @Size(max=10000)
    private String siteDescription;
}
