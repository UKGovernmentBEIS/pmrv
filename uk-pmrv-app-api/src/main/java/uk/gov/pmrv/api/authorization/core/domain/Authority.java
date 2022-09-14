package uk.gov.pmrv.api.authorization.core.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.security.core.GrantedAuthority;
import uk.gov.pmrv.api.common.domain.enumeration.CompetentAuthority;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedAttributeNode;
import javax.persistence.NamedEntityGraph;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents an authority for a user. <br/>
 * An authority is a {@link Role} template instance
 *
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@EntityListeners({AuditingEntityListener.class})
@Table(name = "au_authority",
        uniqueConstraints =
        @UniqueConstraint(columnNames = {"user_id", "code", "account_id", "competent_authority"}))
@NamedEntityGraph(
        name = "authority-permissions-graph",
        attributeNodes = {
                @NamedAttributeNode("authorityPermissions")
        })
@NamedQuery(
        name = Authority.NAMED_QUERY_FIND_ASSIGNED_PERMISSIONS_BY_USER_ID,
        query = "select distinct(authorityPermissions.permission) "
                + "from Authority au "
                + "join au.authorityPermissions authorityPermissions "
                + "where au.userId = :userId ")
@NamedQuery(
        name = Authority.NAMED_QUERY_FIND_USERS_BY_CA_ASSIGNED_WITH_PMRV_SUPER_USER_ROLE_PERMISSIONS,
        query = "select au "
                + "from Authority au "
                + "join au.authorityPermissions authorityPermissions "
                + "where au.competentAuthority = :competentAuthority "
                + "and exists (select 1 from AuthorityPermission ap where ap.authority = au and ap.permission = 'PERM_ACCOUNT_USERS_EDIT') "
                + "and exists (select 1 from AuthorityPermission ap where ap.authority = au and ap.permission = 'PERM_CA_USERS_EDIT') "
                + "and exists (select 1 from AuthorityPermission ap where ap.authority = au and ap.permission = 'PERM_VB_MANAGE') "
                + "order by au.id asc")
@NamedQuery(
        name = Authority.NAMED_QUERY_FIND_OPERATOR_USER_AUTHORITY_ROLE_LIST_BY_ACCOUNT,
        query = "select new uk.gov.pmrv.api.authorization.core.domain.dto.AuthorityRoleDTO("
                + "au.userId, au.status, r.name, r.code, au.creationDate) "
                + "from Authority au "
                + "join Role r on r.code = au.code "
                + "where au.accountId = :accountId ")
@NamedQuery(
        name = Authority.NAMED_QUERY_FIND_NON_PENDING_OPERATOR_USER_AUTHORITY_ROLE_LIST_BY_ACCOUNT,
        query = "select new uk.gov.pmrv.api.authorization.core.domain.dto.AuthorityRoleDTO("
                + "au.userId, au.status, r.name, r.code, au.creationDate) "
                + "from Authority au "
                + "join Role r on r.code = au.code "
                + "where au.accountId = :accountId "
                + "and au.status <> 'PENDING' ")
@NamedQuery(
        name = Authority.NAMED_QUERY_FIND_ACTIVE_OPERATOR_USERS_BY_ACCOUNT_AND_ROLE,
        query = "select distinct(au.userId) "
                + "from Authority au "
                + "where au.accountId = :accountId "
                + "and au.code = :roleCode "
                + "and au.status = 'ACTIVE' ")
@NamedQuery(
        name = Authority.NAMED_QUERY_FIND_VERIFIER_USER_AUTHORITY_ROLE_LIST_BY_VERIFICATION_BODY,
        query = "select new uk.gov.pmrv.api.authorization.core.domain.dto.AuthorityRoleDTO("
                + "au.userId, au.status, r.name, r.code, au.creationDate) "
                + "from Authority au "
                + "join Role r on r.code = au.code "
                + "where au.verificationBodyId = :verificationBodyId")
@NamedQuery(
        name = Authority.NAMED_QUERY_FIND_NON_PENDING_VERIFIER_USER_AUTHORITY_ROLE_LIST_BY_VERIFICATION_BODY,
        query = "select new uk.gov.pmrv.api.authorization.core.domain.dto.AuthorityRoleDTO("
                + "au.userId, au.status, r.name, r.code, au.creationDate) "
                + "from Authority au "
                + "join Role r on r.code = au.code "
                + "where au.verificationBodyId = :verificationBodyId "
                + "and au.status <> 'PENDING'")
@NamedQuery(
        name = Authority.NAMED_QUERY_FIND_USERS_BY_VERIFICATION_BODY_AND_ROLE_CODE,
        query = "select au.userId "
                + "from Authority au "
                + "where au.verificationBodyId = :verificationBodyId "
                + "and au.status = 'ACTIVE' "
                + "and au.code = :roleCode ")
@NamedQuery(
        name = Authority.NAMED_QUERY_UPDATE_CA_REGULATOR_USER,
        query = "update Authority au "
                + "set au.status = :newAuthorityStatus "
                + "where au.userId = :userId "
                + "and au.competentAuthority = :competentAuthority")
@NamedQuery(
        name = Authority.NAMED_QUERY_EXISTS_OTHER_ACCOUNT_OPERATOR_ADMIN,
        query = "select count(au.userId) > 0 "
                + "from Authority au "
                + "where au.accountId = :accountId "
                + "and au.userId <> :userId "
                + "and au.status = 'ACTIVE' "
                + "and au.code = 'operator_admin'")
@NamedQuery(
        name = Authority.NAMED_QUERY_EXISTS_OTHER_VERIFICATION_BODY_ADMIN,
        query = "select count(au.userId) > 0 "
                + "from Authority au "
                + "where au.verificationBodyId = :verificationBodyId "
                + "and au.userId <> :userId "
                + "and au.status = 'ACTIVE' "
                + "and au.code = 'verifier_admin'")
@NamedQuery(
        name = Authority.NAMED_QUERY_FIND_REGULATOR_USER_ASSIGNED_RESOURCE_SUB_TYPES,
        query = "select new uk.gov.pmrv.api.authorization.regulator.domain.RegulatorUserAssignedSubResource("
                + "au.competentAuthority, sc.resourceSubType) "
                + "from Authority au "
                + "join au.authorityPermissions ap "
                + "join ResourceScopePermission sc on sc.permission = ap.permission "
                + "where au.userId = :userId "
                + "and sc.resourceType = :resourceType "
                + "and sc.scope = :scope "
                + "and au.status = 'ACTIVE' ")
@NamedQuery(
        name = Authority.NAMED_QUERY_FIND_VERIFIER_USER_ASSIGNED_RESOURCE_SUB_TYPES,
        query = "select new uk.gov.pmrv.api.authorization.verifier.domain.VerifierUserAssignedSubResource("
                + "au.verificationBodyId, sc.resourceSubType) "
                + "from Authority au "
                + "join au.authorityPermissions ap "
                + "join ResourceScopePermission sc on sc.permission = ap.permission "
                + "where au.userId = :userId "
                + "and sc.resourceType = :resourceType "
                + "and sc.scope = :scope "
                + "and au.status = 'ACTIVE' ")
@NamedQuery(
    name = Authority.NAMED_QUERY_FIND_OPERATOR_USER_ASSIGNED_RESOURCE_SUB_TYPES_BY_ACCOUNT,
    query = "select new uk.gov.pmrv.api.authorization.operator.domain.OperatorUserAssignedSubResource("
        + "au.accountId, sc.resourceSubType) "
        + "from Authority au "
        + "join au.authorityPermissions ap "
        + "join ResourceScopePermission sc on sc.permission = ap.permission "
        + "where au.userId = :userId "
        + "and au.accountId in (:accountIds) "
        + "and sc.resourceType = :resourceType "
        + "and sc.scope = :scope "
        + "and au.status = 'ACTIVE' ")
@NamedQuery(
        name = Authority.NAMED_QUERY_FIND_REGULATOR_USERS_WITH_SCOPE_ON_RESOURCE_TYPE_AND_SUB_TYPE_AND_CA,
        query = "select distinct(au.userId) "
                + "from Authority au "
                + "join au.authorityPermissions ap "
                + "join ResourceScopePermission sc on sc.permission = ap.permission "
                + "where sc.resourceType = :resourceType "
                + "and sc.resourceSubType = :resourceSubType "
                + "and sc.scope = :scope "
                + "and au.competentAuthority = :competentAuthority "
                + "and au.status = 'ACTIVE' ")
@NamedQuery(
        name = Authority.NAMED_QUERY_FIND_VERIFIER_USERS_WITH_SCOPE_ON_RESOURCE_TYPE_AND_SUB_TYPE_AND_VB_ID,
        query = "select distinct(au.userId) "
                + "from Authority au "
                + "join au.authorityPermissions ap "
                + "join ResourceScopePermission sc on sc.permission = ap.permission "
                + "where sc.resourceType = :resourceType "
                + "and sc.resourceSubType = :resourceSubType "
                + "and sc.scope = :scope "
                + "and au.verificationBodyId = :verificationBodyId "
                + "and au.status = 'ACTIVE' ")
@NamedQuery(
        name = Authority.NAMED_QUERY_FIND_OPERATOR_USERS_WITH_SCOPE_ON_RESOURCE_TYPE_AND_SUB_TYPE_AND_ACCOUNT_ID,
        query = "select distinct(au.userId) "
                + "from Authority au "
                + "join au.authorityPermissions ap "
                + "join ResourceScopePermission sc on sc.permission = ap.permission "
                + "where sc.resourceType = :resourceType "
                + "and sc.resourceSubType = :resourceSubType "
                + "and sc.scope = :scope "
                + "and au.accountId = :accountId "
                + "and au.status = 'ACTIVE' ")
@NamedQuery(
        name = Authority.NAMED_QUERY_FIND_OPERATOR_USERS_BY_ACCOUNT,
        query = "select au.userId "
                + "from Authority au "
                + "where au.accountId = :accountId "
                + "and au.status = 'ACTIVE'")
@NamedQuery(
        name = Authority.NAMED_QUERY_FIND_REGULATOR_USERS_BY_CA,
        query = "select au.userId "
                + "from Authority au "
                + "where au.competentAuthority = :competentAuthority "
                + "and au.status = 'ACTIVE'")
@NamedQuery(
        name = Authority.NAMED_QUERY_FIND_VERIFIER_USERS_BY_VB_ID,
        query = "select au.userId "
                + "from Authority au "
                + "where au.verificationBodyId = :verificationBodyId "
                + "and au.status = 'ACTIVE'")
@NamedQuery(
        name = Authority.NAMED_QUERY_FIND_STATUS_BY_USERS_AND_ACCOUNT_ID,
        query = "select au.userId as userId, au.status as status "
                + "from Authority au "
                + "where au.userId in (:userIds) "
                + "and au.accountId = :accountId ")
@NamedQuery(
        name = Authority.NAMED_QUERY_FIND_STATUS_BY_USERS_AND_CA,
        query = "select au.userId as userId, au.status as status "
                + "from Authority au "
                + "where au.userId in (:userIds) "
                + "and au.competentAuthority =: competentAuthority")
public class Authority implements GrantedAuthority {
    public static final String NAMED_QUERY_FIND_ASSIGNED_PERMISSIONS_BY_USER_ID = "Authority.findAssignedPermissionsByUserId";
    public static final String NAMED_QUERY_FIND_USERS_BY_CA_ASSIGNED_WITH_PMRV_SUPER_USER_ROLE_PERMISSIONS = "Authority.findUsersByCAAssignedWithPmrvSuperUserRolePermissions";
    public static final String NAMED_QUERY_FIND_OPERATOR_USER_AUTHORITY_ROLE_LIST_BY_ACCOUNT = "Authority.findOperatorUserAuthorityRoleListByAccount";
    public static final String NAMED_QUERY_FIND_ACTIVE_OPERATOR_USERS_BY_ACCOUNT_AND_ROLE = "Authority.findActiveOperatorUsersByAccountAndRole";
    public static final String NAMED_QUERY_FIND_VERIFIER_USER_AUTHORITY_ROLE_LIST_BY_VERIFICATION_BODY = "Authority.findVerifierUserAuthorityRoleListByVerificationBody";
    public static final String NAMED_QUERY_FIND_USERS_BY_VERIFICATION_BODY_AND_ROLE_CODE = "Authority.findUsersByVerificationBodyAndRoleCode";
    public static final String NAMED_QUERY_UPDATE_CA_REGULATOR_USER = "Authority.updateCARegulatorUser";
    public static final String NAMED_QUERY_EXISTS_OTHER_ACCOUNT_OPERATOR_ADMIN = "Authority.existsOtherAccountOperatorAdmin";
    public static final String NAMED_QUERY_EXISTS_OTHER_VERIFICATION_BODY_ADMIN = "Authority.existsOtherVerificationBodyAdmin";
    public static final String NAMED_QUERY_FIND_REGULATOR_USER_ASSIGNED_RESOURCE_SUB_TYPES = "Authority.findRegulatorUserAssignedResourceSubTypes";
    public static final String NAMED_QUERY_FIND_REGULATOR_USERS_WITH_SCOPE_ON_RESOURCE_TYPE_AND_SUB_TYPE_AND_CA = "Authority.findREgulatorUsersWithScopeOnResourceTypeAndSubTypeAndCA";
    public static final String NAMED_QUERY_FIND_VERIFIER_USERS_WITH_SCOPE_ON_RESOURCE_TYPE_AND_SUB_TYPE_AND_VB_ID = "Authority.findVerifierUsersWithScopeOnResourceTypeAndSubTypeAndVbId";
    public static final String NAMED_QUERY_FIND_OPERATOR_USERS_WITH_SCOPE_ON_RESOURCE_TYPE_AND_SUB_TYPE_AND_ACCOUNT_ID = "Authority.findOperatorUsersWithScopeOnResourceTypeAndSubTypeAndAccountId";
    public static final String NAMED_QUERY_FIND_OPERATOR_USERS_BY_ACCOUNT = "Authohrity.findOperatorUsersByAccount";
    public static final String NAMED_QUERY_FIND_REGULATOR_USERS_BY_CA = "Authohrity.findRegulatorUsersByCA";
    public static final String NAMED_QUERY_FIND_VERIFIER_USERS_BY_VB_ID = "Authohrity.findVerifierUsersByVbId";
    public static final String NAMED_QUERY_FIND_NON_PENDING_OPERATOR_USER_AUTHORITY_ROLE_LIST_BY_ACCOUNT = "Authority.findNonPendingOperatorUserAuthorityRoleListByAccount";
    public static final String NAMED_QUERY_FIND_NON_PENDING_VERIFIER_USER_AUTHORITY_ROLE_LIST_BY_VERIFICATION_BODY = "Authority.findNonPendingVerifierUserAuthorityRoleListByVerificationBody";
    public static final String NAMED_QUERY_FIND_STATUS_BY_USERS_AND_ACCOUNT_ID = "Authohrity.findStatusByUsersAndAccountId";
    public static final String NAMED_QUERY_FIND_STATUS_BY_USERS_AND_CA = "Authority.findStatusByUsersAndCA";
    public static final String NAMED_QUERY_FIND_VERIFIER_USER_ASSIGNED_RESOURCE_SUB_TYPES = "Authority.findVerifierUserAssignedResourceSubTypes";
    public static final String NAMED_QUERY_FIND_OPERATOR_USER_ASSIGNED_RESOURCE_SUB_TYPES_BY_ACCOUNT = "Authority.findOperatorUserAssignedResourceSubTypesByAccount";

    @Id
    @SequenceGenerator(name = "au_authority_id_generator", sequenceName = "au_authority_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "au_authority_id_generator")
    private Long id;

    /**
     * The keycloak userid.
     */
    @EqualsAndHashCode.Include
    @NotNull
    @Column(name = "user_id")
    private String userId;

    /**
     * The code of the role used to initialize the authority.
     */
    @EqualsAndHashCode.Include
    @Column(name = "code")
    private String code;

    /**
     * The status of the authority.
     */
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private AuthorityStatus status;

    /**
     * The authorized account id (used for operator user authorities).
     */
    @EqualsAndHashCode.Include
    @Column(name = "account_id")
    private Long accountId;

    /**
     * The authorized CA (used for regulator user authorities).
     */
    @EqualsAndHashCode.Include
    @Enumerated(EnumType.STRING)
    @Column(name = "competent_authority")
    private CompetentAuthority competentAuthority;

    @Column(name = "verification_body_id")
    private Long verificationBodyId;

    @Column(name = "uuid")
    private String uuid;

    @NotNull
    @Column(name = "creation_date")
    @CreatedDate
    private LocalDateTime creationDate;

    @NotNull
    @Column(name = "created_by")
    private String createdBy;

    /**
     * The authority permissions.
     */
    @Builder.Default
    @OneToMany(mappedBy = "authority", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AuthorityPermission> authorityPermissions = new ArrayList<>();

    /**
     *Adds the provided {@link AuthorityPermission} to the {@link Authority#authorityPermissions}.
     * @param authorityPermission the provided {@link AuthorityPermission}
     */
    public void addPermission(AuthorityPermission authorityPermission) {
        if (authorityPermissions == null) {
            authorityPermissions = new ArrayList<>();
        }
        authorityPermission.setAuthority(this);
        authorityPermissions.add(authorityPermission);
    }

    @Override
    public String getAuthority() {
        return getCode();
    }

}
