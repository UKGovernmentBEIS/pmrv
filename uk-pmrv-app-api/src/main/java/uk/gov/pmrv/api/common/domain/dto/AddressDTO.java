package uk.gov.pmrv.api.common.domain.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.pmrv.api.common.domain.dto.validation.Country;

/**
 * The address details DTO.
 */
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class AddressDTO {

    /** The line 1 address. */
    @NotBlank(message = "{address.line1.notEmpty}")
    @Size(max = 255, message = "{address.line1.typeMismatch}")
    private String line1;

    /** The line 2 address as optional. */
    @Size(max = 255, message = "{address.line2.typeMismatch}")
    private String line2;

    /** The city. */
    @NotBlank(message = "{address.city.notEmpty}")
    @Size(max = 255, message = "{address.city.typeMismatch}")
    private String city;

    /** The country. */
    @NotBlank(message = "{address.country.notEmpty}")
    @Country(message = "{address.country.typeMismatch}")
    private String country;

    /** The postal code. */
    @NotBlank(message = "{address.postcode.notEmpty}")
    @Size(max=64, message = "{address.postcode.typeMismatch}")
    private String postcode;
}
