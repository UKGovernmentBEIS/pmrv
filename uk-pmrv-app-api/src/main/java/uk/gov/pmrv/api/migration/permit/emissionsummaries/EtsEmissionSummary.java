package uk.gov.pmrv.api.migration.permit.emissionsummaries;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EtsEmissionSummary {

    private String etsAccountId;
    private String emitterDisplayId;
    private String sourceStream;
    private Set<String> emissionSources;
    private Set<String> emissionPoints;
    private String regulatedActivity;
    private boolean excludedRegulatedActivity;

}
