package uk.gov.pmrv.api.referencedata.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.pmrv.api.referencedata.domain.Country;
import uk.gov.pmrv.api.referencedata.repository.CountryRepository;

@ExtendWith(MockitoExtension.class)
class CountryServiceTest {

    @InjectMocks
    private CountryService countryService;

    @Mock
    private CountryRepository countryRepository;

    @Test
    void getReferenceData() {
        when(countryRepository.findAll()).thenReturn(buildCountries("GR", "IT"));

        List<Country> countries = countryService.getReferenceData();

        assertThat(countries)
                .hasSize(2)
                .extracting(Country::getCode)
                .containsOnly("GR", "IT");
        verify(countryRepository, times(1)).findAll();
    }

    private List<Country> buildCountries(String... countryCodes) {
        return Arrays.stream(countryCodes).map(countryCode -> Country.builder()
                .code(countryCode)
                .name(countryCode + "_name")
                .officialName(countryCode + "_official").build())
                .collect(Collectors.toList());
    }
}
