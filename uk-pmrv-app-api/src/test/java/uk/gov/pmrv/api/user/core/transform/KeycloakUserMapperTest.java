package uk.gov.pmrv.api.user.core.transform;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import uk.gov.pmrv.api.user.core.domain.model.UserInfo;
import uk.gov.pmrv.api.user.core.domain.model.keycloak.KeycloakUserInfo;

class KeycloakUserMapperTest {

    private KeycloakUserMapper mapper = Mappers.getMapper(KeycloakUserMapper.class);

    @Test
    void toUserInfo() {
        KeycloakUserInfo keycloakUserInfo = KeycloakUserInfo.builder()
                .firstName("fn")
                .lastName("ln")
                .email("email@email")
                .id("id")
                .enabled(true)
                .build();
        
        UserInfo result = mapper.toUserInfo(keycloakUserInfo);
        
        assertThat(result.getId()).isEqualTo(keycloakUserInfo.getId());
        assertThat(result.getFirstName()).isEqualTo(keycloakUserInfo.getFirstName());
        assertThat(result.getLastName()).isEqualTo(keycloakUserInfo.getLastName());
        assertThat(result.getEmail()).isEqualTo(keycloakUserInfo.getEmail());
        assertThat(result.isEnabled()).isEqualTo(keycloakUserInfo.isEnabled());
    }
}
