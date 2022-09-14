package uk.gov.pmrv.api.authorization.rules.services;

import org.junit.jupiter.api.Test;
import uk.gov.pmrv.api.common.domain.enumeration.RoleType;
import uk.gov.pmrv.api.common.domain.model.PmrvAuthority;
import uk.gov.pmrv.api.common.domain.model.PmrvUser;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class RoleAuthorizationServiceTest {

    private final RoleAuthorizationService roleAuthorizationService = new RoleAuthorizationService();

    @Test
    void evaluate() {
        PmrvUser operatorUser = PmrvUser.builder().userId("userId").roleType(RoleType.OPERATOR).build();
        List<PmrvAuthority> authorities = List.of(
            PmrvAuthority.builder().accountId(1L).build()
        );
        operatorUser.setAuthorities(authorities);

        assertDoesNotThrow(() -> roleAuthorizationService.evaluate(operatorUser, new RoleType[] {RoleType.OPERATOR}));
    }

    @Test
    void evaluate_throws_business_exception_if_required_role_type_different() {
        PmrvUser operatorUser = PmrvUser.builder().userId("userId").roleType(RoleType.OPERATOR).build();

        BusinessException businessException = assertThrows(BusinessException.class,
            () -> roleAuthorizationService.evaluate(operatorUser, new RoleType[] {RoleType.REGULATOR}));

        assertEquals(ErrorCode.FORBIDDEN, businessException.getErrorCode());
    }

    @Test
    void evaluate_throws_business_exception_if_user_has_no_authorities() {
        PmrvUser operatorUser = PmrvUser.builder().userId("userId").roleType(RoleType.REGULATOR).build();

        BusinessException businessException = assertThrows(BusinessException.class,
            () -> roleAuthorizationService.evaluate(operatorUser, new RoleType[] {RoleType.REGULATOR}));

        assertEquals(ErrorCode.FORBIDDEN, businessException.getErrorCode());
    }

    @Test
    void evaluate_throws_business_exception_if_permitted_role_types_empty() {
        PmrvUser operatorUser = PmrvUser.builder().userId("userId").roleType(RoleType.REGULATOR).build();

        BusinessException businessException = assertThrows(BusinessException.class,
            () -> roleAuthorizationService.evaluate(operatorUser, new RoleType[] {}));

        assertEquals(ErrorCode.FORBIDDEN, businessException.getErrorCode());
    }
}