package uk.gov.pmrv.api.account.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import uk.gov.pmrv.api.account.domain.Account;
import uk.gov.pmrv.api.account.domain.dto.AccountContactDTO;
import uk.gov.pmrv.api.account.domain.dto.AccountContactVbInfoDTO;
import uk.gov.pmrv.api.account.domain.dto.AccountContactVbInfoResponse;
import uk.gov.pmrv.api.account.domain.enumeration.AccountContactType;
import uk.gov.pmrv.api.account.repository.AccountRepository;
import uk.gov.pmrv.api.authorization.rules.domain.Scope;
import uk.gov.pmrv.api.authorization.rules.services.resource.VerificationBodyAuthorizationResourceService;
import uk.gov.pmrv.api.authorization.rules.services.resource.VerifierAuthorityResourceService;
import uk.gov.pmrv.api.common.domain.enumeration.RoleType;
import uk.gov.pmrv.api.common.domain.model.PmrvAuthority;
import uk.gov.pmrv.api.common.domain.model.PmrvUser;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccountVbSiteContactServiceTest {

    @InjectMocks
    private AccountVbSiteContactService service;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private VerificationBodyAuthorizationResourceService verificationBodyAuthorizationResourceService;

    @Mock
    private VerifierAuthorityResourceService verifierAuthorityResourceService;

    @Test
    void getAccountsAndVbSiteContacts() {
        final Long vbId = 1L;
        final PmrvUser user = PmrvUser.builder().roleType(RoleType.VERIFIER)
                .authorities(List.of(PmrvAuthority.builder().verificationBodyId(vbId).build())).build();
        List<AccountContactVbInfoDTO> contacts = List.of(AccountContactVbInfoDTO.builder()
                .accountId(1L).accountName("name").type("UK ETS Installation").userId("userId").build());
        Page<AccountContactVbInfoDTO> pagedAccounts = new PageImpl<>(contacts);

        AccountContactVbInfoResponse expected = AccountContactVbInfoResponse.builder()
                .contacts(contacts).editable(true).totalItems(1L).build();

        // Mock
        when(verificationBodyAuthorizationResourceService.hasUserScopeToVerificationBody(user, vbId, Scope.EDIT_USER))
                .thenReturn(true);
        when(accountRepository.findAccountContactsByVbAndContactType(PageRequest.of(0, 1), vbId, AccountContactType.VB_SITE))
                .thenReturn(pagedAccounts);

        // Invoke
        AccountContactVbInfoResponse actual = service.getAccountsAndVbSiteContacts(user,0, 1);

        // Assert
        assertEquals(expected, actual);
        verify(verificationBodyAuthorizationResourceService, times(1))
                .hasUserScopeToVerificationBody(user, vbId, Scope.EDIT_USER);
        verify(accountRepository, times(1))
                .findAccountContactsByVbAndContactType(PageRequest.of(0, 1), vbId, AccountContactType.VB_SITE);
    }

    @Test
    void getAccountsAndVbSiteContacts_not_editable() {
        final Long vbId = 1L;
        final PmrvUser user = PmrvUser.builder().roleType(RoleType.VERIFIER)
                .authorities(List.of(PmrvAuthority.builder().verificationBodyId(vbId).build())).build();
        List<AccountContactVbInfoDTO> contacts = List.of(AccountContactVbInfoDTO.builder()
                .accountId(1L).accountName("name").type("UK ETS Installation").userId("userId").build());
        Page<AccountContactVbInfoDTO> pagedAccounts = new PageImpl<>(contacts);

        AccountContactVbInfoResponse expected = AccountContactVbInfoResponse.builder()
                .contacts(contacts).editable(false).totalItems(1L).build();

        // Mock
        when(verificationBodyAuthorizationResourceService.hasUserScopeToVerificationBody(user, vbId, Scope.EDIT_USER))
                .thenReturn(false);
        when(accountRepository.findAccountContactsByVbAndContactType(PageRequest.of(0, 1), vbId, AccountContactType.VB_SITE))
                .thenReturn(pagedAccounts);

        // Invoke
        AccountContactVbInfoResponse actual = service.getAccountsAndVbSiteContacts(user, 0, 1);

        // Assert
        assertEquals(expected, actual);
        verify(verificationBodyAuthorizationResourceService, times(1))
                .hasUserScopeToVerificationBody(user, vbId, Scope.EDIT_USER);
        verify(accountRepository, times(1))
                .findAccountContactsByVbAndContactType(PageRequest.of(0, 1), vbId, AccountContactType.VB_SITE);
    }


    @Test
    void getAccountsAndVbSiteContacts_no_contacts() {
        final Long vbId = 1L;
        final PmrvUser user = PmrvUser.builder().roleType(RoleType.VERIFIER)
                .authorities(List.of(PmrvAuthority.builder().verificationBodyId(vbId).build())).build();
        Page<AccountContactVbInfoDTO> pagedAccounts = new PageImpl<>(List.of());

        AccountContactVbInfoResponse expected = AccountContactVbInfoResponse.builder()
                .contacts(List.of()).editable(true).totalItems(0L).build();

        // Mock
        when(verificationBodyAuthorizationResourceService.hasUserScopeToVerificationBody(user, vbId, Scope.EDIT_USER))
                .thenReturn(true);
        when(accountRepository.findAccountContactsByVbAndContactType(PageRequest.of(0, 1), vbId, AccountContactType.VB_SITE))
                .thenReturn(pagedAccounts);

        // Invoke
        AccountContactVbInfoResponse actual = service.getAccountsAndVbSiteContacts(user, 0, 1);

        // Assert
        assertEquals(expected, actual);
        verify(verificationBodyAuthorizationResourceService, times(1))
                .hasUserScopeToVerificationBody(user, vbId, Scope.EDIT_USER);
        verify(accountRepository, times(1))
                .findAccountContactsByVbAndContactType(PageRequest.of(0, 1), vbId, AccountContactType.VB_SITE);
    }

    @Test
    void updateVbSiteContacts() {
        final Long accountId = 1L;
        final String oldUser = "old";
        final String newUser = "new";
        final Long vbId = 1L;

        final PmrvUser user = PmrvUser.builder().roleType(RoleType.VERIFIER)
                .authorities(List.of(PmrvAuthority.builder().verificationBodyId(vbId).build())).build();
        List<AccountContactDTO> vbSiteContactsUpdate = List.of(AccountContactDTO.builder().accountId(accountId).userId(newUser).build());
        Map<AccountContactType, String> accountContacts = new HashMap<>(){{ put(AccountContactType.VB_SITE, oldUser); }};
        List<Account> accounts = List.of(Account.builder().id(accountId).name("account").contacts(accountContacts).build());

        // Mock
        when(accountRepository.findAllIdsByVB(vbId)).thenReturn(List.of(accountId));
        when(verifierAuthorityResourceService.findUsersByVerificationBodyId(vbId)).thenReturn(List.of(newUser));
        when(accountRepository.findAllByIdIn(List.of(accountId))).thenReturn(accounts);

        // Invoke
        service.updateVbSiteContacts(user, vbSiteContactsUpdate);

        // Assert
        ArgumentCaptor<Account> accountCaptor = ArgumentCaptor.forClass(Account.class);
        verify(accountRepository, times(1)).save(accountCaptor.capture());
        Account accountCaptured = accountCaptor.getValue();
        assertEquals(accountCaptured.getContacts().get(AccountContactType.VB_SITE), newUser);
        verify(accountRepository, times(1)).findAllIdsByVB(vbId);
        verify(accountRepository, times(1)).findAllByIdIn(List.of(accountId));
        verify(verifierAuthorityResourceService, times(1)).findUsersByVerificationBodyId(vbId);
    }

    @Test
    void updateVbSiteContacts_account_not_in_vb() {
        final Long accountId = 1L;
        final String newUser = "new";
        final Long vbId = 1L;

        final PmrvUser user = PmrvUser.builder().roleType(RoleType.VERIFIER)
                .authorities(List.of(PmrvAuthority.builder().verificationBodyId(vbId).build())).build();
        List<AccountContactDTO> vbSiteContactsUpdate = List.of(AccountContactDTO.builder().accountId(accountId).userId(newUser).build());

        // Mock
        when(accountRepository.findAllIdsByVB(vbId)).thenReturn(List.of());

        // Invoke
        BusinessException businessException = assertThrows(BusinessException.class, () ->
                service.updateVbSiteContacts(user, vbSiteContactsUpdate));

        // Assert
        assertEquals(ErrorCode.ACCOUNT_NOT_RELATED_TO_VB, businessException.getErrorCode());
        verify(accountRepository, never()).save(any());
        verify(accountRepository, times(1)).findAllIdsByVB(vbId);
        verify(accountRepository, never()).findAllByIdIn(anyList());
        verify(verifierAuthorityResourceService, never()).findUsersByVerificationBodyId(anyLong());
    }

    @Test
    void updateVbSiteContacts_user_not_in_vb() {
        final Long accountId = 1L;
        final String newUser = "new";
        final Long vbId = 1L;

        final PmrvUser user = PmrvUser.builder().roleType(RoleType.VERIFIER)
                .authorities(List.of(PmrvAuthority.builder().verificationBodyId(vbId).build())).build();
        List<AccountContactDTO> vbSiteContactsUpdate = List.of(AccountContactDTO.builder().accountId(accountId).userId(newUser).build());

        // Mock
        when(accountRepository.findAllIdsByVB(vbId)).thenReturn(List.of(accountId));
        when(verifierAuthorityResourceService.findUsersByVerificationBodyId(vbId)).thenReturn(List.of());

        // Invoke
        BusinessException businessException = assertThrows(BusinessException.class, () ->
                service.updateVbSiteContacts(user, vbSiteContactsUpdate));

        // Assert
        assertEquals(ErrorCode.AUTHORITY_USER_NOT_RELATED_TO_VERIFICATION_BODY, businessException.getErrorCode());
        verify(accountRepository, never()).save(any());
        verify(accountRepository, times(1)).findAllIdsByVB(vbId);
        verify(accountRepository, never()).findAllByIdIn(anyList());
        verify(verifierAuthorityResourceService, times(1)).findUsersByVerificationBodyId(vbId);
    }
    
    @Test
    void removeVbSiteContactFromAccounts() {
        Account account1 = Account.builder().id(1L).build();
        Map<AccountContactType, String> contacts1 = new HashMap<>();
        contacts1.put(AccountContactType.VB_SITE, "vb_site");
        account1.setContacts(contacts1);
        
        service.removeVbSiteContactFromAccounts(Set.of(account1));
        
        ArgumentCaptor<Account> accountCaptor = ArgumentCaptor.forClass(Account.class);
        verify(accountRepository, times(1)).save(accountCaptor.capture());
        Account accountCaptured = accountCaptor.getValue();
        assertThat(accountCaptured.getContacts().get(AccountContactType.VB_SITE)).isNull();
    }
    
    @Test
    void removeVbSiteContactFromAccounts_no_nb_site_contained() {
        Account account1 = Account.builder().id(1L).build();
        Map<AccountContactType, String> contacts1 = new HashMap<>();
        contacts1.put(AccountContactType.CA_SITE, "ca_site");
        account1.setContacts(contacts1);
        
        service.removeVbSiteContactFromAccounts(Set.of(account1));
        
        ArgumentCaptor<Account> accountCaptor = ArgumentCaptor.forClass(Account.class);
        verify(accountRepository, times(1)).save(accountCaptor.capture());
        Account accountCaptured = accountCaptor.getValue();
        assertThat(accountCaptured.getContacts()).containsEntry(AccountContactType.CA_SITE, "ca_site");
    }
}
