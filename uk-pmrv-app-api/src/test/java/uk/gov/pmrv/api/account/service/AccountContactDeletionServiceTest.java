package uk.gov.pmrv.api.account.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.account.domain.Account;
import uk.gov.pmrv.api.account.domain.enumeration.AccountContactType;
import uk.gov.pmrv.api.account.repository.AccountRepository;
import uk.gov.pmrv.api.account.service.validator.AccountContactTypeDeleteValidator;
import uk.gov.pmrv.api.account.service.validator.PrimaryContactValidator;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;

@ExtendWith(MockitoExtension.class)
class AccountContactDeletionServiceTest {

    @InjectMocks
    private AccountContactDeletionService accountContactDeletionService;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private PrimaryContactValidator primaryContactValidator;

    @Spy
    private ArrayList<AccountContactTypeDeleteValidator> accountContactTypeDeleteValidators;

    @BeforeEach
    void setUp() {
        accountContactTypeDeleteValidators.add(primaryContactValidator);
    }

    @Test
    void removeUserFromAccountContacts() {
        Long accountId = 1L;
        String userId1 = "userId1";
        String userId2 = "userId2";

        Map<AccountContactType, String> accountContacts = new HashMap<>();
        accountContacts.put(AccountContactType.PRIMARY, userId2);
        accountContacts.put(AccountContactType.SECONDARY, userId1);
        Account account = Account.builder().id(accountId).name("acc_name").contacts(accountContacts).build();

        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));

        accountContactDeletionService.removeUserFromAccountContacts(userId1, accountId);

        ArgumentCaptor<Account> accountCaptor = ArgumentCaptor.forClass(Account.class);
        verify(accountRepository, times(1)).save(accountCaptor.capture());

        Account savedAccount = accountCaptor.getValue();

        assertEquals(savedAccount.getName(), account.getName());
        assertFalse(savedAccount.getContacts().containsValue(userId1));
        assertThat(savedAccount.getContacts()).containsOnlyKeys(AccountContactType.PRIMARY);
    }

    @Test
    void removeUserFromAccountContacts_when_user_not_related_to_account_contacts() {
        Long accountId = 1L;
        String userId1 = "userId1";
        String userId2 = "userId2";

        Map<AccountContactType, String> accountContacts = new HashMap<>();
        accountContacts.put(AccountContactType.PRIMARY, userId2);
        accountContacts.put(AccountContactType.SECONDARY, userId2);
        Account account = Account.builder().id(accountId).name("acc_name").contacts(accountContacts).build();

        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));

        accountContactDeletionService.removeUserFromAccountContacts(userId1, accountId);

        verify(accountRepository, never()).save(any());
    }

    @Test
    void removeUserFromAccountContacts_throws_exception_when_account_not_found() {
        Long accountId = 1L;
        String userId = "userId";

        when(accountRepository.findById(accountId)).thenReturn(Optional.empty());

        BusinessException businessException = assertThrows(BusinessException.class,
            () -> accountContactDeletionService.removeUserFromAccountContacts(userId, accountId));

        assertEquals(ErrorCode.RESOURCE_NOT_FOUND, businessException.getErrorCode());
    }
}