package uk.gov.pmrv.api.account.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.pmrv.api.account.domain.dto.AccountDTO;
import uk.gov.pmrv.api.account.domain.dto.LegalEntityDTO;
import uk.gov.pmrv.api.account.domain.dto.LocationDTO;
import uk.gov.pmrv.api.account.domain.dto.LocationOnShoreDTO;
import uk.gov.pmrv.api.account.domain.enumeration.LocationType;
import uk.gov.pmrv.api.account.domain.installationoperatordetails.InstallationOperatorDetails;
import uk.gov.pmrv.api.common.domain.dto.AddressDTO;
import uk.gov.pmrv.api.account.transform.AccountToInstallationOperatorDetailsMapper;

@ExtendWith(MockitoExtension.class)
class InstallationOperatorDetailsQueryServiceTest {

	@InjectMocks
    private InstallationOperatorDetailsQueryService service;

    @Mock
    private AccountQueryService accountQueryService;
    
    @Test
    void getInstallationOperatorDetails() {
    	Long accountId = 1L;
    	
    	LocationDTO location = LocationOnShoreDTO.builder()
                .gridReference("gridReference")
                .address(AddressDTO.builder()
                        .line1("line1")
                        .city("city")
                        .country("GB")
                        .postcode("postcode")
                        .build())
                .build();
        location.setType(LocationType.ONSHORE);

        AccountDTO account = AccountDTO.builder()
        		.id(accountId)
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
        
        when(accountQueryService.getAccountDTOById(accountId)).thenReturn(account);
        
        InstallationOperatorDetails result = service.getInstallationOperatorDetails(accountId);
        
        assertThat(result).isEqualTo(Mappers
				.getMapper(AccountToInstallationOperatorDetailsMapper.class).toPermitInstallationOperatorDetails(account));
        verify(accountQueryService, times(1)).getAccountDTOById(accountId);
    }
    
}
