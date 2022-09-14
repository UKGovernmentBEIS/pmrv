package uk.gov.pmrv.api.authorization.rules.repository;

import org.springframework.stereotype.Repository;
import uk.gov.pmrv.api.authorization.rules.domain.AuthorizationRule;
import uk.gov.pmrv.api.authorization.rules.domain.ResourceType;
import uk.gov.pmrv.api.common.domain.enumeration.RoleType;

import javax.persistence.EntityManager;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Tuple;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Repository
public class AuthorizationRuleCustomRepositoryImpl implements AuthorizationRuleCustomRepository {

    @PersistenceContext
    private EntityManager em;
    
    @Override
    public Map<String, Set<RoleType>> findResourceSubTypesRoleTypes() {
        return em.createNamedQuery(AuthorizationRule.NAMED_QUERY_FIND_RESOURCE_SUB_TYPE_ROLE_TYPES, Tuple.class)
                .getResultStream()
                .collect(
                    Collectors.groupingBy(
                            t -> (String)t.get("resourceSubType"),
                            Collectors.mapping(t -> (RoleType)t.get("roleType"), Collectors.toSet())
                           ));
    }
    
    @Override
    public Optional<RoleType> findRoleTypeByResourceTypeAndSubType(ResourceType resourceType, String resourceSubType) {
        List<RoleType> roleTypes =     
                    em.createNamedQuery(AuthorizationRule.NAMED_QUERY_FIND_ROLE_TYPE_BY_RESOURCE_TYPE_AND_SUB_TYPE, RoleType.class)
                        .setParameter("resourceType", resourceType)
                        .setParameter("resourceSubType", resourceSubType)
                        .getResultList();
        if(roleTypes.size() >  1) {
            throw new NonUniqueResultException("More than one role type found for resource type: %s and sub type: %s");
        }
        
        return roleTypes.isEmpty() ? Optional.empty() : Optional.ofNullable(roleTypes.get(0));
    }

    @Override
    public Set<String> findResourceSubTypesByResourceTypeAndRoleType(ResourceType resourceType, RoleType roleType) {
        return new HashSet<>(em.createNamedQuery(AuthorizationRule.NAMED_QUERY_FIND_RESOURCE_SUB_TYPE_BY_RESOURCE_TYPE_AND_ROLE_TYPE, String.class)
                .setParameter("resourceType", resourceType)
                .setParameter("roleType", roleType)
                .getResultList());
    }
}
