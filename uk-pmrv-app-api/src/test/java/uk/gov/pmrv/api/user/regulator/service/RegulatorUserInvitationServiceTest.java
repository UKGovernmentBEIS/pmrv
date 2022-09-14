package uk.gov.pmrv.api.user.regulator.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.pmrv.api.authorization.regulator.domain.RegulatorPermissionGroup.MANAGE_USERS_AND_CONTACTS;
import static uk.gov.pmrv.api.authorization.regulator.domain.RegulatorPermissionLevel.EXECUTE;
import static uk.gov.pmrv.api.common.exception.ErrorCode.USER_INVALID_STATUS;

import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.authorization.core.domain.AuthorityStatus;
import uk.gov.pmrv.api.authorization.core.domain.Permission;
import uk.gov.pmrv.api.authorization.core.domain.dto.AuthorityInfoDTO;
import uk.gov.pmrv.api.authorization.regulator.service.RegulatorAuthorityService;
import uk.gov.pmrv.api.common.domain.enumeration.CompetentAuthority;
import uk.gov.pmrv.api.common.domain.enumeration.RoleType;
import uk.gov.pmrv.api.common.domain.model.PmrvAuthority;
import uk.gov.pmrv.api.common.domain.model.PmrvUser;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.files.common.domain.dto.FileDTO;
import uk.gov.pmrv.api.user.core.domain.dto.InvitedUserInfoDTO;
import uk.gov.pmrv.api.user.regulator.domain.RegulatorInvitedUserDTO;
import uk.gov.pmrv.api.user.regulator.domain.RegulatorInvitedUserDetailsDTO;
import uk.gov.pmrv.api.user.regulator.domain.RegulatorUserDTO;
import uk.gov.pmrv.api.user.core.domain.enumeration.AuthenticationStatus;

@ExtendWith(MockitoExtension.class)
class RegulatorUserInvitationServiceTest {

    @InjectMocks
    private RegulatorUserInvitationService service;

    @Mock
    private RegulatorAuthorityService regulatorAuthorityService;

    @Mock
    private RegulatorUserNotificationGateway regulatorUserNotificationGateway;

    @Mock
    private RegulatorUserAuthService regulatorUserAuthService;

    @Mock
    private RegulatorUserTokenVerificationService regulatorUserTokenVerificationService;

    @Test
    void inviteRegulatorUser() {
        String userId = "userId";
        CompetentAuthority ca = CompetentAuthority.ENGLAND;
        PmrvUser authUser = PmrvUser.builder()
            .userId("authUser")
            .roleType(RoleType.REGULATOR)
            .authorities(List.of(PmrvAuthority.builder().competentAuthority(ca).build()))
            .build();

        String authorityUuid = "uuid";
        RegulatorInvitedUserDTO regulatorInvitedUser = createInvitedUser();
        
        FileDTO signature = FileDTO.builder()
                .fileContent("content".getBytes())
                .fileName("signature")
                .fileSize(1L)
                .fileType("type")
                .build();

        when(regulatorUserAuthService.registerRegulatorInvitedUser(regulatorInvitedUser.getUserDetails(), signature))
            .thenReturn(userId);
        when(regulatorAuthorityService
            .createRegulatorAuthorityPermissions(authUser, userId, ca, List.of(Permission.PERM_CA_USERS_EDIT)))
            .thenReturn(authorityUuid);

        //invoke
        service.inviteRegulatorUser(regulatorInvitedUser,signature,  authUser);

        //verify
        verify(regulatorUserAuthService, times(1))
            .registerRegulatorInvitedUser(regulatorInvitedUser.getUserDetails(), signature);
        verify(regulatorAuthorityService, times(1))
            .createRegulatorAuthorityPermissions(authUser, userId, ca, List.of(Permission.PERM_CA_USERS_EDIT));
        verify(regulatorUserNotificationGateway, times(1))
            .notifyInvitedUser(regulatorInvitedUser.getUserDetails(), authorityUuid);
    }

    @Test
    void acceptInvitation() {
        String invitationToken = "invitationToken";
        String userEmail = "userEmail";
        AuthorityInfoDTO authorityInfo = AuthorityInfoDTO.builder()
            .id(1L)
            .authorityStatus(AuthorityStatus.PENDING)
            .userId("userId")
            .build();

        RegulatorUserDTO regulatorUser = RegulatorUserDTO.builder()
            .email(userEmail)
            .status(AuthenticationStatus.PENDING)
            .build();

        InvitedUserInfoDTO expectedInvitedUserInfo = InvitedUserInfoDTO.builder().email(userEmail).build();

        when(regulatorUserTokenVerificationService.verifyInvitationTokenForPendingAuthority(invitationToken)).thenReturn(authorityInfo);
        when(regulatorUserAuthService.getRegulatorUserById(authorityInfo.getUserId())).thenReturn(regulatorUser);

        InvitedUserInfoDTO actualInvitedUserInfo = service.acceptInvitation(invitationToken);

        assertEquals(expectedInvitedUserInfo, actualInvitedUserInfo);

        verify(regulatorUserTokenVerificationService, times(1)).verifyInvitationTokenForPendingAuthority(invitationToken);
        verify(regulatorUserAuthService, times(1)).getRegulatorUserById(authorityInfo.getUserId());
    }

    @Test
    void checkRegulatorUserRegistrationEligibility_when_user_authentication_status_not_pending() {
        String invitationToken = "invitationToken";
        AuthorityInfoDTO authorityInfo = AuthorityInfoDTO.builder()
            .id(1L)
            .authorityStatus(AuthorityStatus.PENDING)
            .userId("userId")
            .build();

        RegulatorUserDTO regulatorUser = RegulatorUserDTO.builder().status(AuthenticationStatus.REGISTERED).build();

        when(regulatorUserTokenVerificationService.verifyInvitationTokenForPendingAuthority(invitationToken)).thenReturn(authorityInfo);
        when(regulatorUserAuthService.getRegulatorUserById(authorityInfo.getUserId())).thenReturn(regulatorUser);

        BusinessException businessException = assertThrows(BusinessException.class, () ->
            service.acceptInvitation(invitationToken));

        assertThat(businessException.getErrorCode()).isEqualTo(USER_INVALID_STATUS);

        verify(regulatorUserTokenVerificationService, times(1)).verifyInvitationTokenForPendingAuthority(invitationToken);
        verify(regulatorUserAuthService, times(1)).getRegulatorUserById(authorityInfo.getUserId());
    }


    private RegulatorInvitedUserDTO createInvitedUser() {
        RegulatorInvitedUserDTO invitedUser =
            RegulatorInvitedUserDTO.builder()
                .userDetails(RegulatorInvitedUserDetailsDTO.builder()
                    .firstName("fn")
                    .lastName("ln")
                    .email("em@em.gr")
                    .jobTitle("title")
                    .phoneNumber("210000")
                    .build())
                .permissions(Map.of(MANAGE_USERS_AND_CONTACTS, EXECUTE))
                .build();
        return invitedUser;
    }

}
