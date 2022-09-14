package uk.gov.pmrv.api.verificationbody.domain.dto;

import java.util.HashSet;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.pmrv.api.common.domain.dto.AddressDTO;
import uk.gov.pmrv.api.common.domain.enumeration.EmissionTradingScheme;
import uk.gov.pmrv.api.verificationbody.enumeration.VerificationBodyStatus;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class VerificationBodyDTO {

    private Long id;
    private String name;
    private String accreditationReferenceNumber;
    private VerificationBodyStatus status;
    private AddressDTO address;

    @Builder.Default
    private Set<EmissionTradingScheme> emissionTradingSchemes = new HashSet<>();
}
