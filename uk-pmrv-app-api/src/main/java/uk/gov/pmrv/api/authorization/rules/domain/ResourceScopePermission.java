package uk.gov.pmrv.api.authorization.rules.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import uk.gov.pmrv.api.authorization.core.domain.Permission;
import uk.gov.pmrv.api.common.domain.enumeration.RoleType;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "au_resource_scope_permission")
public class ResourceScopePermission {

    @EqualsAndHashCode.Exclude
    @Id
    @SequenceGenerator(name = "au_resource_scope_permission_id_generator", sequenceName = "au_resource_scope_permission_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "au_resource_scope_permission_id_generator")
    private Long id;
    
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "resource_type")
    private ResourceType resourceType;

    @Column(name = "resource_sub_type")
    private String resourceSubType;
    
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "permission")
    private Permission permission;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "scope")
    private Scope scope;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "role_type")
    private RoleType roleType;
    
}
