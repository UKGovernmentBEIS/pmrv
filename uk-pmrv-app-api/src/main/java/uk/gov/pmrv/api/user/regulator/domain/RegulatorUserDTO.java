package uk.gov.pmrv.api.user.regulator.domain;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.files.common.domain.dto.FileInfoDTO;
import uk.gov.pmrv.api.user.core.domain.dto.ApplicationUserDTO;
import uk.gov.pmrv.api.user.core.domain.enumeration.AuthenticationStatus;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
public class RegulatorUserDTO extends ApplicationUserDTO {
	
    private AuthenticationStatus status;

    @NotBlank(message = "{userAccount.jobTitle.notEmpty}")
    @Size(max = 255, message = "{userAccount.jobTitle.typeMismatch}")
    private String jobTitle;

    @NotBlank(message = "{phoneNumber.number.notEmpty}")
    @Size(max = 255, message = "{phoneNumber.number.typeMismatch}")
    private String phoneNumber;

    @Size(max = 255, message = "{phoneNumber.number.typeMismatch}")
    private String mobileNumber;
    
    @JsonProperty(access = Access.READ_ONLY)
    private FileInfoDTO signature;
}
