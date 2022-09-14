package uk.gov.pmrv.api.migration.permit.envpermitsandlicences;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EnvPermitOrLicence {
    
    private String etsAccountId;
    
    private String permitNumber;
    private String permitType;
    private String permitHolder;
    private String issuingAuthority;
}
