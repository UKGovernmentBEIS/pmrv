package uk.gov.pmrv.api.workflow.request.flow.common.service.notification;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.pmrv.api.account.domain.dto.CoordinatesDTO;
import uk.gov.pmrv.api.account.domain.dto.LocationOffShoreDTO;
import uk.gov.pmrv.api.account.domain.dto.LocationOnShoreDTO;
import uk.gov.pmrv.api.account.domain.enumeration.CardinalDirection;
import uk.gov.pmrv.api.account.domain.enumeration.LocationType;
import uk.gov.pmrv.api.common.domain.dto.AddressDTO;
import uk.gov.pmrv.api.referencedata.domain.Country;
import uk.gov.pmrv.api.referencedata.service.CountryService;

@ExtendWith(MockitoExtension.class)
class DocumentTemplateLocationInfoResolverTest {

    @InjectMocks
    private DocumentTemplateLocationInfoResolver resolver;

    @Mock
    private CountryService countryService;

    @Test
    void constructLocationInfo_onshore() {
        LocationOnShoreDTO locationOnShore = LocationOnShoreDTO.builder()
                .type(LocationType.ONSHORE)
                .gridReference("gridRef")
                .address(AddressDTO.builder()
                        .line1("line1")
                        .line2("line2")
                        .city("city")
                        .country("GR")
                        .postcode("15125")
                        .build())
                .build();
        
        when(countryService.getReferenceData())
                .thenReturn(List.of(Country.builder().name("Greece").code("GR").build()));
        
        String result = resolver.constructLocationInfo(locationOnShore);
        
        String expectedResult = new StringBuilder()
                .append(locationOnShore.getAddress().getLine1()).append("\n")
                .append(locationOnShore.getAddress().getLine2()).append("\n")
                .append(locationOnShore.getAddress().getCity()).append("\n")
                .append(locationOnShore.getAddress().getPostcode()).append("\n")
                .append("Greece")
                .toString();
        assertThat(result).isEqualTo(expectedResult);
        
        verify(countryService, times(1)).getReferenceData();
    }
    
    @Test
    void constructLocationInfo_offshore() {
        LocationOffShoreDTO locationOffShore = LocationOffShoreDTO.builder()
                .type(LocationType.OFFSHORE)
                .latitude(CoordinatesDTO.builder()
                        .cardinalDirection(CardinalDirection.EAST)
                        .degree(1)
                        .minute(59)
                        .second(33.33d).build())
                .longitude(CoordinatesDTO.builder()
                        .cardinalDirection(CardinalDirection.NORTH)
                        .degree(2)
                        .minute(58)
                        .second(34.34d).build())
                .build();
        
        String result = resolver.constructLocationInfo(locationOffShore);
        assertThat(result).isEqualTo("1 59 33.33 E / 2 58 34.34 N");
        verify(countryService, never()).getReferenceData();
    }
}
