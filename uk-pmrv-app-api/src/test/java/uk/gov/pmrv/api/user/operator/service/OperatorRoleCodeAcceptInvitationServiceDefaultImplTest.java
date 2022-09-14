package uk.gov.pmrv.api.user.operator.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.authorization.AuthorityConstants;
import uk.gov.pmrv.api.authorization.core.domain.Authority;
import uk.gov.pmrv.api.authorization.core.service.RoleService;
import uk.gov.pmrv.api.authorization.operator.service.OperatorAuthorityService;
import uk.gov.pmrv.api.common.domain.enumeration.RoleType;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.user.core.domain.dto.UserInfoDTO;
import uk.gov.pmrv.api.user.core.service.auth.UserAuthService;
import uk.gov.pmrv.api.user.operator.domain.OperatorUserAcceptInvitationDTO;
import uk.gov.pmrv.api.user.core.domain.enumeration.AuthenticationStatus;
import uk.gov.pmrv.api.user.core.domain.enumeration.UserInvitationStatus;

@ExtendWith(MockitoExtension.class)
class OperatorRoleCodeAcceptInvitationServiceDefaultImplTest {

    @InjectMocks
    private OperatorRoleCodeAcceptInvitationServiceDefaultImpl service;

    @Mock
    private OperatorAuthorityService operatorAuthorityService;

    @Mock
    private OperatorUserNotificationGateway operatorUserNotificationGateway;

    @Mock
    private RoleService roleService;

    @Mock
    private OperatorUserAuthService operatorUserAuthService;

    @Mock
    private UserAuthService userAuthService;

    @Test
    void acceptInvitation_user_authentication_status_deleted() {
        OperatorUserAcceptInvitationDTO operatorUserAcceptInvitation = OperatorUserAcceptInvitationDTO.builder()
            .userAuthenticationStatus(AuthenticationStatus.DELETED)
            .build();

        BusinessException businessException = assertThrows(BusinessException.class,
            () -> service.acceptInvitation(operatorUserAcceptInvitation));

        assertEquals(ErrorCode.USER_STATUS_DELETED, businessException.getErrorCode());

        verifyNoInteractions(operatorAuthorityService, operatorUserNotificationGateway, operatorUserAuthService, userAuthService);
    }

    @Test
    void acceptInvitation_user_authentication_status_pending() {
        OperatorUserAcceptInvitationDTO operatorUserAcceptInvitation = OperatorUserAcceptInvitationDTO.builder()
            .userAuthenticationStatus(AuthenticationStatus.PENDING)
            .build();

        UserInvitationStatus userInvitationStatus = service.acceptInvitation(operatorUserAcceptInvitation);

        assertEquals(UserInvitationStatus.PENDING_USER_REGISTRATION, userInvitationStatus);

        verifyNoInteractions(operatorAuthorityService, operatorUserNotificationGateway, operatorUserAuthService, userAuthService);
    }

    @Test
    void acceptInvitation_user_authentication_status_registered_and_has_password() {
        Long authorityId = 1L;
        String inviterUserId = "inviterUserId";
        OperatorUserAcceptInvitationDTO operatorUserAcceptInvitation = OperatorUserAcceptInvitationDTO.builder()
            .userAuthenticationStatus(AuthenticationStatus.REGISTERED)
            .userId("userId")
            .accountId(2L)
            .userAuthorityId(authorityId)
            .build();
        Authority authority = Authority.builder().createdBy(inviterUserId).build();
        UserInfoDTO inviterUser = UserInfoDTO.builder().build();

        when(operatorUserAuthService.hasOperatorUserPassword(operatorUserAcceptInvitation.getUserId())).thenReturn(true);
        when(operatorAuthorityService.acceptAuthority(authorityId)).thenReturn(authority);
        when(userAuthService.getUserByUserId(inviterUserId)).thenReturn(inviterUser);

        UserInvitationStatus userInvitationStatus = service.acceptInvitation(operatorUserAcceptInvitation);

        assertEquals(UserInvitationStatus.ACCEPTED, userInvitationStatus);

        verify(operatorUserAuthService, times(1))
                .hasOperatorUserPassword(operatorUserAcceptInvitation.getUserId());
        verify(operatorAuthorityService, times(1))
                .acceptAuthority(authorityId);
        verify(userAuthService, times(1))
                .getUserByUserId(inviterUserId);
        verify(operatorUserNotificationGateway, times(1))
                .notifyInviteeAcceptedInvitation(operatorUserAcceptInvitation);
        verify(operatorUserNotificationGateway, times(1))
                .notifyInviterAcceptedInvitation(operatorUserAcceptInvitation, inviterUser);
    }

    @Test
    void acceptInvitation_user_authentication_status_registered_and_no_password() {
        Long authorityId = 1L;
        String userId = "userId";
        OperatorUserAcceptInvitationDTO operatorUserAcceptInvitation = OperatorUserAcceptInvitationDTO.builder()
            .userAuthenticationStatus(AuthenticationStatus.REGISTERED)
            .userId(userId)
            .userAuthorityId(authorityId)
            .build();

        when(operatorUserAuthService.hasOperatorUserPassword(operatorUserAcceptInvitation.getUserId())).thenReturn(false);

        UserInvitationStatus userInvitationStatus = service.acceptInvitation(operatorUserAcceptInvitation);

        assertEquals(UserInvitationStatus.PENDING_USER_ENABLE, userInvitationStatus);

        verifyNoInteractions(operatorAuthorityService, operatorUserNotificationGateway, userAuthService);
    }

    @Test
    void getRoleCodes() {
        Set<String> operatorRoleCodes =
            Set.of(AuthorityConstants.OPERATOR_ADMIN_ROLE_CODE, AuthorityConstants.EMITTER_CONTACT);

        when(roleService.getCodesByType(RoleType.OPERATOR)).thenReturn(operatorRoleCodes);

        Set<String> roleCodes = service.getRoleCodes();

        assertThat(roleCodes).containsOnly(AuthorityConstants.OPERATOR_ADMIN_ROLE_CODE);
    }
}