package gov.uk.pmrv.keycloak.user.api.repository;

import java.util.List;
import javax.persistence.EntityManager;
import org.keycloak.models.jpa.entities.UserEntity;

public class UserEntityRepository {

    private final EntityManager entityManager;

    public UserEntityRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public List<UserEntity> findUserEntities(List<String> userIds) {

        return entityManager.createQuery(
            "select u " +
                "from UserEntity u " +
                "where u.id in (:userIds)", UserEntity.class)
            .setParameter("userIds", userIds)
            .getResultList();
    }

}
