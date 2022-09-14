package gov.uk.pmrv.keycloak.user.api.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import gov.uk.pmrv.keycloak.user.api.model.UserInfo;
import gov.uk.pmrv.keycloak.user.api.repository.UserEntityRepository;
import gov.uk.pmrv.keycloak.user.api.transform.UserMapper;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.keycloak.models.jpa.entities.UserAttributeEntity;
import org.keycloak.models.jpa.entities.UserEntity;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class UserEntityServiceTest {

    @InjectMocks
    private UserEntityService userEntityService;

    @Mock
    private UserEntityRepository userEntityRepository;

    @Mock
    private UserSessionService userSessionService;

    @Mock
    private UserMapper userMapper;

    @Test
    public void getUsers_includeAttributes_true() {
        String userId1 = UUID.randomUUID().toString();
        String userId2 = UUID.randomUUID().toString();
        final String firstName1 = "firstName1";
        final String lastName1 = "lastName1";
        final String email1 = "email1@email";
        final String jobTitle = "jobTitle";
        final String termsVersion = "termsVersion";
        final String firstName2 = "firstName2";
        final String lastName2 = "lastName2";
        final String email2 = "email2@email";
        
        List<String> userIds = List.of(userId1, userId2);
        List<UserEntity> userEntities =
            List.of(createUserEntity(userId1, firstName1, lastName1, Map.of("jobTitle", jobTitle)),
                createUserEntity(userId2, firstName2, lastName2, Map.of("termsVersion", termsVersion)));
        UserInfo expectedUserInfo1 = new UserInfo(
            userId1,
            firstName1,
            lastName1,
            email1,
            true,
            Map.of("jobTitle", jobTitle));
        UserInfo expectedUserInfo2 = new UserInfo(
            userId2,
            firstName2,
            lastName2,
            email2,
            true,
            Map.of("termsVersion", termsVersion));
        List<UserInfo> expectedUserInfoList = List.of(expectedUserInfo1, expectedUserInfo2);

        when(userEntityRepository.findUserEntities(userIds)).thenReturn(userEntities);
        when(userMapper.mapToUsersInfoWithAttributes(userEntities)).thenReturn(expectedUserInfoList);

        List<UserInfo> actualUsersInfo =
            userEntityService.getUsersInfo(List.of(userId1, userId2), true);

        assertEquals(expectedUserInfo1,
            actualUsersInfo.stream().filter(u -> u.getId().equals(userId1)).findFirst().get());
        assertEquals(expectedUserInfo2,
            actualUsersInfo.stream().filter(u -> u.getId().equals(userId2)).findFirst().get());

    }

    @Test
    public void getUsers_includeAttributes_false() {
        String userId1 = UUID.randomUUID().toString();
        String userId2 = UUID.randomUUID().toString();
        final String firstName1 = "firstName1";
        final String lastName1 = "lastName1";
        final String email1 = "email1@email";
        final String jobTitle = "jobTitle";
        final String termsVersion = "termsVersion";
        final String firstName2 = "firstName2";
        final String lastName2 = "lastName2";
        final String email2 = "email2@email";

        List<String> userIds = List.of(userId1, userId2);
        List<UserEntity> userEntities =
            List.of(createUserEntity(userId1, firstName1, lastName1, Map.of("jobTitle", jobTitle)),
                createUserEntity(userId2, firstName2, lastName2, Map.of("termsVersion", termsVersion)));
        UserInfo expectedUserInfo1 = new UserInfo(
            userId1,
            firstName1,
            lastName1,
            email1,
            true,
            null);
        UserInfo expectedUserInfo2 = new UserInfo(
            userId2,
            firstName2,
            lastName2,
            email2,
            true,
            null);
        List<UserInfo> expectedUserInfoList = List.of(expectedUserInfo1, expectedUserInfo2);

        when(userEntityRepository.findUserEntities(userIds)).thenReturn(userEntities);
        when(userMapper.mapToUsersInfo(userEntities)).thenReturn(expectedUserInfoList);

        List<UserInfo> actualUsersInfo =
            userEntityService.getUsersInfo(List.of(userId1, userId2), false);

        assertEquals(expectedUserInfo1,
            actualUsersInfo.stream().filter(u -> u.getId().equals(userId1)).findFirst().get());
        assertEquals(expectedUserInfo2,
            actualUsersInfo.stream().filter(u -> u.getId().equals(userId2)).findFirst().get());

    }

    private UserEntity createUserEntity(String userId, String firstName, String lastName,
                                        Map<String, String> attributeMap) {

        UserEntity userEntity = new UserEntity();
        userEntity.setId(userId);
        userEntity.setFirstName(firstName);
        userEntity.setLastName(lastName);
        Collection<UserAttributeEntity> attributes = userEntity.getAttributes();

        attributeMap.entrySet().forEach(
            (a -> {
                UserAttributeEntity ua = createUserAttributeEntity(userEntity, a.getKey(), a.getValue());
                attributes.add(ua);
                ;
            }));

        return userEntity;
    }

    private UserAttributeEntity createUserAttributeEntity(UserEntity userEntity, String attributeName,
                                                          String attributeValue) {

        UserAttributeEntity userAttributeEntity = new UserAttributeEntity();
        userAttributeEntity.setId(UUID.randomUUID().toString());
        userAttributeEntity.setUser(userEntity);
        userAttributeEntity.setName(attributeName);
        userAttributeEntity.setValue(attributeValue);

        return userAttributeEntity;
    }

}
