package uk.gov.pmrv.api.authorization.rules.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.pmrv.api.authorization.rules.domain.AuthorizationRule;
import uk.gov.pmrv.api.authorization.rules.domain.AuthorizationRuleScopePermission;
import uk.gov.pmrv.api.authorization.rules.domain.ResourceType;
import uk.gov.pmrv.api.authorization.rules.domain.Scope;
import uk.gov.pmrv.api.common.domain.enumeration.RoleType;

import java.util.List;

@Repository
public interface AuthorizationRuleRepository extends JpaRepository<AuthorizationRule, Long>, AuthorizationRuleCustomRepository {
    
    @Transactional(readOnly = true)
    @Query(name =  AuthorizationRule.NAMED_QUERY_FIND_RULE_SCOPE_PERMISSIONS_BY_SERVICE_AND_ROLE_TYPE)
    List<AuthorizationRuleScopePermission> findRulePermissionsByServiceAndRoleType(String serviceName, RoleType roleType);
    
    @Transactional(readOnly = true)
    @Query(name =  AuthorizationRule.NAMED_QUERY_FIND_RULE_SCOPE_PERMISSIONS_BY_RESOURCE_TYPE_SCOPE_AND_ROLE_TYPE)
    List<AuthorizationRuleScopePermission> findRulePermissionsByResourceTypeScopeAndRoleType(
            ResourceType resourceType, Scope scope, RoleType roleType);
    
}
