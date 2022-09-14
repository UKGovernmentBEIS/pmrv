package uk.gov.pmrv.api.user.core.domain.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uk.gov.pmrv.api.user.core.domain.dto.validation.CountryCode;

import javax.validation.constraints.Size;

/**
 * The phone number details DTO.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PhoneNumberDTO {

    /** The country code phone. */
    @CountryCode(message = "{phoneNumber.countryCode.typeMismatch}")
    private String countryCode;

    /** The phone number. */
    @Size(max = 255, message = "{phoneNumber.number.typeMismatch}")
    private String number;
}
