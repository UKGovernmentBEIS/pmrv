package uk.gov.pmrv.api.account.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.account.domain.Account;
import uk.gov.pmrv.api.account.domain.LegalEntity;
import uk.gov.pmrv.api.account.domain.LocationOnShore;
import uk.gov.pmrv.api.account.domain.dto.LocationOnShoreDTO;
import uk.gov.pmrv.api.account.domain.enumeration.EmitterType;
import uk.gov.pmrv.api.account.domain.enumeration.InstallationCategory;
import uk.gov.pmrv.api.account.domain.enumeration.LocationType;
import uk.gov.pmrv.api.account.repository.AccountRepository;
import uk.gov.pmrv.api.common.domain.Address;
import uk.gov.pmrv.api.common.domain.dto.AddressDTO;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;

@ExtendWith(MockitoExtension.class)
class AccountUpdateServiceTest {

    @InjectMocks
    private AccountUpdateService service;

    @Mock
    private AccountQueryService accountQueryService;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private AccountStatusService accountStatusService;

    @Mock
    private LegalEntityValidationService legalEntityValidationService;
    
    @Test
    void updateAccountSiteName() {
        Long accountId = 1L; 
        String newSiteName = "newSiteName";
        
        Account account = Account.builder().id(accountId).siteName("siteName").build();
        
        when(accountQueryService.getAccountById(accountId)).thenReturn(account);
        
        service.updateAccountSiteName(accountId, newSiteName);
        
        assertThat(account.getSiteName()).isEqualTo(newSiteName);
        verify(accountQueryService, times(1)).getAccountById(accountId);
        verify(accountRepository, times(1)).save(account);
    }
    
    @Test
    void updateAccountRegistryId() {
        Long accountId = 1L; 
        Integer newRegistryId = 1234568;
        
        Account account = Account.builder().id(accountId).registryId(1234567).build();
        
        when(accountQueryService.getAccountById(accountId)).thenReturn(account);
        
        service.updateAccountRegistryId(accountId, newRegistryId);
        
        assertThat(account.getRegistryId()).isEqualTo(newRegistryId);
        verify(accountQueryService, times(1)).getAccountById(accountId);
        verify(accountRepository, times(1)).save(account);
    }

    @Test
    void updateAccountSopId() {
        Long accountId = 1L;
        Long newSopId = 1234567892L;

        Account account = Account.builder().id(accountId).sopId(1234567891L).build();

        when(accountQueryService.getAccountById(accountId)).thenReturn(account);

        service.updateAccountSopId(accountId, newSopId);

        assertThat(account.getSopId()).isEqualTo(newSopId);
        verify(accountQueryService, times(1)).getAccountById(accountId);
        verify(accountRepository, times(1)).save(account);
    }

    @Test
    void updateAccountAddress() {
        Long accountId = 1L;
        LocationOnShoreDTO address = LocationOnShoreDTO.builder().type(LocationType.ONSHORE).gridReference("grid").address(AddressDTO.builder()
                .city("city").country("country").line1("line1").line2("line2").postcode("postcode").build()).build();
        LocationOnShore location = LocationOnShore.builder()
                .gridReference("grid")
                .address(Address.builder()
                        .city("city")
                        .country("GR")
                        .line1("line")
                        .postcode("postcode")
                        .build())
                .build();
        LocationOnShore newLocation = LocationOnShore.builder()
                .gridReference(address.getGridReference())
                .address(Address.builder()
                        .city(address.getAddress().getCity())
                        .country(address.getAddress().getCountry())
                        .line1(address.getAddress().getLine1())
                        .line2(address.getAddress().getLine2())
                        .postcode(address.getAddress().getPostcode())
                        .build())
                .build();

        Account account = Account.builder().id(accountId).location(location).build();

        when(accountQueryService.getAccountById(accountId)).thenReturn(account);

        service.updateAccountAddress(accountId, address);

        assertThat(account.getLocation()).isEqualTo(newLocation);
        verify(accountRepository, times(1)).save(account);
    }

    @Test
    void updateAccountUponPermitGranted() {
        Long accountId = 1L;
        EmitterType emitterType = EmitterType.GHGE;
        InstallationCategory installationCategory = InstallationCategory.A;

        Account account = Account.builder().id(accountId).build();

        when(accountQueryService.getAccountById(accountId)).thenReturn(account);

        service.updateAccountUponPermitGranted(accountId, emitterType, BigDecimal.valueOf(40000));

        assertEquals(emitterType, account.getEmitterType());
        assertEquals(installationCategory, account.getInstallationCategory());

        verify(accountQueryService, times(1)).getAccountById(accountId);
        verify(accountStatusService, times(1)).handlePermitGranted(accountId);
    }

    @Test
    void updateAccountLegalEntityAddress() {
        Long accountId = 1L;

        var addressDTO = AddressDTO.builder()
            .city("city")
            .country("country")
            .line1("line1")
            .line2("line2")
            .postcode("postcode")
            .build();

        var existingAddress = Address.builder()
            .city("city")
            .country("GR")
            .line1("line")
            .postcode("postcode")
            .build();

        var expectedResult = new LocationOnShore(
null,
            Address.builder()
                .city(addressDTO.getCity())
                .country(addressDTO.getCountry())
                .line1(addressDTO.getLine1())
                .line2(addressDTO.getLine2())
                .postcode(addressDTO.getPostcode())
                .build()
        );

        Account account = Account.builder()
            .id(accountId)
            .legalEntity(
                LegalEntity.builder()
                    .id(2L)
                    .location(new LocationOnShore(null, existingAddress))
                    .build()
            )
            .build();

        when(accountQueryService.getAccountById(accountId)).thenReturn(account);

        service.updateLegalEntityAddress(accountId, addressDTO);

        assertThat(account.getLegalEntity().getLocation()).isEqualTo(expectedResult);
    }

    @Test
    void updateAccountLegalEntityName() {
        final Long accountId = 1L;
        final String newName = "new name";
        final LegalEntity le =  LegalEntity.builder()
            .id(2L)
            .name("old name")
            .build();
        final Account account = Account.builder()
            .id(accountId)
            .legalEntity(le)
            .build();

        when(accountQueryService.getAccountById(accountId)).thenReturn(account);

        service.updateLegalEntityName(accountId, newName);

        assertThat(account.getLegalEntity().getName()).isEqualTo(newName);

        verify(accountQueryService, times(1)).getAccountById(accountId);
        verify(legalEntityValidationService, times(1))
            .validateNameExistenceInOtherActiveLegalEntities(newName, le.getId());
    }

    @Test
    void updateAccountLegalEntityName_name_already_exists() {
        final Long accountId = 1L;
        final String newName = "new name";
        final LegalEntity le =  LegalEntity.builder()
            .id(2L)
            .name("old name")
            .build();
        final Account account = Account.builder()
            .id(accountId)
            .legalEntity(le)
            .build();

        when(accountQueryService.getAccountById(accountId)).thenReturn(account);
        doThrow(new BusinessException(ErrorCode.LEGAL_ENTITY_ALREADY_EXISTS))
            .when(legalEntityValidationService).validateNameExistenceInOtherActiveLegalEntities(newName, le.getId());

        // Invoke
        BusinessException be = assertThrows(BusinessException.class, () -> service.updateLegalEntityName(accountId, newName));

        assertThat(be.getErrorCode()).isEqualTo(ErrorCode.LEGAL_ENTITY_ALREADY_EXISTS);

        verify(accountQueryService, times(1)).getAccountById(accountId);
        verify(legalEntityValidationService, times(1))
            .validateNameExistenceInOtherActiveLegalEntities(newName,le.getId());
    }
}
