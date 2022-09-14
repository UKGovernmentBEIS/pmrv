package uk.gov.pmrv.api.workflow.request.flow.common.service.notification;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import lombok.RequiredArgsConstructor;
import uk.gov.pmrv.api.account.domain.dto.LocationDTO;
import uk.gov.pmrv.api.account.domain.dto.LocationOnShoreDTO;
import uk.gov.pmrv.api.account.domain.enumeration.LocationType;
import uk.gov.pmrv.api.common.domain.dto.AddressDTO;
import uk.gov.pmrv.api.referencedata.domain.Country;
import uk.gov.pmrv.api.referencedata.service.CountryService;

@Service
@RequiredArgsConstructor
public class DocumentTemplateLocationInfoResolver {

    private final CountryService countryService;
    
    public String constructLocationInfo(LocationDTO location) {
        if(location.getType() == LocationType.OFFSHORE) {
            return location.toString();
        } else if (location.getType() == LocationType.ONSHORE) {
            return constructAddressInfo(((LocationOnShoreDTO)location).getAddress());
        } else {
            throw new UnsupportedOperationException("Unsupported type: " + location.getType());
        }
    }
    
    public String constructAddressInfo(AddressDTO address) {
        String countryName = countryService.getReferenceData().stream()
                .filter((country) -> address.getCountry().equals(country.getCode()))
                .map(Country::getName)
                .findFirst().orElse("");
        StringBuilder addressBuilder = new StringBuilder();
        addressBuilder.append(address.getLine1());
        if(StringUtils.hasLength(address.getLine2())) {
            addressBuilder.append("\n" + address.getLine2());
        }
        addressBuilder.append("\n" + address.getCity());
        addressBuilder.append("\n" + address.getPostcode());
        addressBuilder.append("\n" + countryName);
        return addressBuilder.toString();
    }
}
