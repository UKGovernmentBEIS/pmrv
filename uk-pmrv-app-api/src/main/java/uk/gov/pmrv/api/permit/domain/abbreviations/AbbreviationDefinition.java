package uk.gov.pmrv.api.permit.domain.abbreviations;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AbbreviationDefinition {

    @NotBlank
    @Size(max=10000)
    private String abbreviation;

    @NotBlank
    @Size(max=10000)
    private String definition;
}
