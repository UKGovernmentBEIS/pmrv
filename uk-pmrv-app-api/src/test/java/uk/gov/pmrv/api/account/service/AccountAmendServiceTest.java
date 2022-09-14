package uk.gov.pmrv.api.account.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.account.domain.Account;
import uk.gov.pmrv.api.account.domain.LegalEntity;
import uk.gov.pmrv.api.account.domain.LocationOnShore;
import uk.gov.pmrv.api.account.domain.dto.AccountDTO;
import uk.gov.pmrv.api.account.domain.dto.LegalEntityDTO;
import uk.gov.pmrv.api.account.domain.dto.LocationDTO;
import uk.gov.pmrv.api.account.domain.dto.LocationOnShoreDTO;
import uk.gov.pmrv.api.account.domain.enumeration.AccountStatus;
import uk.gov.pmrv.api.account.domain.enumeration.LocationType;
import uk.gov.pmrv.api.account.repository.AccountRepository;
import uk.gov.pmrv.api.account.transform.AccountMapper;
import uk.gov.pmrv.api.common.domain.enumeration.CompetentAuthority;
import uk.gov.pmrv.api.common.domain.model.PmrvUser;
import uk.gov.pmrv.api.common.exception.BusinessException;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static uk.gov.pmrv.api.common.exception.ErrorCode.ACCOUNT_ALREADY_EXISTS;
import static uk.gov.pmrv.api.common.exception.ErrorCode.ACCOUNT_FIELD_NOT_AMENDABLE;

@ExtendWith(MockitoExtension.class)
class AccountAmendServiceTest {

    @InjectMocks
    private AccountAmendService service;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private LegalEntityService legalEntityService;

    @Mock
    private AccountMapper accountMapper;

    @Mock
	private AccountQueryService accountQueryService;

	@Test
	void amendAccount_non_amendable_fields_changed_should_throw_error() {
	    Long accountId = 1L;
		PmrvUser pmrvUser = PmrvUser.builder().build();
		LocationDTO location = LocationOnShoreDTO.builder().type(LocationType.ONSHORE).build();
		
		AccountDTO previousAccountDTO = AccountDTO.builder()
                .name("account")
                .commencementDate(LocalDate.now())
                .competentAuthority(CompetentAuthority.ENGLAND)
                .location(location)
                .legalEntity(LegalEntityDTO.builder().id(1L).build())
                .build();
		
		AccountDTO newAccountDTO = AccountDTO.builder()
                .name("account")
                .commencementDate(LocalDate.now().minusDays(1))
                .competentAuthority(CompetentAuthority.ENGLAND)
                .location(location)
                .legalEntity(LegalEntityDTO.builder().id(1L).build())
                .build();

		//invoke
		BusinessException businessException = assertThrows(BusinessException.class, () ->
			service.amendAccount(accountId, previousAccountDTO, newAccountDTO, pmrvUser));

		assertThat(businessException.getErrorCode()).isEqualTo(ACCOUNT_FIELD_NOT_AMENDABLE);

		//verify
		verifyNoInteractions(accountQueryService, accountRepository, legalEntityService, accountMapper);
	}
	
	@Test
    void amendAccount_invalid_account_name() {
	    Long accountId = 1L;
        PmrvUser pmrvUser = PmrvUser.builder().build();
        LocationDTO location = LocationOnShoreDTO.builder().type(LocationType.ONSHORE).build();
        LocalDate now = LocalDate.now();
        
        AccountDTO previousAccountDTO = AccountDTO.builder()
                .name("account1")
                .commencementDate(now)
                .competentAuthority(CompetentAuthority.ENGLAND)
                .location(location)
                .legalEntity(LegalEntityDTO.builder().id(1L).build())
                .build();
        
        AccountDTO newAccountDTO = AccountDTO.builder()
                .name("account2")
                .commencementDate(now)
                .competentAuthority(CompetentAuthority.ENGLAND)
                .location(location)
                .legalEntity(LegalEntityDTO.builder().id(1L).build())
                .build();
        
        doThrow(new BusinessException((ACCOUNT_ALREADY_EXISTS))).when(accountQueryService)
            .validateAccountName(newAccountDTO.getName());

        //invoke
        BusinessException businessException = assertThrows(BusinessException.class, () ->
        service.amendAccount(accountId, previousAccountDTO, newAccountDTO, pmrvUser));

        assertThat(businessException.getErrorCode()).isEqualTo(ACCOUNT_ALREADY_EXISTS);
    
        //verify
        verify(accountQueryService, times(1)).validateAccountName(newAccountDTO.getName());
        verifyNoInteractions(accountRepository, legalEntityService, accountMapper);
    }
	
	@Test
    void amendAccount() {
        Long accountId = 1L;
        PmrvUser pmrvUser = PmrvUser.builder().build();
        LocationDTO locationDTO = LocationOnShoreDTO.builder().type(LocationType.ONSHORE).build();
        
        LocationOnShore location = new LocationOnShore();
        location.setId(1L);
        
        LocalDate now = LocalDate.now();
        
        LegalEntity legalEntity = LegalEntity.builder().id(1L).name("le1").build();
        Account account = Account.builder()
                .id(accountId)
                .name("account")
                .legalEntity(legalEntity)
                .status(AccountStatus.LIVE)
                .location(location)
                .emitterId("EM00001")
                .build();
        
        AccountDTO previousAccountDTO = AccountDTO.builder()
                .name("account")
                .commencementDate(now)
                .competentAuthority(CompetentAuthority.ENGLAND)
                .location(locationDTO)
                .legalEntity(LegalEntityDTO.builder().id(1L).name("le1").build())
                .build();
        
        AccountDTO newAccountDTO = AccountDTO.builder()
                .name("account")
                .commencementDate(now)
                .competentAuthority(CompetentAuthority.ENGLAND)
                .location(locationDTO)
                .legalEntity(LegalEntityDTO.builder().id(2L).build())
                .build();
        LegalEntity newLegalEntity = LegalEntity.builder().id(2L).name("le2").build();
        Account newAccount = Account.builder()
                .id(accountId)
                .name("account")
                .location(location)
                .build();
        Account accountSaved = Account.builder()
                .name("account")
                .status(AccountStatus.LIVE)
                .location(location)
                .legalEntity(newLegalEntity)
                .build();
        AccountDTO accountDTOSaved = AccountDTO.builder()
                .name("account")
                .commencementDate(now)
                .competentAuthority(CompetentAuthority.ENGLAND)
                .location(locationDTO)
                .legalEntity(LegalEntityDTO.builder().id(2L).name("le2").build())
                .build();
        
        when(accountQueryService.getAccountById(accountId)).thenReturn(account);
        when(legalEntityService.getLegalEntityById(account.getLegalEntity().getId())).thenReturn(legalEntity);
        when(legalEntityService.resolveAmendedLegalEntity(newAccountDTO.getLegalEntity(), legalEntity, pmrvUser)).thenReturn(newLegalEntity);
        when(accountMapper.toAccount(newAccountDTO, accountId)).thenReturn(newAccount);
        when(accountRepository.save(newAccount)).thenReturn(accountSaved);
        when(accountQueryService.isLegalEntityUnused(legalEntity.getId())).thenReturn(true);
        when(accountMapper.toAccountDTO(accountSaved)).thenReturn(accountDTOSaved);
        
        //invoke
        AccountDTO result = service.amendAccount(accountId, previousAccountDTO, newAccountDTO, pmrvUser);
        
        assertThat(result).isEqualTo(accountDTOSaved);

        verify(accountQueryService, times(1)).getAccountById(accountId);
        verify(legalEntityService, times(1)).getLegalEntityById(account.getLegalEntity().getId());
        verify(legalEntityService, times(1)).resolveAmendedLegalEntity(newAccountDTO.getLegalEntity(), legalEntity, pmrvUser);
        verify(accountMapper, times(1)).toAccount(newAccountDTO, accountId);
        verify(accountQueryService, times(1)).isLegalEntityUnused(legalEntity.getId());
        verify(accountMapper, times(1)).toAccountDTO(accountSaved);
        verify(legalEntityService, times(1)).deleteLegalEntity(legalEntity);
        
        ArgumentCaptor<Account> accountCaptor = ArgumentCaptor.forClass(Account.class);
        verify(accountRepository, times(1)).save(accountCaptor.capture());
        Account accountCaptured = accountCaptor.getValue();
        assertThat(accountCaptured.getStatus()).isEqualTo(account.getStatus());
        assertThat(accountCaptured.getLegalEntity()).isEqualTo(newLegalEntity);
    }

}
