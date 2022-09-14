package uk.gov.pmrv.api.authorization.rules.services.authorityinfo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.pmrv.api.common.domain.enumeration.CompetentAuthority;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ResourceAuthorityInfo {

    private Long accountId;
    private CompetentAuthority competentAuthority;
    private Long verificationBodyId;
    
}
