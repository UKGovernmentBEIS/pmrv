package uk.gov.pmrv.api.authorization.core.transform;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import uk.gov.pmrv.api.authorization.core.domain.Authority;
import uk.gov.pmrv.api.authorization.core.domain.AuthorityPermission;
import uk.gov.pmrv.api.authorization.core.domain.Permission;
import uk.gov.pmrv.api.authorization.core.domain.dto.AuthorityDTO;
import uk.gov.pmrv.api.authorization.core.domain.dto.AuthorityInfoDTO;
import uk.gov.pmrv.api.common.transform.MapperConfig;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", config = MapperConfig.class)
public interface AuthorityMapper {

	@Mapping(source = "authorityPermissions", target = "authorityPermissions", qualifiedByName = "authPermToPermEnum")
	AuthorityDTO toAuthorityDTO(Authority authority);

	@Named("authPermToPermEnum")
	default List<Permission> permissionToPermission(List<AuthorityPermission> authorityPermissions) {
		return authorityPermissions.stream().map(AuthorityPermission::getPermission).collect(Collectors.toList());
	}

	@Mapping(source = "status", target = "authorityStatus")
	AuthorityInfoDTO toAuthorityInfoDTO(Authority authority);
}
