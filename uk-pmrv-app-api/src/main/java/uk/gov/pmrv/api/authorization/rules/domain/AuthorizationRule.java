package uk.gov.pmrv.api.authorization.rules.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import uk.gov.pmrv.api.common.domain.enumeration.RoleType;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.Set;


/**
 * The AuthorizationRule Entity.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "au_rule")
@NamedQuery(
        name = AuthorizationRule.NAMED_QUERY_FIND_RULE_SCOPE_PERMISSIONS_BY_SERVICE_AND_ROLE_TYPE,
        query = "select distinct new uk.gov.pmrv.api.authorization.rules.domain.AuthorizationRuleScopePermission("
                + "rule.resourceSubType, rule.handler, ps.permission) "
                + "from AuthorizationRule rule "
                + "join rule.services service "
                + "left join ResourceScopePermission ps on ("
                + "		ps.resourceType = rule.resourceType "
                + "		and ps.roleType = rule.roleType "
                + "     and ((rule.resourceSubType is not null and ps.resourceSubType = rule.resourceSubType) or "
                + "     		(rule.resourceSubType is null and ps.resourceSubType is null)) "
                + "     and ps.scope = rule.scope) "
                + "where service.name = :serviceName "
                + "and rule.roleType = :roleType")
@NamedQuery(
        name = AuthorizationRule.NAMED_QUERY_FIND_RULE_SCOPE_PERMISSIONS_BY_RESOURCE_TYPE_SCOPE_AND_ROLE_TYPE,
        query = "select distinct new uk.gov.pmrv.api.authorization.rules.domain.AuthorizationRuleScopePermission("
                + "rule.resourceSubType, rule.handler, ps.permission) "
                + "from AuthorizationRule rule "
                + "left join ResourceScopePermission ps on ("
                + "		ps.resourceType = rule.resourceType "
                + "		and ps.roleType = rule.roleType "
                + "     and (rule.resourceSubType is not null and ps.resourceSubType = rule.resourceSubType) "
                + "     and ps.scope = rule.scope) "
                + "where rule.resourceType = :resourceType "
                + "and rule.scope = :scope "
                + "and rule.roleType = :roleType")
@NamedQuery(
        name = AuthorizationRule.NAMED_QUERY_FIND_RESOURCE_SUB_TYPE_ROLE_TYPES,
        query = "select distinct rule.resourceSubType as resourceSubType, rule.roleType as roleType "
                + "from AuthorizationRule rule "
                + "where rule.resourceSubType is not null")
@NamedQuery(
        name = AuthorizationRule.NAMED_QUERY_FIND_ROLE_TYPE_BY_RESOURCE_TYPE_AND_SUB_TYPE,
        query = "select distinct rule.roleType as roleType "
                + "from AuthorizationRule rule "
                + "where rule.resourceType = :resourceType and "
                + "rule.resourceSubType = :resourceSubType ")
@NamedQuery(
        name = AuthorizationRule.NAMED_QUERY_FIND_RESOURCE_SUB_TYPE_BY_RESOURCE_TYPE_AND_ROLE_TYPE,
        query = "select distinct rule.resourceSubType as resourceSubType "
                + "from AuthorizationRule rule "
                + "where rule.resourceType = :resourceType and "
                + "rule.roleType = :roleType ")
public class AuthorizationRule {

    public static final String NAMED_QUERY_FIND_RULE_SCOPE_PERMISSIONS_BY_SERVICE_AND_ROLE_TYPE = "AuthorizationRule.findRuleScopePermissionsByServiceAndRoleType";
    public static final String NAMED_QUERY_FIND_RULE_SCOPE_PERMISSIONS_BY_RESOURCE_TYPE_SCOPE_AND_ROLE_TYPE = "AuthorizationRule.findRuleScopePermissionsByResourceTypeScopeAndRoleType";
    public static final String NAMED_QUERY_FIND_RESOURCE_SUB_TYPE_ROLE_TYPES = "AuthorizationRule.findResourceSubTypeRoleTypes";
    public static final String NAMED_QUERY_FIND_ROLE_TYPE_BY_RESOURCE_TYPE_AND_SUB_TYPE = "AuthorizationRule.findRoleTypeByResourceTypeAndSubType";
    public static final String NAMED_QUERY_FIND_RESOURCE_SUB_TYPE_BY_RESOURCE_TYPE_AND_ROLE_TYPE = "AuthorizationRule.findResourceSubTypesByResourceTypeAndRoleType";

    /** The id. */
    @EqualsAndHashCode.Exclude
    @Id
    @SequenceGenerator(name = "au_rules_id_generator", sequenceName = "au_rule_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "au_rules_id_generator")
    private Long id;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "resource_type")
    private ResourceType resourceType;

    @Column(name = "resource_sub_type")
    private String resourceSubType;

    @NotNull
    @Column(name = "handler")
    private String handler;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "role_type")
    private RoleType roleType;

    @Enumerated(EnumType.STRING)
    @Column(name = "scope")
    private Scope scope;

    @Builder.Default
    @ManyToMany(mappedBy = "rules")
    private Set<AuthorizedService> services = new HashSet<>();
}
