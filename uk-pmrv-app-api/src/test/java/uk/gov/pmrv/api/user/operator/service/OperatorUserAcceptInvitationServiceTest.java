package uk.gov.pmrv.api.user.operator.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.account.service.AccountQueryService;
import uk.gov.pmrv.api.authorization.core.domain.dto.AuthorityInfoDTO;
import uk.gov.pmrv.api.authorization.core.service.UserRoleTypeService;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.user.core.domain.enumeration.UserInvitationStatus;
import uk.gov.pmrv.api.user.operator.domain.OperatorUserAcceptInvitationDTO;
import uk.gov.pmrv.api.user.operator.domain.OperatorUserDTO;
import uk.gov.pmrv.api.user.operator.transform.OperatorUserAcceptInvitationMapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OperatorUserAcceptInvitationServiceTest {

    @InjectMocks
    private OperatorUserAcceptInvitationService operatorUserAcceptInvitationService;

    @Mock
    private OperatorUserAuthService operatorUserAuthService;

    @Mock
    private UserRoleTypeService userRoleTypeService;

    @Mock
    private OperatorUserTokenVerificationService operatorUserTokenVerificationService;

    @Mock
    private OperatorUserAcceptInvitationMapper operatorUserAcceptInvitationMapper;

    @Mock
    private AccountQueryService accountQueryService;

    @Mock
    private OperatorRoleCodeAcceptInvitationServiceDelegator operatorRoleCodeAcceptInvitationServiceDelegator;

    @Test
    void acceptInvitation() {
        String invitationToken = "token";
        String userId = "userId";
        Long accountId = 1L;
        String authorityRoleCode = "roleCode";
        String accountInstallationName = "accountInstallationName";
        AuthorityInfoDTO authorityInfo = AuthorityInfoDTO.builder().userId(userId).accountId(accountId).code(authorityRoleCode).build();
        OperatorUserDTO operatorUser = OperatorUserDTO.builder().build();
        OperatorUserAcceptInvitationDTO operatorUserAcceptInvitation = OperatorUserAcceptInvitationDTO.builder().build();
        UserInvitationStatus userInvitationStatus = UserInvitationStatus.ACCEPTED;


        when(operatorUserTokenVerificationService.verifyInvitationTokenForPendingAuthority(invitationToken)).thenReturn(authorityInfo);
        when(userRoleTypeService.isUserOperator(authorityInfo.getUserId())).thenReturn(true);
        when(operatorUserAuthService.getOperatorUserById(authorityInfo.getUserId())).thenReturn(operatorUser);
        when(accountQueryService.getAccountInstallationName(authorityInfo.getAccountId())).thenReturn(accountInstallationName);
        when(operatorUserAcceptInvitationMapper.toOperatorUserAcceptInvitationDTO(operatorUser, authorityInfo, accountInstallationName))
            .thenReturn(operatorUserAcceptInvitation);
        when(operatorRoleCodeAcceptInvitationServiceDelegator.acceptInvitation(operatorUserAcceptInvitation, authorityInfo.getCode()))
            .thenReturn(userInvitationStatus);

        operatorUserAcceptInvitationService.acceptInvitation(invitationToken);

        verify(operatorUserTokenVerificationService, times(1))
            .verifyInvitationTokenForPendingAuthority(invitationToken);
        verify(userRoleTypeService, times(1)).isUserOperator(userId);
        verify(operatorUserAuthService, times(1)).getOperatorUserById(userId);
        verify(accountQueryService, times(1)).getAccountInstallationName(accountId);
        verify(operatorUserAcceptInvitationMapper, times(1)).
            toOperatorUserAcceptInvitationDTO(operatorUser, authorityInfo, accountInstallationName);
        verify(operatorRoleCodeAcceptInvitationServiceDelegator, times(1))
            .acceptInvitation(operatorUserAcceptInvitation, authorityRoleCode);
        verify(operatorUserAcceptInvitationMapper, times(1))
            .toOperatorInvitedUserInfoDTO(operatorUserAcceptInvitation, authorityRoleCode, userInvitationStatus);
    }

    @Test
    void acceptInvitation_no_operator_user() {
        String invitationToken = "token";
        String userId = "userId";
        Long accountId = 1L;
        String authorityRoleCode = "roleCode";
        AuthorityInfoDTO authorityInfo = AuthorityInfoDTO.builder().userId(userId).accountId(accountId).code(authorityRoleCode).build();

        when(operatorUserTokenVerificationService.verifyInvitationTokenForPendingAuthority(invitationToken)).thenReturn(authorityInfo);
        when(userRoleTypeService.isUserOperator(authorityInfo.getUserId())).thenReturn(false);

        BusinessException businessException = assertThrows(BusinessException.class,
            () -> operatorUserAcceptInvitationService.acceptInvitation(invitationToken));

        assertEquals(ErrorCode.AUTHORITY_USER_IS_NOT_OPERATOR, businessException.getErrorCode());

        verify(operatorUserTokenVerificationService, times(1))
            .verifyInvitationTokenForPendingAuthority(invitationToken);
        verify(userRoleTypeService, times(1)).isUserOperator(userId);
        verifyNoInteractions(operatorUserAuthService,
            operatorUserAcceptInvitationMapper,
            accountQueryService,
            operatorRoleCodeAcceptInvitationServiceDelegator);
    }
}