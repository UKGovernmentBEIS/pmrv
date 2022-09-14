package uk.gov.pmrv.api.authorization.regulator.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.pmrv.api.common.domain.enumeration.CompetentAuthority;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegulatorUserAssignedSubResource {
    private CompetentAuthority ca;
    private String resourceSubType;
}
