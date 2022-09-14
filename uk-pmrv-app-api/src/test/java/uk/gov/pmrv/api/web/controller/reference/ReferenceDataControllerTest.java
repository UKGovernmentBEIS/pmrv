package uk.gov.pmrv.api.web.controller.reference;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import uk.gov.pmrv.api.referencedata.domain.Country;
import uk.gov.pmrv.api.referencedata.domain.enumeration.ReferenceDataType;
import uk.gov.pmrv.api.referencedata.service.CountryService;
import uk.gov.pmrv.api.web.controller.exception.ExceptionControllerAdvice;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class ReferenceDataControllerTest {

    public static final String REF_DATA_CONTROLLER_PATH = "/v1.0/data";

    @InjectMocks
    private ReferenceDataController referenceDataController;

    @Mock
    private ApplicationContext context;

    @Mock
    private CountryService countryService;

    private MockMvc mockMvc;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(referenceDataController)
                .setControllerAdvice(new ExceptionControllerAdvice())
                .build();
    }

    @Test
    void getReferenceData() throws Exception {
        when(context.getBean(CountryService.class)).thenReturn(countryService);
        
        List<Country> countries = buildCountries("GR", "IT");
        when(countryService.getReferenceData()).thenReturn(countries);

        mockMvc.perform(MockMvcRequestBuilders.get(REF_DATA_CONTROLLER_PATH)
                .param("types", ReferenceDataType.COUNTRIES.name())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("GR" + "_name")))
                .andExpect(content().string(containsString("IT" + "_name")));
    }

    private List<Country> buildCountries(String... countryCodes) {
        return Arrays.stream(countryCodes).map(countryCode -> Country.builder()
                .code(countryCode)
                .name(countryCode + "_name")
                .officialName(countryCode + "_official").build())
                .collect(Collectors.toList());
    }
}
