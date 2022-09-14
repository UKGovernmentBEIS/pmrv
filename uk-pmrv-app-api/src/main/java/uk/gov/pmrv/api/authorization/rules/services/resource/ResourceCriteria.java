package uk.gov.pmrv.api.authorization.rules.services.resource;

import lombok.Builder;
import lombok.Data;
import uk.gov.pmrv.api.common.domain.enumeration.CompetentAuthority;

@Data
@Builder
public class ResourceCriteria {
    
    private Long accountId;
    private CompetentAuthority competentAuthority;
    private Long verificationBodyId;
    
}
