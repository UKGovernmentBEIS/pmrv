package uk.gov.pmrv.api.account.domain.dto.validation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.validation.ConstraintValidatorContext;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class GridReferenceValidatorTest {

    @InjectMocks
    private GridReferenceValidator gridReferenceValidator;

    @Mock
    private ConstraintValidatorContext context;

    @Test
    void testValidGridReference() {
        assertTrue(gridReferenceValidator.isValid("SJ43957595", context));
    }

    @Test
    void test_min_digits() {
        assertFalse(gridReferenceValidator.isValid("SJ439", context));
    }

    @Test
    void test_max_digits() {
        assertFalse(gridReferenceValidator.isValid("SJ43957595555", context));
    }

    @Test
    void test_max_letters() {
        assertFalse(gridReferenceValidator.isValid("SJJ43957595", context));
    }

    @Test
    void test_whitespaces() {
        assertTrue(gridReferenceValidator.isValid("SP 8207 1383", context));
    }

    @Test
    void test_special_chars() {
        assertFalse(gridReferenceValidator.isValid("AA1234565666+==**&&====**********", context));
    }
}
