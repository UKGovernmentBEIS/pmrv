package uk.gov.pmrv.api.migration.legalentity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LegalEntityVO {

    private String operatorId;
    private String emitterId;
    private String competentAuthority;
    private String type;
    private String name;
    private String referenceNumber;
    private String line1;
    private String line2;
    private String city;
    private String postcode;
    private String country;
    private String emitterDisplayId;
    private String emitterName;
}
