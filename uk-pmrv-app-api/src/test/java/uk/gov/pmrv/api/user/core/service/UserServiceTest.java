package uk.gov.pmrv.api.user.core.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.authorization.core.domain.dto.UserRoleTypeDTO;
import uk.gov.pmrv.api.authorization.core.service.UserRoleTypeService;
import uk.gov.pmrv.api.common.domain.enumeration.RoleType;
import uk.gov.pmrv.api.user.application.UserService;
import uk.gov.pmrv.api.user.operator.service.OperatorUserAuthService;
import uk.gov.pmrv.api.user.regulator.service.RegulatorUserAuthService;
import uk.gov.pmrv.api.user.verifier.service.VerifierUserAuthService;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRoleTypeService userRoleTypeService;
    
    @Mock
    private OperatorUserAuthService operatorUserAuthService;
    
    @Mock
    private RegulatorUserAuthService regulatorUserAuthService;

    @Mock
    private VerifierUserAuthService verifierUserAuthService;
    
    @Test
    void getUserById_Operator() {
        final String userId = "userId";
        UserRoleTypeDTO userRoleTypeDTO = UserRoleTypeDTO.builder().userId(userId).roleType(RoleType.OPERATOR).build();

        when(userRoleTypeService.getUserRoleTypeByUserId(userId)).thenReturn(userRoleTypeDTO);

        // Invoke
        userService.getUserById(userId);

        // Assert
        verify(userRoleTypeService, times(1)).getUserRoleTypeByUserId(userId);
        verify(operatorUserAuthService, times(1)).getOperatorUserById(userId);
        verify(regulatorUserAuthService, never()).getRegulatorUserById(Mockito.anyString());
        verify(verifierUserAuthService, never()).getVerifierUserById(Mockito.anyString());
    }

    @Test
    void getUserById_Regulator() {
        final String userId = "userId";
        UserRoleTypeDTO userRoleTypeDTO = UserRoleTypeDTO.builder().userId(userId).roleType(RoleType.REGULATOR).build();

        when(userRoleTypeService.getUserRoleTypeByUserId(userId)).thenReturn(userRoleTypeDTO);

        // Invoke
        userService.getUserById(userId);

        // Assert
        verify(userRoleTypeService, times(1)).getUserRoleTypeByUserId(userId);
        verify(operatorUserAuthService, never()).getOperatorUserById(Mockito.anyString());
        verify(regulatorUserAuthService, times(1)).getRegulatorUserById(userId);
        verify(verifierUserAuthService, never()).getVerifierUserById(Mockito.anyString());
    }

    @Test
    void getUserById_Verifier() {
        final String userId = "userId";
        UserRoleTypeDTO userRoleTypeDTO = UserRoleTypeDTO.builder().userId(userId).roleType(RoleType.VERIFIER).build();

        when(userRoleTypeService.getUserRoleTypeByUserId(userId)).thenReturn(userRoleTypeDTO);

        // Invoke
        userService.getUserById(userId);

        // Assert
        verify(userRoleTypeService, times(1)).getUserRoleTypeByUserId(userId);
        verify(operatorUserAuthService, never()).getOperatorUserById(Mockito.anyString());
        verify(regulatorUserAuthService, never()).getRegulatorUserById(Mockito.anyString());
        verify(verifierUserAuthService, times(1)).getVerifierUserById(userId);
    }
}