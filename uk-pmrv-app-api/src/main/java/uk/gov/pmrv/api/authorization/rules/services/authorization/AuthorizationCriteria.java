package uk.gov.pmrv.api.authorization.rules.services.authorization;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import uk.gov.pmrv.api.authorization.core.domain.Permission;
import uk.gov.pmrv.api.common.domain.enumeration.CompetentAuthority;

@Data
@AllArgsConstructor
@Builder
public class AuthorizationCriteria {
    private Long accountId;
    private CompetentAuthority competentAuthority;
    private Long verificationBodyId;
    private Permission permission;
}
