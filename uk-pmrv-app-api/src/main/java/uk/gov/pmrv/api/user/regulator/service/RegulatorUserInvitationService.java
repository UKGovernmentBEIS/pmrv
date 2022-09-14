package uk.gov.pmrv.api.user.regulator.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.pmrv.api.authorization.core.domain.dto.AuthorityInfoDTO;
import uk.gov.pmrv.api.authorization.regulator.service.RegulatorAuthorityService;
import uk.gov.pmrv.api.authorization.regulator.transform.RegulatorPermissionsAdapter;
import uk.gov.pmrv.api.common.domain.model.PmrvUser;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.files.common.domain.dto.FileDTO;
import uk.gov.pmrv.api.user.core.domain.dto.InvitedUserInfoDTO;
import uk.gov.pmrv.api.user.regulator.domain.RegulatorInvitedUserDTO;
import uk.gov.pmrv.api.user.regulator.domain.RegulatorUserDTO;

import static uk.gov.pmrv.api.user.core.domain.enumeration.AuthenticationStatus.PENDING;

@Log4j2
@Service
@RequiredArgsConstructor
public class RegulatorUserInvitationService {

    private final RegulatorAuthorityService regulatorAuthorityService;
    private final RegulatorUserNotificationGateway regulatorUserNotificationGateway;
    private final RegulatorUserAuthService regulatorUserAuthService;
    private final RegulatorUserTokenVerificationService regulatorUserTokenVerificationService;
    
    @Transactional
    public void inviteRegulatorUser(RegulatorInvitedUserDTO regulatorInvitedUser, FileDTO signature, PmrvUser authUser) {
        //register invited user
        final String userId = regulatorUserAuthService.registerRegulatorInvitedUser(regulatorInvitedUser.getUserDetails(), signature);

        //create authorities for invited user
        String authorityUuid = regulatorAuthorityService.createRegulatorAuthorityPermissions(
            authUser,
            userId,
            authUser.getCompetentAuthority(),
            RegulatorPermissionsAdapter.getPermissionsFromPermissionGroupLevels(regulatorInvitedUser.getPermissions()));

        //send invitation email
        regulatorUserNotificationGateway.notifyInvitedUser(regulatorInvitedUser.getUserDetails(), authorityUuid);
    }

    public InvitedUserInfoDTO acceptInvitation(String invitationToken) {
        AuthorityInfoDTO authorityInfo = regulatorUserTokenVerificationService.verifyInvitationTokenForPendingAuthority(invitationToken);

        RegulatorUserDTO user = regulatorUserAuthService.getRegulatorUserById(authorityInfo.getUserId());

        if(user.getStatus() != PENDING) {
            log.error("User '{}' found with status '{}'", authorityInfo::getUserId, user::getStatus);
            throw new BusinessException(ErrorCode.USER_INVALID_STATUS);
        }

        return InvitedUserInfoDTO.builder().email(user.getEmail()).build();
    }
}
