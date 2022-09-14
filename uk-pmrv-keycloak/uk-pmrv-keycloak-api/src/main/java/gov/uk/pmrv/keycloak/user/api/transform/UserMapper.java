package gov.uk.pmrv.keycloak.user.api.transform;

import gov.uk.pmrv.keycloak.user.api.model.UserInfo;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.keycloak.models.jpa.entities.UserAttributeEntity;
import org.keycloak.models.jpa.entities.UserEntity;

public class UserMapper {

    public List<UserInfo> mapToUsersInfoWithAttributes(List<UserEntity> userEntities) {
        return map(userEntities, true);
    }

    public List<UserInfo> mapToUsersInfo(List<UserEntity> userEntities) {
        return map(userEntities, false);
    }
    
    private List<UserInfo> map(List<UserEntity> userEntities, boolean mapAttributes) {
        return userEntities.stream().map(u ->
            new UserInfo(u.getId(), u.getFirstName(), u.getLastName(), u.getEmail(), u.isEnabled(), mapAttributes? mapToUserAttributes(u.getAttributes()) : null))
            .collect(Collectors.toList());
    }

    private Map<String, String> mapToUserAttributes(Collection<UserAttributeEntity> userAttributeEntities) {
        return userAttributeEntities.stream()
            .collect(Collectors.toMap(UserAttributeEntity::getName, UserAttributeEntity::getValue));
    }

}
