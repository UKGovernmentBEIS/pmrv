package uk.gov.pmrv.api.user.operator.domain;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.common.domain.enumeration.RoleType;
import uk.gov.pmrv.api.user.core.domain.dto.UserDTO;
import uk.gov.pmrv.api.authorization.core.domain.dto.RoleCode;

/**
 * Data transfer object used to add an operator user to an account.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
public class OperatorUserInvitationDTO extends UserDTO {

    @RoleCode(roleType = RoleType.OPERATOR)
    private String roleCode;
}
