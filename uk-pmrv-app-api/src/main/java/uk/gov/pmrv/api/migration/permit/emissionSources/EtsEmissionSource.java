package uk.gov.pmrv.api.migration.permit.emissionSources;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EtsEmissionSource {

    private String etsAccountId;
    private String reference;
    private String description;
}
