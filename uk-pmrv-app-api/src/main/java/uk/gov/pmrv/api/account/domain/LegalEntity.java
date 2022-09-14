package uk.gov.pmrv.api.account.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uk.gov.pmrv.api.account.domain.enumeration.LegalEntityStatus;
import uk.gov.pmrv.api.account.domain.enumeration.LegalEntityType;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedAttributeNode;
import javax.persistence.NamedEntityGraph;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * The Legal Entity.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "account_legal_entity")
@NamedEntityGraph(name = "fetchLocation", attributeNodes = @NamedAttributeNode(value = "location"))
@NamedQuery(
		name = LegalEntity.NAMED_QUERY_FIND_ACTIVE,
		query = "select le "
				+ "from LegalEntity le "
				+ "where le.status = 'ACTIVE'")
@NamedQuery(
		name = LegalEntity.NAMED_QUERY_FIND_ACTIVE_LEGAL_ENTITIES_BY_ACCOUNTS,
		query = "select distinct(le) "
				+ "from Account ac "
				+ "join ac.legalEntity le "
				+ "where ac.id in :accountIds "
				+ "and le.status = 'ACTIVE'" )
@NamedQuery(
		name = LegalEntity.NAMED_QUERY_EXISTS_LEGAL_ENTITY_IN_ANY_OF_ACCOUNTS,
		query = "select count(le) > 0 "
				+ "from Account ac "
				+ "join ac.legalEntity le "
				+ "where le.id = :leId "
				+ "and ac.id in :accountIds")
@NamedQuery(
		name = LegalEntity.NAMED_QUERY_EXISTS_ACTIVE_LEGAL_ENTITY_NAME_IN_ANY_OF_ACCOUNTS,
		query = "select count(le) > 0 "
				+ "from Account ac "
				+ "join ac.legalEntity le "
				+ "where le.name = :leName "
				+ "and le.status = 'ACTIVE' "
				+ "and ac.id in :accountIds")
@NamedQuery(
		name = LegalEntity.NAMED_QUERY_EXISTS_ACTIVE_LEGAL_ENTITY,
		query = "select count(le.name) > 0 "
				+ "from LegalEntity le "
				+ "where le.name = :leName "
				+ "and le.status = 'ACTIVE'")
public class LegalEntity {

	public static final String NAMED_QUERY_FIND_ACTIVE = "LegalEntity.findActive";
	public static final String NAMED_QUERY_FIND_ACTIVE_LEGAL_ENTITIES_BY_ACCOUNTS = "LegalEntity.findActiveLegalEntitiesByAccounts";
	public static final String NAMED_QUERY_EXISTS_LEGAL_ENTITY_IN_ANY_OF_ACCOUNTS = "LegalEntity.existsLegalEntityInAnyOfAccounts";
	public static final String NAMED_QUERY_EXISTS_ACTIVE_LEGAL_ENTITY_NAME_IN_ANY_OF_ACCOUNTS = "LegalEntity.existsActiveLegalEntityNameInAnyOfAccounts";
	public static final String NAMED_QUERY_EXISTS_ACTIVE_LEGAL_ENTITY = "LegalEntity.existsActiveLegalEntity";

	/** The id. */
	@Id
	@SequenceGenerator(name = "legal_entity_id_generator", sequenceName = "account_legal_entity_seq", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "legal_entity_id_generator")
	private Long id;

	/** The Legal Entity name. */
	@EqualsAndHashCode.Include()
	@Column(name = "name", unique = true)
	@NotBlank
	private String name;

	/** The {@link Location}. */
	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinColumn(name = "location_id")
	@NotNull
	private LocationOnShore location;

	/** The {@link LegalEntityType}. */
	@Enumerated(EnumType.STRING)
	@Column(name = "type")
	@NotNull
	private LegalEntityType type;

	/** The companies house reference number. */
	@Column(name = "reference_number")
	private String referenceNumber;

	/** The explanation of why organisation does not have a companies house reference number. */
	@Column(name = "no_reference_number")
	private String noReferenceNumberReason;

	@Enumerated(EnumType.STRING)
	@Column(name = "status")
	@NotNull
	private LegalEntityStatus status;

}
