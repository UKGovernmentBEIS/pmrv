package uk.gov.pmrv.api.referencedata.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;

/**
 * The country entity reference data.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "ref_country")
public class Country implements ReferenceData {

    /** The country id. */
    @Id
    @SequenceGenerator(name = "ref_country_id_generator", sequenceName = "ref_country_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ref_country_id_generator")
    private Long id;

    /** The country code. */
    @EqualsAndHashCode.Include()
    @Column(name = "code", unique=true)
    @NotBlank
    private String code;

    /** The country name. */
    @Column(name = "name")
    @NotBlank
    private String name;

    /** The country official name. */
    @Column(name = "official_name")
    @NotBlank
    private String officialName;

}
