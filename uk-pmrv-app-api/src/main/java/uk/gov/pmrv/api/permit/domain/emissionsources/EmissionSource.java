package uk.gov.pmrv.api.permit.domain.emissionsources;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.permit.domain.PermitIdSection;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class EmissionSource extends PermitIdSection {

    @NotBlank
    @Size(max=10000)
    private String reference;

    @NotBlank
    @Size(max=10000)
    private String description;
}
