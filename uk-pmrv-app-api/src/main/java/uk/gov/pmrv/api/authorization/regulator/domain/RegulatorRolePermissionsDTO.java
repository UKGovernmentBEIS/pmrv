package uk.gov.pmrv.api.authorization.regulator.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegulatorRolePermissionsDTO {

    private String name;

    private String code;

    private Map<RegulatorPermissionGroup, RegulatorPermissionLevel> rolePermissions;

}
