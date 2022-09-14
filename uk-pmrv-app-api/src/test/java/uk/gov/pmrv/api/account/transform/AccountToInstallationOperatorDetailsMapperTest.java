package uk.gov.pmrv.api.account.transform;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import uk.gov.pmrv.api.account.domain.dto.AccountDTO;
import uk.gov.pmrv.api.account.domain.dto.CoordinatesDTO;
import uk.gov.pmrv.api.account.domain.dto.LegalEntityDTO;
import uk.gov.pmrv.api.account.domain.dto.LocationDTO;
import uk.gov.pmrv.api.account.domain.dto.LocationOffShoreDTO;
import uk.gov.pmrv.api.account.domain.dto.LocationOnShoreDTO;
import uk.gov.pmrv.api.account.domain.enumeration.CardinalDirection;
import uk.gov.pmrv.api.account.domain.enumeration.LocationType;
import uk.gov.pmrv.api.account.domain.installationoperatordetails.InstallationOperatorDetails;
import uk.gov.pmrv.api.common.domain.dto.AddressDTO;

class AccountToInstallationOperatorDetailsMapperTest {

	private final AccountToInstallationOperatorDetailsMapper mapper = Mappers.getMapper(AccountToInstallationOperatorDetailsMapper.class);
	
	@Test
	void toPermitInstallationOperatorDetails_onshore() {
		LocationDTO location = LocationOnShoreDTO.builder()
				.type(LocationType.ONSHORE)
                .gridReference("gridReference")
                .address(AddressDTO.builder()
                        .line1("line1")
                        .city("city")
                        .country("GB")
                        .postcode("postcode")
                        .build())
                .build();

        AccountDTO account = AccountDTO.builder()
        		.id(1L)
                .name("Account name")
                .location(location)
                .legalEntity(LegalEntityDTO.builder()
                        .name("le")
                        .address(AddressDTO.builder()
                                .line1("line1")
                                .city("city")
                                .country("GB")
                                .postcode("postcode")
                                .build())
                        .build())
                .commencementDate(LocalDate.now())
                .build();
        
        InstallationOperatorDetails result = mapper.toPermitInstallationOperatorDetails(account);
        
        assertThat(result.getInstallationLocation()).isEqualTo(account.getLocation());
        assertThat(result.getInstallationName()).isEqualTo(account.getName());
        assertThat(result.getOperator()).isEqualTo(account.getLegalEntity().getName());
        assertThat(result.getOperatorDetailsAddress().getLine1()).isEqualTo(account.getLegalEntity().getAddress().getLine1());
        assertThat(result.getOperatorDetailsAddress().getLine2()).isEqualTo(account.getLegalEntity().getAddress().getLine2());
        assertThat(result.getOperatorDetailsAddress().getCity()).isEqualTo(account.getLegalEntity().getAddress().getCity());
        assertThat(result.getOperatorDetailsAddress().getPostcode()).isEqualTo(account.getLegalEntity().getAddress().getPostcode());
	}
	
	@Test
	void toPermitInstallationOperatorDetails_offshore() {
		LocationDTO location = LocationOffShoreDTO.builder()
				.type(LocationType.OFFSHORE)
				.latitude(CoordinatesDTO.builder().cardinalDirection(CardinalDirection.EAST).build())
				.longitude(CoordinatesDTO.builder().cardinalDirection(CardinalDirection.NORTH).build())
                .build();

        AccountDTO account = AccountDTO.builder()
        		.id(1L)
                .name("Account name")
                .location(location)
                .legalEntity(LegalEntityDTO.builder()
                        .name("le")
                        .address(AddressDTO.builder()
                                .line1("line1")
                                .city("city")
                                .country("GB")
                                .postcode("postcode")
                                .build())
                        .build())
                .commencementDate(LocalDate.now())
                .build();
        
        InstallationOperatorDetails result = mapper.toPermitInstallationOperatorDetails(account);
        
        assertThat(result.getInstallationLocation()).isEqualTo(account.getLocation());
        assertThat(result.getInstallationName()).isEqualTo(account.getName());
        assertThat(result.getOperator()).isEqualTo(account.getLegalEntity().getName());
        assertThat(result.getOperatorDetailsAddress().getLine1()).isEqualTo(account.getLegalEntity().getAddress().getLine1());
        assertThat(result.getOperatorDetailsAddress().getLine2()).isEqualTo(account.getLegalEntity().getAddress().getLine2());
        assertThat(result.getOperatorDetailsAddress().getCity()).isEqualTo(account.getLegalEntity().getAddress().getCity());
        assertThat(result.getOperatorDetailsAddress().getPostcode()).isEqualTo(account.getLegalEntity().getAddress().getPostcode());
	}
}
