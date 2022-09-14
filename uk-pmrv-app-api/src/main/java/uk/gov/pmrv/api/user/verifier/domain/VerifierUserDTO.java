package uk.gov.pmrv.api.user.verifier.domain;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.user.core.domain.dto.ApplicationUserDTO;
import uk.gov.pmrv.api.user.core.domain.enumeration.AuthenticationStatus;

/**
 * The Verifier's details DTO.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
public class VerifierUserDTO extends ApplicationUserDTO {

    /** The authentication status. */
    private AuthenticationStatus status;

    /** The phone number. */
    @NotBlank(message = "{phoneNumber.number.notEmpty}")
    @Size(max = 255, message = "{phoneNumber.number.typeMismatch}")
    private String phoneNumber;

    /** The mobile number. */
    @Size(max = 255, message = "{phoneNumber.number.typeMismatch}")
    private String mobileNumber;
}
