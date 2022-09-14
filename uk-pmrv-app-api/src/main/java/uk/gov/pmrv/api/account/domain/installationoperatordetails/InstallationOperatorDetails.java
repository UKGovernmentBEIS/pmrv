package uk.gov.pmrv.api.account.domain.installationoperatordetails;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import uk.gov.pmrv.api.account.domain.dto.LocationDTO;
import uk.gov.pmrv.api.account.domain.enumeration.LegalEntityType;
import uk.gov.pmrv.api.common.domain.dto.AddressDTO;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InstallationOperatorDetails {

    private String installationName;
    private String siteName;
    private LocationDTO installationLocation;
    private String operator;
    private LegalEntityType operatorType;
    private String companyReferenceNumber;
    private AddressDTO operatorDetailsAddress;
}
