package uk.gov.pmrv.api.migration.permit.abbreviations;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EtsAbbreviation {

    private String etsAccountId;
    private String abbreviation;
    private String definition;
}
