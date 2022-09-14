package uk.gov.pmrv.api.migration.operatoruser.invite;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountOperatorAdminUserInvitation {

    private Long rowId;

    private String firstName;
    private String lastName;
    private String email;

    private Long accountId;

    private String inviterEmail;
}
