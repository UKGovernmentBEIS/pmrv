package uk.gov.pmrv.api.authorization.operator.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.pmrv.api.authorization.core.domain.AuthorityStatus;
import uk.gov.pmrv.api.authorization.core.domain.dto.RoleCode;
import uk.gov.pmrv.api.common.domain.enumeration.RoleType;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountOperatorAuthorityUpdateDTO {

    @NotBlank
	private String userId;

    @RoleCode(roleType = RoleType.OPERATOR)
	private String roleCode;

    @NotNull
    private AuthorityStatus authorityStatus;
    
}
