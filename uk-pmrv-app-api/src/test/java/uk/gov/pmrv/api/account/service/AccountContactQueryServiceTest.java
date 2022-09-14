package uk.gov.pmrv.api.account.service;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.account.domain.Account;
import uk.gov.pmrv.api.account.domain.enumeration.AccountContactType;
import uk.gov.pmrv.api.account.repository.AccountRepository;
import uk.gov.pmrv.api.common.exception.BusinessCheckedException;

import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccountContactQueryServiceTest {

    @InjectMocks
    private AccountContactQueryService service;

    @Mock
    private AccountRepository accountRepository;

    @Test
    void findContactByAccountAndContactType() {
        Long accountId = 1L;
        String caSiteContact = "ca";

        when(accountRepository.findById(accountId)).thenReturn(
                Optional.of(Account.builder().id(accountId).contacts(Map.of(AccountContactType.CA_SITE, caSiteContact)).build())
        );

        Optional<String> caSiteContactOpt = service.findContactByAccountAndContactType(accountId, AccountContactType.CA_SITE);

        assertThat(caSiteContactOpt).contains(caSiteContact);
        verify(accountRepository, times(1)).findById(accountId);
    }

    @Test
    void findContactByAccountAndContactType_no_contact_found() {
        Long accountId = 1L;

        when(accountRepository.findById(accountId)).thenReturn(
                Optional.of(Account.builder().id(accountId).contacts(Map.of(AccountContactType.FINANCIAL, "financial")).build())
        );

        Optional<String> caSiteContactOpt = service.findContactByAccountAndContactType(accountId, AccountContactType.CA_SITE);

        assertThat(caSiteContactOpt).isEmpty();
        verify(accountRepository, times(1)).findById(accountId);
    }

    @Test
    void findPrimaryContactByAccount() throws BusinessCheckedException {
        Long accountId = 1L;

        when(accountRepository.findById(accountId)).thenReturn(
                Optional.of(Account.builder().id(accountId).contacts(Map.of(AccountContactType.PRIMARY, "user")).build())
        );

        String result = service.findPrimaryContactByAccount(accountId);
        assertThat(result).isEqualTo("user");
    }

    @Test
    void findPrimaryContactByAccount_not_found() {
        Long accountId = 1L;

        when(accountRepository.findById(accountId)).thenReturn(Optional.empty());

        assertThrows(BusinessCheckedException.class, () -> {
            service.findPrimaryContactByAccount(accountId);
        });
    }

    @Test
    void findContactTypesByAccount() {
        Long accountId = 1L;

        Map<AccountContactType, String> contactTypes =
                Map.of(
                        AccountContactType.PRIMARY, "primary",
                        AccountContactType.SERVICE, "service");

        when(accountRepository.findById(accountId)).thenReturn(
                Optional.of(Account.builder().id(accountId).contacts(contactTypes).build())
        );

        //invoke
        Map<AccountContactType, String> result = service.findContactTypesByAccount(accountId);

        assertThat(result).isEqualTo(contactTypes);
        verify(accountRepository, times(1)).findById(accountId);
    }

    @Test
    void findContactTypesByAccount_account_not_found() {
        Long accountId = 1L;

        when(accountRepository.findById(accountId)).thenReturn(
                Optional.empty());

        //invoke
        Map<AccountContactType, String> result = service.findContactTypesByAccount(accountId);

        assertThat(result).isEqualTo(Map.of());
        verify(accountRepository, times(1)).findById(accountId);
    }

    @Test
    void findContactTypesByAccount_empty_contacts() {
        Long accountId = 1L;
        Map<AccountContactType, String> contactTypes = Map.of();
        when(accountRepository.findById(accountId)).thenReturn(
                Optional.of(Account.builder().id(accountId).contacts(contactTypes).build()));

        //invoke
        Map<AccountContactType, String> result = service.findContactTypesByAccount(accountId);

        assertThat(result).isEqualTo(Map.of());
        verify(accountRepository, times(1)).findById(accountId);
    }

    @Test
    void findOperatorContactTypesByAccount() {
        Long accountId = 1L;

        Map<AccountContactType, String> contactTypes =
                Map.of(AccountContactType.PRIMARY, "primary",
                        AccountContactType.SERVICE, "service",
                        AccountContactType.SECONDARY, "secondary",
                        AccountContactType.FINANCIAL, "financial",
                        AccountContactType.CA_SITE, "ca_site",
                        AccountContactType.VB_SITE, "vb_site");

        when(accountRepository.findById(accountId)).thenReturn(
                Optional.of(Account.builder().id(accountId).contacts(contactTypes).build())
        );

        Map<AccountContactType, String> result = service.findOperatorContactTypesByAccount(accountId);

        assertThat(result).isEqualTo(Map.of(AccountContactType.PRIMARY, "primary",
                AccountContactType.SECONDARY, "secondary",
                AccountContactType.SERVICE, "service",
                AccountContactType.FINANCIAL, "financial"));
        verify(accountRepository, times(1)).findById(accountId);
    }

    @Test
    void findContactTypesByAccountIds() {
        Long accountId = 1L;

        Map<AccountContactType, String> contactTypes =
            Map.of(AccountContactType.PRIMARY, "primary",
                AccountContactType.SERVICE, "service",
                AccountContactType.SECONDARY, "secondary",
                AccountContactType.FINANCIAL, "financial",
                AccountContactType.CA_SITE, "ca_site",
                AccountContactType.VB_SITE, "vb_site");

        Account account = Account.builder().id(accountId).contacts(contactTypes).build();

        when(accountRepository.findAllByIdIn(List.of(accountId))).thenReturn(List.of(account));

        Map<Long, Map<AccountContactType, String>> contactTypesByAccountIds = service.findContactTypesByAccountIds(List.of(accountId));

        assertThat(contactTypesByAccountIds.size()).isEqualTo(1);
        assertThat(contactTypesByAccountIds.get(accountId)).isEqualTo(contactTypes);
    }
}
