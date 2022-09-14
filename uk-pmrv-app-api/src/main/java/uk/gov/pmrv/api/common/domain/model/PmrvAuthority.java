package uk.gov.pmrv.api.common.domain.model;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.pmrv.api.authorization.core.domain.Permission;
import uk.gov.pmrv.api.common.domain.enumeration.CompetentAuthority;

/**
 * The authenticated User's applicable accounts.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PmrvAuthority {

    private String code;

    private Long accountId;

    private CompetentAuthority competentAuthority;

    private Long verificationBodyId;

    private List<Permission> permissions;
}
