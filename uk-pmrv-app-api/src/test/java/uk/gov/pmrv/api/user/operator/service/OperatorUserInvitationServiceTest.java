package uk.gov.pmrv.api.user.operator.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.pmrv.api.authorization.core.service.UserRoleTypeService;
import uk.gov.pmrv.api.common.domain.enumeration.RoleType;
import uk.gov.pmrv.api.common.domain.model.PmrvUser;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.user.operator.domain.OperatorUserInvitationDTO;
import uk.gov.pmrv.api.user.core.domain.dto.UserInfoDTO;
import uk.gov.pmrv.api.user.core.domain.enumeration.AuthenticationStatus;
import uk.gov.pmrv.api.user.core.service.auth.UserAuthService;

@ExtendWith(MockitoExtension.class)
class OperatorUserInvitationServiceTest {

    @InjectMocks
    private OperatorUserInvitationService service;

    @Mock
    private UserAuthService authUserService;

    @Mock
    private UserRoleTypeService userRoleTypeService;

    @Mock
    private OperatorUserRegistrationService operatorUserRegistrationService;

    @Mock
    private ExistingOperatorUserInvitationService existingOperatorUserInvitationService;
    
    @Test
    void addOperatorUserToAccountThrowsExceptionWhenRequesterIsRegulatorAndRoleCodeNotOperatorAdmin() {
        String operatorUserRoleCode = "operator";
        String email = "email";
        Long accountId = 1L;
        String currentUserId = "currentUserId";
        PmrvUser currentUser = createPmrvUser(currentUserId, RoleType.REGULATOR);
        OperatorUserInvitationDTO
            operatorUserInvitationDTO = createOperatorUserInvitationDTO(email, operatorUserRoleCode);
        when(userRoleTypeService.isUserRegulator(currentUser.getUserId())).thenReturn(true);

        BusinessException businessException =
            assertThrows(BusinessException.class,
                () -> service.inviteUserToAccount(accountId, operatorUserInvitationDTO, currentUser));

        assertEquals(ErrorCode.AUTHORITY_USER_REGULATOR_NOT_ALLOWED_TO_ADD_OPERATOR_ROLE_TO_ACCOUNT, businessException.getErrorCode());

        verify(userRoleTypeService, times(1)).isUserRegulator(currentUser.getUserId());
        verify(authUserService, never()).getUserByEmail(email);
        verify(operatorUserRegistrationService, never()).registerUserToAccountWithStatusPending(any(), anyLong(), any());
        verify(existingOperatorUserInvitationService, never()).addExistingUserToAccount(any(), anyLong(), anyString(), any(), any());
    }

    @Test
    void addOperatorUserToAccountWhenUserNotExists() {
        String operatorUserRoleCode = "operator";
        String email = "email";
        Long accountId = 1L;
        String currentUserId = "currentUserId";
        PmrvUser currentUser = createPmrvUser(currentUserId, RoleType.OPERATOR);
        OperatorUserInvitationDTO
            operatorUserInvitationDTO = createOperatorUserInvitationDTO(email, operatorUserRoleCode);

        when(userRoleTypeService.isUserRegulator(currentUser.getUserId())).thenReturn(false);
        when(authUserService.getUserByEmail(email)).thenReturn(Optional.empty());

        service.inviteUserToAccount(accountId, operatorUserInvitationDTO, currentUser);

        verify(userRoleTypeService, times(1)).isUserRegulator(currentUser.getUserId());
        verify(authUserService, times(1)).getUserByEmail(email);
        verify(operatorUserRegistrationService, times(1))
            .registerUserToAccountWithStatusPending(operatorUserInvitationDTO, accountId, currentUser);
        verify(existingOperatorUserInvitationService, never()).addExistingUserToAccount(any(), anyLong(), anyString(), any(), any());
    }

    @Test
    void addOperatorUserToAccountWhenUserAlreadyExists() {
        String operatorUserRoleCode = "operator";
        String email = "email";
        Long accountId = 1L;
        String currentUserId = "currentUserId";
        String operatorUserId = "operatorUserId";
        PmrvUser currentUser = createPmrvUser(currentUserId, RoleType.OPERATOR);
        OperatorUserInvitationDTO
            operatorUserInvitationDTO = createOperatorUserInvitationDTO(email, operatorUserRoleCode);
        UserInfoDTO userInfoDTO = createUserInfoDTO(operatorUserId, AuthenticationStatus.REGISTERED);

        when(userRoleTypeService.isUserRegulator(currentUser.getUserId())).thenReturn(false);
        when(authUserService.getUserByEmail(email)).thenReturn(Optional.of(userInfoDTO));

        service.inviteUserToAccount(accountId, operatorUserInvitationDTO, currentUser);

        verify(userRoleTypeService, times(1)).isUserRegulator(currentUser.getUserId());
        verify(authUserService, times(1)).getUserByEmail(email);
        verify(operatorUserRegistrationService, never()).registerUserToAccountWithStatusPending(any(), anyLong(), any());
        verify(existingOperatorUserInvitationService, times(1))
            .addExistingUserToAccount(operatorUserInvitationDTO, accountId, operatorUserId, AuthenticationStatus.REGISTERED, currentUser);
    }
    
    private PmrvUser createPmrvUser(String userId, RoleType roleType) {
        return PmrvUser.builder().userId(userId).roleType(roleType).build();
    }

    private OperatorUserInvitationDTO createOperatorUserInvitationDTO(String email, String roleCode) {
        return OperatorUserInvitationDTO.builder()
            .email(email)
            .roleCode(roleCode)
            .build();
    }

    private UserInfoDTO createUserInfoDTO(String userId, AuthenticationStatus authenticationStatus) {
    	UserInfoDTO user = new UserInfoDTO();
    	user.setUserId(userId);
        user.setStatus(authenticationStatus);
        return user;
    }
}