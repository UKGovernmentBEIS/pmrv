package gov.uk.pmrv.keycloak.user.api.service;

import gov.uk.pmrv.keycloak.user.api.model.UserInfo;
import gov.uk.pmrv.keycloak.user.api.repository.UserEntityRepository;
import gov.uk.pmrv.keycloak.user.api.transform.UserMapper;
import java.util.List;
import org.keycloak.models.jpa.entities.UserEntity;

public class UserEntityService {

    private final UserEntityRepository userEntityRepository;

    private final UserSessionService userSessionService;

    private final UserMapper userMapper;

    public UserEntityService(UserEntityRepository userEntityRepository,
                             UserSessionService userSessionService,
                             UserMapper userMapper) {
        this.userEntityRepository = userEntityRepository;
        this.userSessionService = userSessionService;
        this.userMapper = userMapper;
    }

    public List<UserInfo> getUsersInfo(List<String> userIds, boolean includeAttributes) {
        userSessionService.getAuthenticatedUser();

        List<UserEntity> userEntities = userEntityRepository.findUserEntities(userIds);

        return includeAttributes ? userMapper.mapToUsersInfoWithAttributes(userEntities)
                : userMapper.mapToUsersInfo(userEntities);
    }

}
