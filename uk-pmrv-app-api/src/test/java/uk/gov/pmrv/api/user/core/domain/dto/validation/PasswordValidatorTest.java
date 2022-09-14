package uk.gov.pmrv.api.user.core.domain.dto.validation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;

import javax.validation.ConstraintValidatorContext;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PasswordValidatorTest {

    @InjectMocks
    private PasswordValidator passwordValidator;

    @Mock
    private PasswordClientService passwordClientService;

    @Mock
    private ConstraintValidatorContext constraintValidatorContext;

    private static final String weakPassword = "password";

    private static final String strongPassword = "redfatbus";

    private static final String strongPasswordHashPrefix = "c7481";

    private static final String strongPasswordResponse = "1AA8423017483440CC271B810DEB524E139:2";

    @Test
    void validPassword() {
        when(passwordClientService.searchPassword(strongPasswordHashPrefix)).thenReturn(strongPasswordResponse);

        assertTrue(passwordValidator.isValid(strongPassword, constraintValidatorContext));
        verify(passwordClientService, times(1)).searchPassword(strongPasswordHashPrefix);
    }

    @Test
    void notValidPwnedPassword() {
        when(passwordClientService.searchPassword(strongPasswordHashPrefix)).thenThrow(new BusinessException(ErrorCode.INTERNAL_SERVER));

        assertThrows(BusinessException.class, () -> passwordValidator.isValid(strongPassword, constraintValidatorContext));
        verify(passwordClientService, times(1)).searchPassword(strongPasswordHashPrefix);
    }

    @Test
    void notValidPassword() {
        assertFalse(passwordValidator.isValid(weakPassword, constraintValidatorContext));
        verify(passwordClientService, never()).searchPassword(anyString());
    }

    @Test
    void nullPassword() {
        assertTrue(passwordValidator.isValid(null, constraintValidatorContext));
        verify(passwordClientService, never()).searchPassword(strongPasswordHashPrefix);
    }

}
