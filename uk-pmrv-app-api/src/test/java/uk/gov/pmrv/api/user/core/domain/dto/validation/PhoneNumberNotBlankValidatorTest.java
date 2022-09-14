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
class PhoneNumberNotBlankValidatorTest {

    @InjectMocks
    private PhoneNumberNotBlankValidator phoneNumberNotBlankValidator;

    @Mock
    private ConstraintValidatorContext context;

    @Test
    void validPhoneNumber() {
        PhoneNumberDTO dto = PhoneNumberDTO.builder().countryCode("30").number("2101313131").build();
        assertTrue(phoneNumberNotBlankValidator.isValid(dto, context));
    }

    @Test
    void phoneNumberAndBothNull() {
        PhoneNumberDTO dto = PhoneNumberDTO.builder().build();
        assertFalse(phoneNumberNotBlankValidator.isValid(dto, context));
    }

    @Test
    void phoneNumberAndBothEmpty() {
        PhoneNumberDTO dto = PhoneNumberDTO.builder().countryCode("").number("").build();
        assertFalse(phoneNumberNotBlankValidator.isValid(dto, context));
    }

    @Test
    void phoneNumberAndBothWhiteSpace() {
        PhoneNumberDTO dto = PhoneNumberDTO.builder().countryCode(" ").number(" ").build();
        assertFalse(phoneNumberNotBlankValidator.isValid(dto, context));
    }

    @Test
    void phoneNumberAndCountryCodeNull() {
        PhoneNumberDTO dto = PhoneNumberDTO.builder().number("2101313131").build();
        assertFalse(phoneNumberNotBlankValidator.isValid(dto, context));
    }

    @Test
    void phoneNumberAndNumberNull() {
        PhoneNumberDTO dto = PhoneNumberDTO.builder().countryCode("30").build();
        assertFalse(phoneNumberNotBlankValidator.isValid(dto, context));
    }

    @Test
    void phoneNumberAndCountryCodeEmpty() {
        PhoneNumberDTO dto = PhoneNumberDTO.builder().countryCode("").number("2101313131").build();
        assertFalse(phoneNumberNotBlankValidator.isValid(dto, context));
    }

    @Test
    void phoneNumberAndNumberEmpty() {
        PhoneNumberDTO dto = PhoneNumberDTO.builder().countryCode("30").number("").build();
        assertFalse(phoneNumberNotBlankValidator.isValid(dto, context));
    }

    @Test
    void phoneNumberAndCountryCodeWhiteSpace() {
        PhoneNumberDTO dto = PhoneNumberDTO.builder().countryCode(" ").number("2101313131").build();
        assertFalse(phoneNumberNotBlankValidator.isValid(dto, context));
    }

    @Test
    void phoneNumberAndNumberWhiteSpace() {
        PhoneNumberDTO dto = PhoneNumberDTO.builder().countryCode("30").number(" ").build();
        assertFalse(phoneNumberNotBlankValidator.isValid(dto, context));
    }
}
