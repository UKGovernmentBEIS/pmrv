package uk.gov.pmrv.api.user.core.domain.dto.validation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.user.core.domain.dto.PhoneNumberDTO;

import javax.validation.ConstraintValidatorContext;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class PhoneNumberIntegrityValidatorTest {

    @InjectMocks
    private PhoneNumberIntegrityValidator phoneNumberIntegrityValidator;

    @Mock
    private ConstraintValidatorContext context;

    @Test
    void validPhoneNumber() {
        PhoneNumberDTO dto = PhoneNumberDTO.builder().countryCode("30").number("2101313131").build();
        assertTrue(phoneNumberIntegrityValidator.isValid(dto, context));
    }

    @Test
    void phoneNumberEmptyCountryCode() {
        PhoneNumberDTO dto = PhoneNumberDTO.builder().countryCode("").number("2101313131").build();
        assertFalse(phoneNumberIntegrityValidator.isValid(dto, context));
    }

    @Test
    void phoneNumberNullCountryCode() {
        PhoneNumberDTO dto = PhoneNumberDTO.builder().number("2101313131").build();
        assertFalse(phoneNumberIntegrityValidator.isValid(dto, context));
    }

    @Test
    void phoneNumberEmptyNumber() {
        PhoneNumberDTO dto = PhoneNumberDTO.builder().countryCode("30").number("").build();
        assertFalse(phoneNumberIntegrityValidator.isValid(dto, context));
    }

    @Test
    void phoneNumberNullNumber() {
        PhoneNumberDTO dto = PhoneNumberDTO.builder().countryCode("30").build();
        assertFalse(phoneNumberIntegrityValidator.isValid(dto, context));
    }

    @Test
    void phoneNumberWhiteSpaceNumber() {
        PhoneNumberDTO dto = PhoneNumberDTO.builder().countryCode("30").number(" ").build();
        assertFalse(phoneNumberIntegrityValidator.isValid(dto, context));
    }
}
