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
import uk.gov.pmrv.api.account.domain.dto.AccountContactInfoDTO;
import uk.gov.pmrv.api.account.domain.dto.AccountContactInfoResponse;
import uk.gov.pmrv.api.account.domain.enumeration.AccountContactType;
import uk.gov.pmrv.api.account.repository.AccountRepository;
import uk.gov.pmrv.api.authorization.rules.services.resource.RegulatorAuthorityResourceService;
import uk.gov.pmrv.api.authorization.rules.domain.Scope;
import uk.gov.pmrv.api.authorization.rules.services.resource.CompAuthAuthorizationResourceService;
import uk.gov.pmrv.api.common.domain.enumeration.CompetentAuthority;
import uk.gov.pmrv.api.common.domain.enumeration.RoleType;
import uk.gov.pmrv.api.common.domain.model.PmrvAuthority;
import uk.gov.pmrv.api.common.domain.model.PmrvUser;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccountCaSiteContactServiceTest {

    @InjectMocks
    private AccountCaSiteContactService service;

    @Mock
    private AccountRepository accountRepository;
    
    @Mock
    private CompAuthAuthorizationResourceService compAuthAuthorizationResourceService;

    @Mock
    private RegulatorAuthorityResourceService regulatorAuthorityResourceService;

    @Test
    void getAccountsAndCaSiteContacts() {
        final CompetentAuthority ca = CompetentAuthority.WALES;
        final PmrvUser user = PmrvUser.builder().roleType(RoleType.REGULATOR)
                .authorities(List.of(PmrvAuthority.builder().competentAuthority(ca).build())).build();
        List<AccountContactInfoDTO> contacts = List.of(AccountContactInfoDTO.builder()
                .accountId(1L).accountName("name").userId("userId").build());
        Page<AccountContactInfoDTO> pagedAccounts = new PageImpl<>(contacts);

        AccountContactInfoResponse expected = AccountContactInfoResponse.builder()
                .contacts(contacts).editable(true).totalItems(1L).build();

        when(compAuthAuthorizationResourceService.hasUserScopeToCompAuth(user, ca, Scope.EDIT_USER))
            .thenReturn(true);
        when(accountRepository.findApprovedAccountContactsByCaAndContactType(PageRequest.of(0, 1), ca, AccountContactType.CA_SITE))
            .thenReturn(pagedAccounts);

        // Invoke
        AccountContactInfoResponse actual = service.getAccountsAndCaSiteContacts(user,0, 1);

        // Assert
        assertEquals(expected, actual);
        verify(compAuthAuthorizationResourceService, times(1)).hasUserScopeToCompAuth(user, ca, Scope.EDIT_USER);
        verify(accountRepository, times(1)).findApprovedAccountContactsByCaAndContactType(PageRequest.of(0, 1), ca, AccountContactType.CA_SITE);
    }
    
    @Test
    void getAccountsAndCaSiteContacts_not_editable() {
        final CompetentAuthority ca = CompetentAuthority.WALES;
        final PmrvUser user = PmrvUser.builder().roleType(RoleType.REGULATOR)
                .authorities(List.of(PmrvAuthority.builder().competentAuthority(ca).build())).build();
        List<AccountContactInfoDTO> contacts = List.of(AccountContactInfoDTO.builder()
                .accountId(1L).accountName("name").userId("userId").build());
        Page<AccountContactInfoDTO> pagedAccounts = new PageImpl<>(contacts);

        AccountContactInfoResponse expected = AccountContactInfoResponse.builder()
                .contacts(contacts).editable(false).totalItems(1L).build();

        when(compAuthAuthorizationResourceService.hasUserScopeToCompAuth(user, ca, Scope.EDIT_USER))
            .thenReturn(false);
        when(accountRepository.findApprovedAccountContactsByCaAndContactType(PageRequest.of(0, 1), ca, AccountContactType.CA_SITE))
            .thenReturn(pagedAccounts);

        // Invoke
        AccountContactInfoResponse actual = service.getAccountsAndCaSiteContacts(user,0, 1);

        // Assert
        assertEquals(expected, actual);
        verify(compAuthAuthorizationResourceService, times(1)).hasUserScopeToCompAuth(user, ca, Scope.EDIT_USER);
        verify(accountRepository, times(1)).findApprovedAccountContactsByCaAndContactType(PageRequest.of(0, 1), ca, AccountContactType.CA_SITE);
    }


    @Test
    void getAccountsAndCaSiteContacts_no_contacts() {
        final CompetentAuthority ca = CompetentAuthority.WALES;
        final PmrvUser user = PmrvUser.builder().roleType(RoleType.REGULATOR)
                .authorities(List.of(PmrvAuthority.builder().competentAuthority(ca).build())).build();
        Page<AccountContactInfoDTO> pagedAccounts = new PageImpl<>(List.of());

        AccountContactInfoResponse expected = AccountContactInfoResponse.builder()
                .contacts(List.of()).editable(true).totalItems(0L).build();

        when(compAuthAuthorizationResourceService.hasUserScopeToCompAuth(user, ca, Scope.EDIT_USER)).thenReturn(true);
        when(accountRepository.findApprovedAccountContactsByCaAndContactType(PageRequest.of(0, 1), ca, AccountContactType.CA_SITE))
            .thenReturn(pagedAccounts);

        // Invoke
        AccountContactInfoResponse actual = service.getAccountsAndCaSiteContacts(user,0, 1);

        // Assert
        assertEquals(expected, actual);
        verify(compAuthAuthorizationResourceService, times(1)).hasUserScopeToCompAuth(user, ca, Scope.EDIT_USER);
        verify(accountRepository, times(1)).findApprovedAccountContactsByCaAndContactType(PageRequest.of(0, 1), ca, AccountContactType.CA_SITE);
    }
    
    @Test
    void updateCaSiteContacts() {
        final Long accountId = 1L;
        final String oldUser = "old";
        final String newUser = "new";
        final CompetentAuthority ca = CompetentAuthority.WALES;
        final PmrvUser user = PmrvUser.builder().roleType(RoleType.REGULATOR)
                .authorities(List.of(PmrvAuthority.builder().competentAuthority(ca).build())).build();
        List<AccountContactDTO> caSiteContactsUpdate = 
                List.of(AccountContactDTO.builder().accountId(accountId).userId(newUser).build());
        
        Map<AccountContactType, String> accountContacts = new HashMap<>();
        accountContacts.put(AccountContactType.CA_SITE, oldUser);
        List<Account> accounts = List.of(
                Account.builder().id(accountId).name("account").contacts(accountContacts).build()
                );

        when(accountRepository.findAllApprovedIdsByCA(ca)).thenReturn(List.of(accountId));
        when(regulatorAuthorityResourceService.findUsersByCompetentAuthority(ca)).thenReturn(List.of(newUser));
        when(accountRepository.findAllByIdIn(List.of(accountId))).thenReturn(accounts);

        // Invoke
        service.updateCaSiteContacts(user, caSiteContactsUpdate);
        
        ArgumentCaptor<Account> accountCaptor = ArgumentCaptor.forClass(Account.class);
        verify(accountRepository, times(1)).save(accountCaptor.capture());
        Account accountCaptured = accountCaptor.getValue();
        assertThat(accountCaptured.getContacts()).containsEntry(AccountContactType.CA_SITE, newUser);
        verify(accountRepository, times(1)).findAllApprovedIdsByCA(ca);
        verify(regulatorAuthorityResourceService, times(1)).findUsersByCompetentAuthority(ca);
    }
    
    @Test
    void updateCaSiteContacts_account_not_in_ca() {
        final Long accountId = 1L;
        final String newUser = "new";
        final CompetentAuthority ca = CompetentAuthority.WALES;
        final PmrvUser user = PmrvUser.builder().roleType(RoleType.REGULATOR)
                .authorities(List.of(PmrvAuthority.builder().competentAuthority(ca).build())).build();
        List<AccountContactDTO> caSiteContactsUpdate = 
                List.of(AccountContactDTO.builder().accountId(accountId).userId(newUser).build());
        
        when(accountRepository.findAllApprovedIdsByCA(ca)).thenReturn(List.of());

        BusinessException businessException = assertThrows(BusinessException.class, () ->
              service.updateCaSiteContacts(user, caSiteContactsUpdate));
        
        // Assert
        assertEquals(ErrorCode.ACCOUNT_NOT_RELATED_TO_CA, businessException.getErrorCode());
    }
    
    @Test
    void updateCaSiteContacts_user_not_in_ca() {
        final Long accountId = 1L;
        final String newUser = "new";
        final CompetentAuthority ca = CompetentAuthority.WALES;
        final PmrvUser user = PmrvUser.builder().roleType(RoleType.REGULATOR)
                .authorities(List.of(PmrvAuthority.builder().competentAuthority(ca).build())).build();
        List<AccountContactDTO> caSiteContactsUpdate = 
                List.of(AccountContactDTO.builder().accountId(accountId).userId(newUser).build());
        
        when(accountRepository.findAllApprovedIdsByCA(ca)).thenReturn(List.of(accountId));
        when(regulatorAuthorityResourceService.findUsersByCompetentAuthority(ca)).thenReturn(List.of());

        BusinessException businessException = assertThrows(BusinessException.class, () ->
              service.updateCaSiteContacts(user, caSiteContactsUpdate));
        
        // Assert
        assertEquals(ErrorCode.AUTHORITY_USER_NOT_RELATED_TO_CA, businessException.getErrorCode());
    }

    @Test
    void removeUserFromCaSiteContact() {
        String userId = "user";
        
        Map<AccountContactType, String> accountContacts = new HashMap<>();
        accountContacts.put(AccountContactType.CA_SITE, userId);
        List<Account> accounts = List.of(
                Account.builder().id(1L).name("account").contacts(accountContacts).build()
                );
        
        when(accountRepository.findAccountsByContactTypeAndUserId(AccountContactType.CA_SITE, userId))
            .thenReturn(accounts);
        
        //invoke
        service.removeUserFromCaSiteContact(userId);
        
        ArgumentCaptor<Account> accountCaptor = ArgumentCaptor.forClass(Account.class);
        verify(accountRepository, times(1)).save(accountCaptor.capture());
        Account accountCaptured = accountCaptor.getValue();
        assertThat(accountCaptured.getContacts()).doesNotContainKey(AccountContactType.CA_SITE);
    }
    
}
