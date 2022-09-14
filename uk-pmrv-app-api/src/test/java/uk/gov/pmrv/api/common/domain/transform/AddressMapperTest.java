package uk.gov.pmrv.api.common.domain.transform;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import uk.gov.pmrv.api.common.domain.Address;
import uk.gov.pmrv.api.common.domain.dto.AddressDTO;

class AddressMapperTest {
    
    private AddressMapper mapper;

    @BeforeEach
    void init() {
        mapper = Mappers.getMapper(AddressMapper.class);
    }
    
    @Test
    void toAddress() {
        AddressDTO addressDto = AddressDTO.builder().line1("line1").line2("line2").city("city").country("country").postcode("postcode").build();
        
        Address result = mapper.toAddress(addressDto);
        
        assertThat(result.getLine1()).isEqualTo("line1");
        assertThat(result.getLine2()).isEqualTo("line2");
        assertThat(result.getCity()).isEqualTo("city");
        assertThat(result.getCountry()).isEqualTo("country");
        assertThat(result.getPostcode()).isEqualTo("postcode");
    }
}
