package uk.gov.pmrv.api.user.regulator.domain;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.user.core.domain.dto.UserDTO;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
public class RegulatorInvitedUserDetailsDTO extends UserDTO {

    @NotBlank(message = "{userAccount.jobTitle.notEmpty}")
    @Size(max = 255, message = "{userAccount.jobTitle.typeMismatch}")
    private String jobTitle;

    @NotBlank(message = "{phoneNumber.number.notEmpty}")
    @Size(max = 255, message = "{phoneNumber.number.typeMismatch}")
    private String phoneNumber;

    @Size(max = 255, message = "{phoneNumber.number.typeMismatch}")
    private String mobileNumber;

}
