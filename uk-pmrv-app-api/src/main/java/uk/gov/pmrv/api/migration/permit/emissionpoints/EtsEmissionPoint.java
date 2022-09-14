package uk.gov.pmrv.api.migration.permit.emissionpoints;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EtsEmissionPoint {

    private String emitterId;

    private String reference;

    private String description;
}
