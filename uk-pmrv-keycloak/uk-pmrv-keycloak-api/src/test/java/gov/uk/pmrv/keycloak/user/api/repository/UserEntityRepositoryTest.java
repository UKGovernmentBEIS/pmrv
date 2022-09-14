package gov.uk.pmrv.keycloak.user.api.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.google.common.collect.Iterables;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.keycloak.models.jpa.entities.UserAttributeEntity;
import org.keycloak.models.jpa.entities.UserEntity;

public class UserEntityRepositoryTest {

    private static EntityManagerFactory entityManagerFactory;
    private static EntityManager entityManager;
    private UserEntityRepository userEntityRepository;

    @BeforeAll
    public static void init() {
        entityManagerFactory = Persistence.createEntityManagerFactory("uk-pmrv");
    }

    @BeforeEach
    public void setup() {
        entityManager = entityManagerFactory.createEntityManager();
        userEntityRepository = new UserEntityRepository(entityManager);
    }

    @AfterEach
    public void close() {
        entityManager.clear();
        entityManager.close();
    }

    @Test
    public void findUsers_two_users_one_attribute_each() {
        final String firstName1 = "firstName1";
        final String lastName1 = "lastName1";
        final String jobTitle = "jobTitle";
        final String termsVersion = "1";
        final String firstName2 = "firstName2";
        final String lastName2 = "lastName2";

        entityManager.getTransaction().begin();

        final String userId1 = createUser(firstName1, lastName1, Map.of("jobTitle", jobTitle));
        final String userId2 = createUser(firstName2, lastName2, Map.of("termsVersion", termsVersion));

        entityManager.flush();
        entityManager.clear();

        List<UserEntity> users = userEntityRepository.findUserEntities(List.of(userId1, userId2));

        entityManager.getTransaction().commit();

        assertEquals(2, users.size());

        UserEntity user1 = users.stream().filter(u -> u.getId().equals(userId1)).findFirst().get();
        assertEquals(userId1, user1.getId());
        assertEquals(firstName1, user1.getFirstName());
        assertEquals(lastName1, user1.getLastName());
        assertEquals(1, user1.getAttributes().size());
        assertEquals("jobTitle", Iterables.get(user1.getAttributes(), 0).getName());
        assertEquals(jobTitle, Iterables.get(user1.getAttributes(), 0).getValue());

        UserEntity user2 = users.stream().filter(u -> u.getId().equals(userId2)).findFirst().get();
        assertEquals(userId2, user2.getId());
        assertEquals(firstName2, user2.getFirstName());
        assertEquals(lastName2, user2.getLastName());
        assertEquals("termsVersion", Iterables.get(user2.getAttributes(), 0).getName());
        assertEquals(termsVersion, Iterables.get(user2.getAttributes(), 0).getValue());

    }

    private String createUser(String firstName, String lastName, Map<String, String> attributeMap) {

        UserEntity userEntity = new UserEntity();
        userEntity.setId(UUID.randomUUID().toString());
        userEntity.setFirstName(firstName);
        userEntity.setLastName(lastName);
        Collection<UserAttributeEntity> attributes = userEntity.getAttributes();

        attributeMap.entrySet().forEach(
            (a -> {
                UserAttributeEntity ua = createUserAttributeEntity(userEntity, a.getKey(), a.getValue());
                attributes.add(ua);
                entityManager.persist(ua);
            }));

        entityManager.persist(userEntity);

        return userEntity.getId();
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
