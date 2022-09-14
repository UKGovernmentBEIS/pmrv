package uk.gov.pmrv.api.authorization.core.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uk.gov.pmrv.api.common.domain.enumeration.RoleType;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedAttributeNode;
import javax.persistence.NamedEntityGraph;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a role template
 *
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "au_role")
@NamedEntityGraph(
	name = "role-permissions-graph",
	attributeNodes = {  
		@NamedAttributeNode("rolePermissions")
	}
)
public class Role {

	@Id
	@SequenceGenerator(name = "au_role_id_generator", sequenceName = "au_role_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "au_role_id_generator")
	private Long id;
	
	@NotNull
	@Column(name = "name")
	private String name;

	@EqualsAndHashCode.Include()
	@NotNull
	@Column(name = "code", unique = true)
	private String code;
	
	/**
	 * The type of the role
	 */
	@NotNull
	@Enumerated(EnumType.STRING)
	@Column(name = "type")
	private RoleType type;

	@Builder.Default
	@OneToMany(mappedBy = "role", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<RolePermission> rolePermissions = new ArrayList<>();
	
	/**
	 * Add the provided permission
	 * @param rolePermission {@link RolePermission}
	 */
	public void addPermission(RolePermission rolePermission) {
		if(rolePermissions == null) {
			rolePermissions = new ArrayList<>();
		}
		rolePermission.setRole(this);
		rolePermissions.add(rolePermission);
	}
}
