package uk.gov.pmrv.api.verificationbody.domain;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.AttributeOverride;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.NamedAttributeNode;
import javax.persistence.NamedEntityGraph;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import uk.gov.pmrv.api.common.domain.Address;
import uk.gov.pmrv.api.common.domain.enumeration.EmissionTradingScheme;
import uk.gov.pmrv.api.verificationbody.enumeration.VerificationBodyStatus;

/**
 * The persistent class for the verification_body database table.
 */
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@EntityListeners({AuditingEntityListener.class})
@Table(name = "verification_body")
@NamedEntityGraph(name = "emissionTradingSchemes-graph", attributeNodes = @NamedAttributeNode("emissionTradingSchemes"))
@NamedQuery(
        name = VerificationBody.NAMED_QUERY_FIND_BY_ID,
        query = "select vb from VerificationBody vb "
                + "where vb.id = :id ")
@NamedQuery(
        name = VerificationBody.NAMED_QUERY_FIND_ACTIVE_VER_BODIES_ACCREDITED_TO_EMISSION_TRADING_SCHEME,
        query = "select new uk.gov.pmrv.api.verificationbody.domain.dto.VerificationBodyNameInfoDTO(vb.id, vb.name) "
                + "from VerificationBody vb "
                + "inner join vb.emissionTradingSchemes ets "
                + "where ets = :emissionTradingScheme "
                + "and vb.status = 'ACTIVE'")
@NamedQuery(
        name = VerificationBody.NAMED_QUERY_IS_VER_BODY_ACCREDITED_TO_EMISSION_TRADING_SCHEME,
        query = "select count(vb) > 0 "
                + "from VerificationBody vb "
                + "inner join vb.emissionTradingSchemes ets "
                + "where vb.id = :vbId "
                + "and ets = :emissionTradingScheme "
                + "and vb.status = 'ACTIVE'")
public class VerificationBody {
    public static final String NAMED_QUERY_FIND_BY_ID = "VerificationBody.findById";
    public static final String NAMED_QUERY_FIND_ACTIVE_VER_BODIES_ACCREDITED_TO_EMISSION_TRADING_SCHEME = "VerificationBody.findActiveVerificationBodiesAccreditedToEmissionTradingScheme";
    public static final String NAMED_QUERY_IS_VER_BODY_ACCREDITED_TO_EMISSION_TRADING_SCHEME = "VerificationBody.isVerificationBodyAccreditedToEmissionTradingScheme";

    @EqualsAndHashCode.Exclude
    @Id
    @SequenceGenerator(name = "verification_body_id_generator", sequenceName = "verification_body_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "verification_body_id_generator")
    private Long id;

    @Column(name = "name")
    @NotBlank
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    @NotNull
    private VerificationBodyStatus status;

    @Embedded
    @AttributeOverride(name="line1", column=@Column(name="addr_line1"))
    @AttributeOverride(name="line2", column=@Column(name="addr_line2"))
    @AttributeOverride(name="city", column=@Column(name="addr_city"))
    @AttributeOverride(name="country", column=@Column(name="addr_country"))
    @AttributeOverride(name="postcode", column=@Column(name="addr_postcode"))
    @NotNull
    @Valid
    private Address address;

    @EqualsAndHashCode.Exclude
    @NotNull
    @Column(name = "created_date")
    @CreatedDate
    private LocalDateTime createdDate;

    @Column(name = "accreditation_reference_number", unique = true)
    @NotBlank
    private String accreditationReferenceNumber;

    @EqualsAndHashCode.Exclude
    @Builder.Default
    @ElementCollection
    @CollectionTable(name = "verification_body_emission_trading_scheme", joinColumns = @JoinColumn(name = "verification_body_id"))
    @Column(name="emission_trading_scheme")
    @Enumerated(EnumType.STRING)
    private Set<EmissionTradingScheme> emissionTradingSchemes = new HashSet<>();
}
