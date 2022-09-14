package uk.gov.pmrv.api.verificationbody.transform;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import uk.gov.pmrv.api.common.domain.Address;
import uk.gov.pmrv.api.common.domain.dto.AddressDTO;
import uk.gov.pmrv.api.common.domain.enumeration.EmissionTradingScheme;
import uk.gov.pmrv.api.verificationbody.domain.VerificationBody;
import uk.gov.pmrv.api.verificationbody.domain.dto.VerificationBodyEditDTO;

class VerificationBodyMapperTest {

    private VerificationBodyMapper mapper;

    @BeforeEach
    void init() {
        mapper = Mappers.getMapper(VerificationBodyMapper.class);
    }

    @Test
    void toVerificationBody() {
        String name = "name";
        String accreditationRefNum = "accreditationRefNum";
        AddressDTO address = AddressDTO.builder().line1("line1").line2("line2").city("city").country("country").postcode("code").build();
        Set<EmissionTradingScheme> emissionTradingSchemes =
            Set.of(EmissionTradingScheme.UK_ETS_INSTALLATIONS, EmissionTradingScheme.UK_ETS_AVIATION);

        VerificationBodyEditDTO verificationBodyEditDTO = VerificationBodyEditDTO.builder()
            .name(name)
            .accreditationReferenceNumber(accreditationRefNum)
            .address(address)
            .emissionTradingSchemes(emissionTradingSchemes)
            .build();

        //invoke
        VerificationBody verificationBody = mapper.toVerificationBody(verificationBodyEditDTO);

        //assertions
        assertThat(verificationBody).isNotNull();
        assertEquals(name, verificationBody.getName());
        assertEquals(accreditationRefNum, verificationBody.getAccreditationReferenceNumber());

        Address verificationBodyAddress = verificationBody.getAddress();
        assertThat(verificationBodyAddress).isNotNull();
        assertEquals(address.getLine1(), verificationBodyAddress.getLine1());
        assertEquals(address.getLine2(), verificationBodyAddress.getLine2());
        assertEquals(address.getCity(), verificationBodyAddress.getCity());
        assertEquals(address.getCountry(), verificationBodyAddress.getCountry());
        assertEquals(address.getPostcode(), verificationBodyAddress.getPostcode());

        assertThat(verificationBody.getEmissionTradingSchemes())
                .hasSize(2)
                .containsExactlyInAnyOrder(EmissionTradingScheme.UK_ETS_INSTALLATIONS, EmissionTradingScheme.UK_ETS_AVIATION);
    }
}