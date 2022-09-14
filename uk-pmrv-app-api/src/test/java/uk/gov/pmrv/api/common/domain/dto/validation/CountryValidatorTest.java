package uk.gov.pmrv.api.common.domain.dto.validation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.referencedata.domain.Country;
import uk.gov.pmrv.api.referencedata.service.CountryService;

import javax.validation.ConstraintValidatorContext;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CountryValidatorTest {

    @InjectMocks
    private CountryValidator countryValidator;

    @Mock
    private CountryService countryService;

    @Test
    void testInvalidCountry() {
        List<Country> countries = buildCountries("GR", "IT");
        when(countryService.getReferenceData()).thenReturn(countries);

        assertFalse(countryValidator.isValid("INVALID_COUNTRY_CODE", Mockito.mock(ConstraintValidatorContext.class)));
    }

    @Test
    void testValidCountry() {
        List<Country> countries = buildCountries("GR", "IT");
        when(countryService.getReferenceData()).thenReturn(countries);

        assertTrue(countryValidator.isValid("GR", Mockito.mock(ConstraintValidatorContext.class)));
    }

    private List<Country> buildCountries(String... countryCodes) {
        return Arrays.stream(countryCodes).map(countryCode -> Country.builder()
                .code(countryCode)
                .name(countryCode + "_name")
                .officialName(countryCode + "_official").build())
                .collect(Collectors.toList());
    }
}
