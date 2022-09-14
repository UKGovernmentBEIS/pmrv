package uk.gov.pmrv.api.migration.verification_body;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.pmrv.api.common.domain.enumeration.EmissionTradingScheme;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VerificationBodyVO {
    private String id;
    private String name;
    private EmissionTradingScheme accreditationType;
    private String referenceNumber;
    private String country;
    private String line1;
    private String line2;
    private String city;
    private String postcode;
    private boolean isDisabled;
}
