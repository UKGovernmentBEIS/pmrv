package uk.gov.pmrv.api.common.domain;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.NotBlank;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Address {
	
	/** The line 1 address. */
    @Column(name = "line1")
    @NotBlank
    private String line1;

    /** The line 2 address. */
    @Column(name = "line2")
    private String line2;

    /** The city. */
    @Column(name = "city")
    @NotBlank
    private String city;

    /** The country. */
    @Column(name = "country")
    @NotBlank
    private String country;

    /** The postcode. */
    @Column(name = "postcode")
    @NotBlank
    private String postcode;
}
