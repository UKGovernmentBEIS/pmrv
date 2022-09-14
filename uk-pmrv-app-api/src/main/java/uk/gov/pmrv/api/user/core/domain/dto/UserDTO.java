package uk.gov.pmrv.api.user.core.domain.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@SuperBuilder
public abstract class UserDTO {

	/** The user email. */
	@Email(message = "{userAccount.email.typeMismatch}")
	@Size(max = 255, message = "{userAccount.email.typeMismatch}")
	@NotBlank(message = "{userAccount.email.notEmpty}")
	private String email;

	/** The first name. */
	@NotBlank(message = "{userAccount.firstName.notEmpty}")
	@Size(max = 255, message = "{userAccount.firstName.typeMismatch}")
	private String firstName;

	/** The last name. */
	@NotBlank(message = "{userAccount.lastName.notEmpty}")
	@Size(max = 255, message = "{userAccount.lastName.typeMismatch}")
	private String lastName;
	
	@JsonIgnore
	public String getFullName() {
	    return firstName + " " + lastName;
	}
}
