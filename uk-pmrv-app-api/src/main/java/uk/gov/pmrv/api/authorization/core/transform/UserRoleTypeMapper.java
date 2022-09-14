package uk.gov.pmrv.api.authorization.core.transform;

import org.mapstruct.Mapper;
import uk.gov.pmrv.api.authorization.core.domain.UserRoleType;
import uk.gov.pmrv.api.authorization.core.domain.dto.UserRoleTypeDTO;
import uk.gov.pmrv.api.common.transform.MapperConfig;

/**
 * Mapper for {@link UserRoleType} objects.
 */
@Mapper(componentModel = "spring", config = MapperConfig.class)
public interface UserRoleTypeMapper {

    UserRoleTypeDTO toUserRoleTypeDTO(UserRoleType userRoleType);
}
