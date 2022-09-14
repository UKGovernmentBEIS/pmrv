package uk.gov.pmrv.api.user.operator.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.user.core.domain.dto.InvitedUserInfoDTO;
import uk.gov.pmrv.api.user.core.domain.enumeration.UserInvitationStatus;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class OperatorInvitedUserInfoDTO extends InvitedUserInfoDTO {

	private String firstName;
	private String lastName;
	private String roleCode;
    private String accountInstallationName;
    private UserInvitationStatus invitationStatus;
	
}
