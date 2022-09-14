package uk.gov.pmrv.api.account.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static uk.gov.pmrv.api.common.exception.ErrorCode.ACCOUNT_ALREADY_EXISTS;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.pmrv.api.account.domain.Account;
import uk.gov.pmrv.api.account.domain.LegalEntity;
import uk.gov.pmrv.api.account.domain.dto.AccountDTO;
import uk.gov.pmrv.api.account.domain.dto.LegalEntityDTO;
import uk.gov.pmrv.api.account.domain.enumeration.AccountStatus;
import uk.gov.pmrv.api.account.repository.AccountRepository;
import uk.gov.pmrv.api.account.transform.AccountMapper;
import uk.gov.pmrv.api.common.domain.model.PmrvUser;
import uk.gov.pmrv.api.common.exception.BusinessException;

@ExtendWith(MockitoExtension.class)
class AccountCreationServiceTest {

    @InjectMocks
    private AccountCreationService accountCreationService;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private AccountIdentifierService accountIdentifierService;

    @Mock
    private LegalEntityService legalEntityService;

    @Mock
    private AccountMapper accountMapper;

    @Mock
    private AccountQueryService accountQueryService;
    
    @Test
    void createAccount() {
        final PmrvUser pmrvUser = PmrvUser.builder().build();
        long identifierId = 2L;
        
        AccountDTO accountDTO = AccountDTO.builder()
                .name("account")
                .legalEntity(LegalEntityDTO.builder().id(1L).build())
                .build();
        Account account = Account.builder()
                .id(identifierId)
                .name("account")
                .legalEntity(LegalEntity.builder().id(1L).build())
                .build();
        
        LegalEntity legalEntity = LegalEntity.builder()
                .id(1L).name("le")
                .build();
        
        Account accountSaved = Account.builder()
                .name("account")
                .legalEntity(LegalEntity.builder().id(1L).name("le").build())
                .build();
        AccountDTO accountDTOSaved = AccountDTO.builder()
                .name("account")
                .legalEntity(LegalEntityDTO.builder().id(1L).name("le").build())
                .build();

        when(accountIdentifierService.incrementAndGet()).thenReturn(identifierId);
        when(legalEntityService.resolveLegalEntity(accountDTO.getLegalEntity(), pmrvUser)).thenReturn(legalEntity);
        when(accountMapper.toAccount(accountDTO, identifierId)).thenReturn(account);
        when(accountRepository.save(account)).thenReturn(accountSaved);
        when(accountMapper.toAccountDTO(accountSaved)).thenReturn(accountDTOSaved);
        
        //invoke
        AccountDTO result = accountCreationService.createAccount(accountDTO, pmrvUser);

        assertThat(result).isEqualTo(accountDTOSaved);

        verify(accountIdentifierService, times(1)).incrementAndGet();
        verify(legalEntityService, times(1)).resolveLegalEntity(accountDTO.getLegalEntity(), pmrvUser);
        verify(accountMapper, times(1)).toAccount(accountDTO, identifierId);
        ArgumentCaptor<Account> accountCaptor = ArgumentCaptor.forClass(Account.class);
        verify(accountRepository, times(1)).save(accountCaptor.capture());
        Account accountCaptured = accountCaptor.getValue();
        assertThat(accountCaptured.getStatus()).isEqualTo(AccountStatus.UNAPPROVED);
        assertThat(accountCaptured.getLegalEntity()).isEqualTo(legalEntity);
    }

    @Test
    void createAccount_for_migration_with_id() {
        final PmrvUser pmrvUser = PmrvUser.builder().build();
        long identifierId = 2L;

        AccountDTO accountDTO = AccountDTO.builder()
                .id(identifierId)
                .name("account")
                .legalEntity(LegalEntityDTO.builder().id(1L).build())
                .build();
        Account account = Account.builder()
                .id(identifierId)
                .name("account")
                .legalEntity(LegalEntity.builder().id(1L).build())
                .build();

        LegalEntity legalEntity = LegalEntity.builder()
                .id(1L).name("le")
                .build();

        Account accountSaved = Account.builder()
                .name("account")
                .legalEntity(LegalEntity.builder().id(1L).name("le").build())
                .build();
        AccountDTO accountDTOSaved = AccountDTO.builder()
                .name("account")
                .legalEntity(LegalEntityDTO.builder().id(1L).name("le").build())
                .build();

        when(legalEntityService.resolveLegalEntity(accountDTO.getLegalEntity(), pmrvUser)).thenReturn(legalEntity);
        when(accountMapper.toAccount(accountDTO, identifierId)).thenReturn(account);
        when(accountRepository.save(account)).thenReturn(accountSaved);
        when(accountMapper.toAccountDTO(accountSaved)).thenReturn(accountDTOSaved);

        //invoke
        AccountDTO result = accountCreationService.createAccount(accountDTO, pmrvUser);

        assertThat(result).isEqualTo(accountDTOSaved);

        verify(accountIdentifierService, never()).incrementAndGet();
        verify(legalEntityService, times(1)).resolveLegalEntity(accountDTO.getLegalEntity(), pmrvUser);
        verify(accountMapper, times(1)).toAccount(accountDTO, identifierId);
        ArgumentCaptor<Account> accountCaptor = ArgumentCaptor.forClass(Account.class);
        verify(accountRepository, times(1)).save(accountCaptor.capture());
        Account accountCaptured = accountCaptor.getValue();
        assertThat(accountCaptured.getStatus()).isEqualTo(AccountStatus.UNAPPROVED);
        assertThat(accountCaptured.getLegalEntity()).isEqualTo(legalEntity);
    }

    @Test
    void createAccount_account_not_valid_should_throw_exception() {
        final String accountName = "account";
        final PmrvUser pmrvUser = PmrvUser.builder().build();
        AccountDTO accountDTO = AccountDTO.builder()
                .name(accountName)
                .legalEntity(LegalEntityDTO.builder().id(1L).build())
                .build();

        doThrow(new BusinessException((ACCOUNT_ALREADY_EXISTS))).when(accountQueryService)
            .validateAccountName(accountDTO.getName());

        //invoke
        BusinessException businessException = assertThrows(BusinessException.class,
            () -> accountCreationService.createAccount(accountDTO, pmrvUser));

        assertThat(businessException.getErrorCode()).isEqualTo(ACCOUNT_ALREADY_EXISTS);

        verify(accountQueryService, times(1)).validateAccountName(accountName);
        verifyNoInteractions(legalEntityService, accountIdentifierService);
        verifyNoMoreInteractions(accountRepository, accountMapper);
    }
}
