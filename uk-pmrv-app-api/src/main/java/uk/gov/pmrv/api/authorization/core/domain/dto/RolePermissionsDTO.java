package uk.gov.pmrv.api.authorization.core.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.pmrv.api.authorization.core.domain.RolePermission;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RolePermissionsDTO {

    private String name;

    private String code;

    private List<RolePermission> rolePermissions;

}
