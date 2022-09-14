package uk.gov.pmrv.api.terms.domain;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;


/**
 * The persistent class for the terms database table.
 */
@Entity
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Table(name = "terms")
public class Terms {

    /**
     * The id.
     */
    @Id
    @SequenceGenerator(name = "terms_id_generator", sequenceName = "terms_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "terms_id_generator")
    private Long id;

    /**
     * The url for terms and conditions.
     */
    @Column(name = "url")
    @NotBlank
    private String url;

    /**
     * The terms and conditions version.
     */
    @EqualsAndHashCode.Include()
    @Column(name = "version", unique = true)
    @NotNull
    private Short version;

}
