package uk.gov.pmrv.api.user.operator.service;

import static uk.gov.pmrv.api.user.core.domain.enumeration.UserInvitationStatus.ACCEPTED;
import static uk.gov.pmrv.api.user.core.domain.enumeration.UserInvitationStatus.PENDING_USER_ENABLE;
import static uk.gov.pmrv.api.user.core.domain.enumeration.UserInvitationStatus.PENDING_USER_REGISTRATION;

import java.util.Set;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uk.gov.pmrv.api.authorization.AuthorityConstants;
import uk.gov.pmrv.api.authorization.core.service.RoleService;
import uk.gov.pmrv.api.authorization.operator.service.OperatorAuthorityService;
import uk.gov.pmrv.api.common.domain.enumeration.RoleType;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.user.core.domain.dto.UserInfoDTO;
import uk.gov.pmrv.api.user.core.service.auth.UserAuthService;
import uk.gov.pmrv.api.user.operator.domain.OperatorUserAcceptInvitationDTO;
import uk.gov.pmrv.api.user.core.domain.enumeration.UserInvitationStatus;

@Service
@RequiredArgsConstructor
public class OperatorRoleCodeAcceptInvitationServiceDefaultImpl implements OperatorRoleCodeAcceptInvitationService {

    private final OperatorAuthorityService operatorAuthorityService;
    private final OperatorUserNotificationGateway operatorUserNotificationGateway;
    private final RoleService roleService;
    private final OperatorUserAuthService operatorUserAuthService;
    private final UserAuthService userAuthService;

    @Transactional
    public UserInvitationStatus acceptInvitation(OperatorUserAcceptInvitationDTO operatorUserAcceptInvitation) {
        switch (operatorUserAcceptInvitation.getUserAuthenticationStatus()) {
            case DELETED:
                throw new BusinessException(ErrorCode.USER_STATUS_DELETED);
            case PENDING:
                return PENDING_USER_REGISTRATION;
            case REGISTERED:
                if(operatorUserAuthService.hasOperatorUserPassword(operatorUserAcceptInvitation.getUserId())) {
                    // Update status to Accepted and get inviter
                    String userId = operatorAuthorityService.acceptAuthority(operatorUserAcceptInvitation.getUserAuthorityId())
                            .getCreatedBy();
                    UserInfoDTO inviterUser = userAuthService.getUserByUserId(userId);

                    // Notify invitee and inviter
                    operatorUserNotificationGateway.notifyInviteeAcceptedInvitation(operatorUserAcceptInvitation);
                    operatorUserNotificationGateway.notifyInviterAcceptedInvitation(operatorUserAcceptInvitation, inviterUser);

                    return ACCEPTED;
                } else {
                    return PENDING_USER_ENABLE;
                }
            default:
                throw new UnsupportedOperationException();
        }
    }

    @Override
    public Set<String> getRoleCodes() {
        return roleService.getCodesByType(RoleType.OPERATOR).stream()
            .filter(roleCode -> !AuthorityConstants.EMITTER_CONTACT.equals(roleCode))
            .collect(Collectors.toSet());
    }

}
