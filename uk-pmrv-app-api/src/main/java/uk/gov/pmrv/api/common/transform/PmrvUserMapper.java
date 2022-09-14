package uk.gov.pmrv.api.common.transform;

import org.keycloak.representations.AccessToken;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import uk.gov.pmrv.api.authorization.core.domain.dto.AuthorityDTO;
import uk.gov.pmrv.api.common.domain.enumeration.RoleType;
import uk.gov.pmrv.api.common.domain.model.PmrvAuthority;
import uk.gov.pmrv.api.common.domain.model.PmrvUser;

import java.util.List;

/**
 * The AuthenticatedUser Mapper, mapping authority to authenticated user.
 */
@Mapper(componentModel = "spring", config = MapperConfig.class)
public interface PmrvUserMapper {

    @Mapping(target = "userId", source = "token.subject")
    @Mapping(target = "email", source = "token.email")
    @Mapping(target = "firstName", source = "token.givenName")
    @Mapping(target = "lastName", source = "token.familyName")
    @Mapping(target = "authorities", source = "authorities")
    @Mapping(target = "roleType", source = "roleType")
    PmrvUser toPmrvUser(AccessToken token, List<AuthorityDTO> authorities, RoleType roleType);

    @Mapping(target = "permissions", source = "authorityPermissions")
    PmrvAuthority toPmrvAuthority(AuthorityDTO authorityDTO);
}
