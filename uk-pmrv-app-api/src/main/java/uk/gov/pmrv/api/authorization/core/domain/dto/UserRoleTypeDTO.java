package uk.gov.pmrv.api.authorization.core.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uk.gov.pmrv.api.common.domain.enumeration.RoleType;

/**
 * The User Role Type DTO.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Builder
public class UserRoleTypeDTO {

    /**
     * The user id (value from keycloak).
     */
    private String userId;

    /**
     * The user role type {@link RoleType}.
     */
    private RoleType roleType;
}
