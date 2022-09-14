package uk.gov.pmrv.api.user.operator.service;

import java.util.Set;
import uk.gov.pmrv.api.user.operator.domain.OperatorUserAcceptInvitationDTO;
import uk.gov.pmrv.api.user.core.domain.enumeration.UserInvitationStatus;

public interface OperatorRoleCodeAcceptInvitationService {

    UserInvitationStatus acceptInvitation(OperatorUserAcceptInvitationDTO operatorUserAcceptInvitation);

    Set<String> getRoleCodes();
}
