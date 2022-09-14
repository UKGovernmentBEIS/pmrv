package uk.gov.pmrv.api.authorization.rules.services.authorization;

import org.junit.jupiter.api.Test;

import uk.gov.pmrv.api.authorization.core.domain.Permission;
import uk.gov.pmrv.api.common.domain.enumeration.RoleType;
import uk.gov.pmrv.api.common.domain.model.PmrvAuthority;
import uk.gov.pmrv.api.common.domain.model.PmrvUser;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class OperatorAccountAuthorizationServiceTest {
    private final OperatorAccountAuthorizationService operatorAccountAuthorizationService = new OperatorAccountAuthorizationService();
    private final PmrvAuthority pmrvAuthority = PmrvAuthority.builder()
            .accountId(1L)
            .permissions(List.of(Permission.PERM_PERMIT_ISSUANCE_APPLICATION_SUBMIT_EXECUTE_TASK,
                    Permission.PERM_INSTALLATION_ACCOUNT_OPENING_ARCHIVE_EXECUTE_TASK))
            .build();
    private final PmrvUser user = PmrvUser.builder().authorities(List.of(pmrvAuthority)).roleType(RoleType.OPERATOR).build();


    @Test
    void isAuthorized_account_true() {
        assertTrue(operatorAccountAuthorizationService.isAuthorized(user, 1L));
    }

    @Test
    void isAuthorized_account_false() {
        assertFalse(operatorAccountAuthorizationService.isAuthorized(user, 2L));
    }

    @Test
    void isAuthorized_account_with_permissions_true() {
        assertTrue(operatorAccountAuthorizationService.isAuthorized(user, 1L, Permission.PERM_PERMIT_ISSUANCE_APPLICATION_SUBMIT_EXECUTE_TASK));
    }

    @Test
    void isAuthorized_account_with_permissions_false() {
        assertFalse(operatorAccountAuthorizationService.isAuthorized(user, 1L, Permission.PERM_PERMIT_ISSUANCE_APPLICATION_SUBMIT_VIEW_TASK));
    }

    @Test
    void getType() {
        assertEquals(RoleType.OPERATOR, operatorAccountAuthorizationService.getRoleType());
    }
}