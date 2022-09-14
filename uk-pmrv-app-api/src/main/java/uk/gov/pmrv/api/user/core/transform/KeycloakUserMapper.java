package uk.gov.pmrv.api.user.core.transform;

import org.mapstruct.Mapper;

import uk.gov.pmrv.api.common.transform.MapperConfig;
import uk.gov.pmrv.api.user.core.domain.model.UserInfo;
import uk.gov.pmrv.api.user.core.domain.model.keycloak.KeycloakUserInfo;

@Mapper(componentModel = "spring", config = MapperConfig.class)
public interface KeycloakUserMapper {

    UserInfo toUserInfo(KeycloakUserInfo keycloakUserInfo);
}
