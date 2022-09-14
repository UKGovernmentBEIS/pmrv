package uk.gov.pmrv.api.user.operator.domain;

import javax.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.common.domain.dto.AddressDTO;
import uk.gov.pmrv.api.user.core.domain.dto.ApplicationUserDTO;
import uk.gov.pmrv.api.user.core.domain.dto.PhoneNumberDTO;
import uk.gov.pmrv.api.user.core.domain.dto.validation.PhoneNumberIntegrity;
import uk.gov.pmrv.api.user.core.domain.dto.validation.PhoneNumberNotBlank;
import uk.gov.pmrv.api.user.core.domain.enumeration.AuthenticationStatus;

/**
 * The Operator's details DTO.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
public class OperatorUserDTO extends ApplicationUserDTO {

	/** The authentication status. */
    private AuthenticationStatus status;

    /** The address. */
    @Valid
    private AddressDTO address;

    /** The phone number. */
    @PhoneNumberNotBlank(message = "{userAccount.phoneNumber.notEmpty}")
    @PhoneNumberIntegrity(message = "{userAccount.phoneNumber.typeMismatch}")
    @Valid
    private PhoneNumberDTO phoneNumber;

    /** The mobile number. */
    @PhoneNumberIntegrity(message = "{userAccount.mobileNumber.typeMismatch}")
    @Valid
    private PhoneNumberDTO mobileNumber;
}
