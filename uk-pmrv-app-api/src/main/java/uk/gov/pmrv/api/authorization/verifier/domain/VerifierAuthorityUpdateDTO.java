package uk.gov.pmrv.api.authorization.verifier.domain;

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
public class VerifierAuthorityUpdateDTO {

	@NotBlank
	private String userId;
	
	@NotNull
    private AuthorityStatus authorityStatus;

	@RoleCode(roleType = RoleType.VERIFIER)
    private String roleCode;
}
