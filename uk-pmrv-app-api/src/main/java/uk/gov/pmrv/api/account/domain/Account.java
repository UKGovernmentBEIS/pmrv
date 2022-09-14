package uk.gov.pmrv.api.account.domain;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.EnumMap;
import java.util.Map;
import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ColumnResult;
import javax.persistence.ConstructorResult;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapKeyColumn;
import javax.persistence.MapKeyEnumerated;
import javax.persistence.NamedAttributeNode;
import javax.persistence.NamedEntityGraph;
import javax.persistence.NamedQuery;
import javax.persistence.NamedSubgraph;
import javax.persistence.OneToOne;
import javax.persistence.QueryHint;
import javax.persistence.SqlResultSetMapping;
import javax.persistence.Table;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Type;
import uk.gov.pmrv.api.account.domain.dto.AccountInfoDTO;
import uk.gov.pmrv.api.account.domain.enumeration.AccountContactType;
import uk.gov.pmrv.api.account.domain.enumeration.AccountStatus;
import uk.gov.pmrv.api.account.domain.enumeration.AccountType;
import uk.gov.pmrv.api.account.domain.enumeration.EmitterType;
import uk.gov.pmrv.api.account.domain.enumeration.InstallationCategory;
import uk.gov.pmrv.api.common.domain.enumeration.CompetentAuthority;
import uk.gov.pmrv.api.common.domain.enumeration.EmissionTradingScheme;

/**
 * The Account Entity.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "account")
@NamedEntityGraph(name = "location-and-le-with-location-graph",
    attributeNodes = {
        @NamedAttributeNode(value = "location"),
        @NamedAttributeNode(value = "legalEntity", subgraph = "legal-entity-subgraph")
    },
    subgraphs = {
        @NamedSubgraph(
            name = "legal-entity-subgraph",
            attributeNodes = {
                @NamedAttributeNode("location")
            }
        )
    })
@NamedEntityGraph(name = "contacts-graph",
    attributeNodes = {
        @NamedAttributeNode(value = "contacts")
    })
@NamedQuery(
        name = Account.NAMED_QUERY_FIND_BY_ACCOUNT_IDS_AND_COMP_AUTH,
        query = "select distinct acc "
                + "from Account acc "
                + "where acc.competentAuthority = :compAuth and "
                + "acc.id in :accountIds")
@NamedQuery(
        name = Account.NAMED_QUERY_FIND_BY_ACCOUNT_IDS_AND_VER_BODY,
        query = "select distinct acc "
                + "from Account acc "
                + "where acc.verificationBodyId = :verBodyId and "
                + "acc.id in :accountIds")
@NamedQuery(
        name = Account.NAMED_QUERY_FIND_ACCOUNT_CONTACTS_BY_CA_AND_CONTACT_TYPE,
        query = "select new uk.gov.pmrv.api.account.domain.dto.AccountContactInfoDTO(acc.id, acc.name, VALUE(contacts)) "
                + "from Account acc "
                + "left join acc.contacts contacts on KEY(contacts) = :contactType "
                + "where acc.competentAuthority = :ca "
                + "and acc.status not in (uk.gov.pmrv.api.account.domain.enumeration.AccountStatus.UNAPPROVED, uk.gov.pmrv.api.account.domain.enumeration.AccountStatus.DENIED) "
                + "order by acc.name")
@NamedQuery(
        name = Account.NAMED_QUERY_FIND_ACCOUNT_CONTACTS_BY_VB_AND_CONTACT_TYPE,
        // TODO Fix TYPE
        query = "select new uk.gov.pmrv.api.account.domain.dto.AccountContactVbInfoDTO(acc.id, acc.name, 'UK ETS Installation', VALUE(contacts)) "
                + "from Account acc "
                + "left join acc.contacts contacts on KEY(contacts) = :contactType "
                + "where acc.verificationBodyId = :vbId "
                + "order by acc.name")
@NamedQuery(
        name = Account.NAMED_QUERY_FIND_ACCOUNT_CONTACTS_BY_ACCOUNT_IDS_AND_CONTACT_TYPE,
        query = "select new uk.gov.pmrv.api.account.domain.dto.AccountContactInfoDTO(acc.id, acc.name, VALUE(contacts)) "
                + "from Account acc "
                + "join acc.contacts contacts on KEY(contacts) = :contactType "
                + "where acc.id in (:accountIds)")
@NamedQuery(
        name = Account.NAMED_QUERY_FIND_ACCOUNTS_BY_CONTACT_TYPE_AND_USER_ID,
        query = "select acc "
                + "from Account acc "
                + "inner join acc.contacts contacts "
                + "where KEY(contacts) = :contactType "
                + "and contacts = :userId")
@NamedQuery(
        name = Account.NAMED_QUERY_FIND_APPROVED_IDS_BY_CA,
        query = "select ac.id "
                + "from Account ac "
                + "where ac.status not in (uk.gov.pmrv.api.account.domain.enumeration.AccountStatus.UNAPPROVED, uk.gov.pmrv.api.account.domain.enumeration.AccountStatus.DENIED) "
                + "and ac.competentAuthority = :ca")
@NamedQuery(
        name = Account.NAMED_QUERY_FIND_IDS_BY_VB,
        query = "select ac.id "
                + "from Account ac "
                + "where ac.verificationBodyId = :vbId")
@NamedQuery(
        name = Account.NAMED_QUERY_FIND_ACCOUNTS_BY_VB_IN_LIST,
        query = "select ac "
                + "from Account ac "
                + "where ac.verificationBodyId in (:vbIds)")
@NamedQuery(
        name = Account.NAMED_QUERY_FIND_ACCOUNTS_BY_VB_AND_EMISSION_TRADING_SCHEME_IN_LIST,
        query = "select ac "
                + "from Account ac "
                + "where ac.verificationBodyId = :vbId "
                + "and ac.emissionTradingScheme in (:emissionTradingSchemes)")
@NamedQuery(
        name = Account.NAMED_QUERY_FIND_BY_ID_FOR_UPDATE,
        query = "select ac from Account ac where ac.id = :id",
        hints = {@QueryHint(name = "javax.persistence.query.timeout", value = "5000")}
)
@SqlResultSetMapping(
        name = Account.ACCOUNT_INFO_DTO_RESULT_MAPPER,
        classes = {
                @ConstructorResult(
                    targetClass = AccountInfoDTO.class,
                    columns = {
                            @ColumnResult(name = "id", type = Long.class),
                            @ColumnResult(name = "name"),
                            @ColumnResult(name = "emitterId"),
                            @ColumnResult(name = "status", type = String.class),
                            @ColumnResult(name = "legalEntityName")
                    }
                )})

public class Account {
    
    public static final String ACCOUNT_INFO_DTO_RESULT_MAPPER = "AccountInfoDTOResultMapper";

    public static final String NAMED_QUERY_FIND_BY_ACCOUNT_IDS_AND_COMP_AUTH = "Account.findByAccountIdsAndCompAuth";
    public static final String NAMED_QUERY_FIND_BY_ACCOUNT_IDS_AND_VER_BODY = "Account.findByAccountIdsAndVerificationBody";
    public static final String NAMED_QUERY_FIND_ACCOUNT_CONTACTS_BY_CA_AND_CONTACT_TYPE = "Account.findAccountContactsByCAAndContactType";
    public static final String NAMED_QUERY_FIND_ACCOUNT_CONTACTS_BY_VB_AND_CONTACT_TYPE = "Account.findAccountContactsByVbAndContactType";
    public static final String NAMED_QUERY_FIND_ACCOUNT_CONTACTS_BY_ACCOUNT_IDS_AND_CONTACT_TYPE = "Account.findAccountContactsByAccountIdsAndContactType";
    public static final String NAMED_QUERY_FIND_ACCOUNTS_BY_CONTACT_TYPE_AND_USER_ID = "Account.findAccountsByContactTypeAndUserId";
    public static final String NAMED_QUERY_FIND_APPROVED_IDS_BY_CA = "Account.findApprovedIdsByCA";
    public static final String NAMED_QUERY_FIND_IDS_BY_VB = "Account.findIdsByVB";
    public static final String NAMED_QUERY_FIND_ACCOUNTS_BY_VB_IN_LIST = "Account.findAccountsByVBInList";
    public static final String NAMED_QUERY_FIND_ACCOUNTS_BY_VB_AND_EMISSION_TRADING_SCHEME_IN_LIST = "Account.findAccountsByVBAndEmissionTradingSchemeinList";
    public static final String NAMED_QUERY_FIND_BY_ID_FOR_UPDATE = "Account.findByIdForUpdate";

    @Id
    private Long id;

    @EqualsAndHashCode.Include()
    @Column(name = "name", unique = true)
    @NotBlank
    private String name;

    @Column(name = "site_name")
    @NotBlank
    private String siteName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "legal_entity_id")
    private LegalEntity legalEntity;

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "location_id")
    private Location location;

    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    @NotNull
    private AccountType accountType;

    @Enumerated(EnumType.STRING)
    @Column(name = "competent_authority")
    @NotNull
    private CompetentAuthority competentAuthority;

    @Column(name = "commencement_date")
    @Type(type = "org.hibernate.type.LocalDateType")
    @NotNull
    private LocalDate commencementDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    @NotNull
    private AccountStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name = "emission_trading_scheme")
    @NotNull
    private EmissionTradingScheme emissionTradingScheme;

    @Column(name = "accepted_date")
    private LocalDateTime acceptedDate;

    @Column(name = "verification_body_id")
    private Long verificationBodyId;

    @Builder.Default
    @ElementCollection
    @MapKeyColumn(name="contact_type")
    @MapKeyEnumerated(EnumType.STRING)
    @Column(name="user_id")
    @CollectionTable(name = "account_contact", joinColumns = @JoinColumn(name = "account_id"))
    private Map<AccountContactType, String> contacts = new EnumMap<>(AccountContactType.class);

    @Column(name = "migrated_account_id")
    private String migratedAccountId;

    @EqualsAndHashCode.Include()
    @Column(name = "emitter_id", unique = true)
    @NotBlank
    @Size(min = 7, max = 7)
    private String emitterId;
    
    @Column(name = "sop_id")
    @Min(0)
    @Max(9999999999L)
    private Long sopId;
    
    @Column(name = "registry_id")
    @Min(1000000)
    @Max(9999999)
    private Integer registryId;

    @Enumerated(EnumType.STRING)
    @Column(name = "emitter_type")
    private EmitterType emitterType;

    @Enumerated(EnumType.STRING)
    @Column(name = "installation_category")
    private InstallationCategory installationCategory;
}
