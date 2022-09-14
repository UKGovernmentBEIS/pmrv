package uk.gov.pmrv.api.authorization.core.transform;

import org.mapstruct.Mapper;
import uk.gov.pmrv.api.authorization.core.domain.Role;
import uk.gov.pmrv.api.authorization.core.domain.dto.RoleDTO;
import uk.gov.pmrv.api.authorization.core.domain.dto.RolePermissionsDTO;
import uk.gov.pmrv.api.common.transform.MapperConfig;

@Mapper(componentModel = "spring", config = MapperConfig.class)
public interface RoleMapper {

    RoleDTO toRoleDTO(Role role);
    RolePermissionsDTO toRolePermissionsDTO(Role role);

}
