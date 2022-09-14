package uk.gov.pmrv.api.user.core.service;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;
import static uk.gov.pmrv.api.common.domain.enumeration.RoleType.OPERATOR;

import java.util.Set;
import javax.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import uk.gov.pmrv.api.authorization.core.service.RoleCodeValidator;
import uk.gov.pmrv.api.authorization.core.service.RoleService;
import uk.gov.pmrv.api.common.domain.enumeration.RoleType;

@ExtendWith(MockitoExtension.class)
class RoleCodeValidatorTest {

    private static final RoleType OPERATOR_ROLE_TYPE = OPERATOR;

    private static final String OPERATOR_ADMIN = "operator_admin";
    private static final String OPERATOR_USER = "operator_user";
    private static final String AGENT = "agent";
    private static final Set<String> OPERATOR_ROLE_CODES = Set.of(OPERATOR_ADMIN, OPERATOR_USER, AGENT);

    @InjectMocks
    private RoleCodeValidator roleCodeValidator;

    @Mock
    private RoleService roleService;

    @Mock
    private ConstraintValidatorContext constraintValidatorContext;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(roleCodeValidator, "roleType", OPERATOR_ROLE_TYPE);
    }

    @Test
    void isValid() {
        when(roleService.getCodesByType(OPERATOR)).thenReturn(OPERATOR_ROLE_CODES);
        assertTrue(roleCodeValidator.isValid(OPERATOR_ADMIN, constraintValidatorContext));
    }

    @Test
    void isValidFalse() {
        final String regulator = "regulator";
        when(roleService.getCodesByType(OPERATOR)).thenReturn(OPERATOR_ROLE_CODES);
        assertFalse(roleCodeValidator.isValid(regulator, constraintValidatorContext));
    }
}