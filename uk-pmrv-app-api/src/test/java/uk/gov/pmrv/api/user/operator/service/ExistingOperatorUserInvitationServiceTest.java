package uk.gov.pmrv.api.user.operator.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.account.service.AccountQueryService;
import uk.gov.pmrv.api.authorization.core.service.UserRoleTypeService;
import uk.gov.pmrv.api.authorization.operator.service.OperatorAuthorityService;
import uk.gov.pmrv.api.common.domain.model.PmrvUser;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.user.core.domain.enumeration.AuthenticationStatus;
import uk.gov.pmrv.api.user.operator.domain.OperatorUserInvitationDTO;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ExistingOperatorUserInvitationServiceTest {

    @InjectMocks
    private ExistingOperatorUserInvitationService existingOperatorUserInvitationService;

    @Mock
    private UserRoleTypeService userRoleTypeService;

    @Mock
    private OperatorAuthorityService operatorAuthorityService;
    
    @Mock
    private AccountQueryService accountQueryService;;

    @Mock
    private OperatorUserNotificationGateway operatorUserNotificationGateway;

    @Mock
    private OperatorUserAuthService operatorUserAuthService;

    @Test
    void addExistingOperatorUserToAccountWhenUserInStatusRegistered() {
        String email = "email";
        String roleCode = "roleCode";
        String userId = "userId";
        Long accountId = 1L;
        String accountName = "accountName";
        String authorityUuid = "authorityUuid";
        PmrvUser currentUser = PmrvUser.builder().userId("current_user_id").build();
        OperatorUserInvitationDTO operatorUserInvitationDTO = createOperatorUserInvitationDTO(email, roleCode);

        when(userRoleTypeService.isUserOperator(userId)).thenReturn(true);
        when(operatorAuthorityService.createPendingAuthorityForOperator(accountId, roleCode, userId, currentUser))
            .thenReturn(authorityUuid);
        when(accountQueryService.getAccountInstallationName(accountId))
            .thenReturn(accountName);

        existingOperatorUserInvitationService
            .addExistingUserToAccount(operatorUserInvitationDTO, accountId, userId, AuthenticationStatus.REGISTERED, currentUser);

        verify(userRoleTypeService, times(1)).isUserOperator(userId);
        verify(operatorAuthorityService, times(1)).createPendingAuthorityForOperator(accountId, roleCode, userId, currentUser);
        verify(accountQueryService, times(1)).getAccountInstallationName(accountId);
        verify(operatorUserNotificationGateway, times(1)).notifyInvitedUser(operatorUserInvitationDTO, accountName, authorityUuid);
        verify(operatorUserAuthService, never()).updateOperatorUserToPending(anyString(), any());
    }

    @Test
    void addExistingOperatorUserToAccountThrowsExceptionWhenUserInStatusRegisteredIsNotOperator() {
        String email = "email";
        String roleCode = "roleCode";
        String userId = "userId";
        Long accountId = 1L;
        PmrvUser currentUser = PmrvUser.builder().userId("current_user_id").build();
        OperatorUserInvitationDTO operatorUserInvitationDTO = createOperatorUserInvitationDTO(email, roleCode);

        when(userRoleTypeService.isUserOperator(userId)).thenReturn(false);

        BusinessException businessException = assertThrows(BusinessException.class, () -> existingOperatorUserInvitationService
            .addExistingUserToAccount(operatorUserInvitationDTO, accountId, userId, AuthenticationStatus.REGISTERED, currentUser));

        assertEquals(ErrorCode.USER_ALREADY_REGISTERED, businessException.getErrorCode());

        verify(userRoleTypeService, times(1)).isUserOperator(userId);
        verify(operatorAuthorityService, never()).createPendingAuthorityForOperator(anyLong(), anyString(), anyString(), any());
        verify(operatorUserNotificationGateway, never()).notifyInvitedUser(Mockito.any(), anyString(), anyString());
        verify(operatorUserAuthService, never()).updateOperatorUserToPending(anyString(), any());
    }

    @Test
    void addExistingOperatorUserToAccountWhenUserInStatusPending() {
        String email = "email";
        String roleCode = "roleCode";
        String userId = "userId";
        Long accountId = 1L;
        String accountName = "accountName";
        String authorityUuid = "authUuid";
        PmrvUser currentUser = PmrvUser.builder().userId("current_user_id").build();
        OperatorUserInvitationDTO operatorUserInvitationDTO = createOperatorUserInvitationDTO(email, roleCode);

        when(operatorAuthorityService.createPendingAuthorityForOperator(accountId, roleCode, userId, currentUser))
            .thenReturn(authorityUuid);
        when(accountQueryService.getAccountInstallationName(accountId))
            .thenReturn(accountName);
        
        existingOperatorUserInvitationService
            .addExistingUserToAccount(operatorUserInvitationDTO, accountId, userId, AuthenticationStatus.PENDING, currentUser);

        verify(userRoleTypeService, never()).isUserOperator(anyString());
        verify(operatorUserAuthService, times(1)).updateOperatorUserToPending(userId, operatorUserInvitationDTO);
        verify(operatorAuthorityService, times(1)).createPendingAuthorityForOperator(accountId, roleCode, userId, currentUser);
        verify(accountQueryService, times(1)).getAccountInstallationName(accountId);
        verify(operatorUserNotificationGateway, times(1)).notifyInvitedUser(operatorUserInvitationDTO, accountName, authorityUuid);
    }

    @Test
    void addExistingOperatorUserToAccountWhenUserInStatusDeleted() {
        String email = "email";
        String roleCode = "roleCode";
        String userId = "userId";
        Long accountId = 1L;
        String accountName = "accountName";
        String authorityUuid = "authUuid";
        PmrvUser currentUser = PmrvUser.builder().userId("current_user_id").build();
        OperatorUserInvitationDTO operatorUserInvitationDTO = createOperatorUserInvitationDTO(email, roleCode);

        when(operatorAuthorityService.createPendingAuthorityForOperator(accountId, roleCode, userId, currentUser))
            .thenReturn(authorityUuid);
        when(accountQueryService.getAccountInstallationName(accountId))
            .thenReturn(accountName);

        existingOperatorUserInvitationService
            .addExistingUserToAccount(operatorUserInvitationDTO, accountId, userId, AuthenticationStatus.DELETED, currentUser);

        verify(userRoleTypeService, never()).isUserOperator(anyString());
        verify(operatorUserAuthService, times(1)).updateOperatorUserToPending(userId, operatorUserInvitationDTO);
        verify(operatorAuthorityService, times(1)).createPendingAuthorityForOperator(accountId, roleCode, userId, currentUser);
        verify(accountQueryService, times(1)).getAccountInstallationName(accountId);
        verify(operatorUserNotificationGateway, times(1)).notifyInvitedUser(operatorUserInvitationDTO, accountName, authorityUuid);
    }

    private OperatorUserInvitationDTO createOperatorUserInvitationDTO(String email, String roleCode) {
        return OperatorUserInvitationDTO.builder()
            .email(email)
            .roleCode(roleCode)
            .build();
    }

}