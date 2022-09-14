package uk.gov.pmrv.api.permit.domain.emissionpoints;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.permit.domain.PermitIdSection;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class EmissionPoint extends PermitIdSection {

    @NotBlank
    @Size(max = 10000)
    private String reference;

    @NotBlank
    @Size(max = 10000)
    private String description;
}
