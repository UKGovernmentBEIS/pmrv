package uk.gov.pmrv.api.user.operator.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.pmrv.api.user.core.domain.enumeration.AuthenticationStatus;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OperatorUserAcceptInvitationDTO {

    private String userId;
    private String email;
    private String firstName;
    private String lastName;
    private Long userAuthorityId;
    private Long accountId;
    private String accountInstallationName;
    private AuthenticationStatus userAuthenticationStatus;
}
