package uk.gov.pmrv.api.user.core.domain.dto;

import javax.validation.constraints.Email;
import javax.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Holds the user email.
 *
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EmailDTO {

    @Email(message = "{userAccount.email.typeMismatch}")
    @Size(max = 255, message = "{userAccount.email.typeMismatch}")
    private String email;
}
