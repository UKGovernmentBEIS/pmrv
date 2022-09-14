package uk.gov.pmrv.api.authorization.rules.services.authorization;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.pmrv.api.authorization.core.domain.Permission;
import uk.gov.pmrv.api.common.domain.enumeration.RoleType;
import uk.gov.pmrv.api.common.domain.model.PmrvUser;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(MockitoExtension.class)
class AccountAuthorizationServiceDelegatorTest {
    private AccountAuthorizationServiceDelegator accountAuthorizationServiceDelegator;
    private OperatorAccountAuthorizationService operatorAccountAuthorizationService;
    private RegulatorAccountAuthorizationService regulatorAccountAuthorizationService;

    @BeforeAll
    void setup() {
        operatorAccountAuthorizationService = Mockito.mock(OperatorAccountAuthorizationService.class);
        regulatorAccountAuthorizationService = Mockito.mock(RegulatorAccountAuthorizationService.class);
        accountAuthorizationServiceDelegator = new AccountAuthorizationServiceDelegator(
                List.of(operatorAccountAuthorizationService, regulatorAccountAuthorizationService));
        when(operatorAccountAuthorizationService.getRoleType()).thenReturn(RoleType.OPERATOR);
        when(regulatorAccountAuthorizationService.getRoleType()).thenReturn(RoleType.REGULATOR);
    }

    private final PmrvUser OPERATOR = PmrvUser.builder().roleType(RoleType.OPERATOR).build();
    private final PmrvUser REGULATOR = PmrvUser.builder().roleType(RoleType.REGULATOR).build();

    @Test
    void isAuthorized_operator_no_permissions() {
        Long accountId = 1L;
        when(operatorAccountAuthorizationService.isAuthorized(OPERATOR, accountId)).thenReturn(true);

        assertTrue(accountAuthorizationServiceDelegator.isAuthorized(OPERATOR, accountId));

        verify(operatorAccountAuthorizationService, times(1)).isAuthorized(OPERATOR, accountId);
    }

    @Test
    void isAuthorized_operator_with_permissions() {
        Long accountId = 1L;
        Permission permission = Permission.PERM_PERMIT_ISSUANCE_APPLICATION_SUBMIT_EXECUTE_TASK;
        when(operatorAccountAuthorizationService.isAuthorized(OPERATOR, accountId, permission)).thenReturn(true);

        assertTrue(accountAuthorizationServiceDelegator.isAuthorized(OPERATOR, accountId, permission));

        verify(operatorAccountAuthorizationService, times(1)).isAuthorized(OPERATOR, accountId, permission);
    }

    @Test
    void isAuthorized_regulator_no_permissions() {
        Long accountId = 1L;
        when(regulatorAccountAuthorizationService.isAuthorized(REGULATOR, accountId)).thenReturn(true);

        assertTrue(accountAuthorizationServiceDelegator.isAuthorized(REGULATOR, accountId));

        verify(regulatorAccountAuthorizationService, times(1)).isAuthorized(REGULATOR, accountId);
    }

    @Test
    void isAuthorized_regulator_with_permissions() {
        Long accountId = 1L;
        Permission permission = Permission.PERM_INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW_EXECUTE_TASK;
        when(regulatorAccountAuthorizationService.isAuthorized(REGULATOR, accountId, permission)).thenReturn(true);

        assertTrue(accountAuthorizationServiceDelegator.isAuthorized(REGULATOR, accountId, permission));

        verify(regulatorAccountAuthorizationService, times(1)).isAuthorized(REGULATOR, accountId, permission);
    }
}