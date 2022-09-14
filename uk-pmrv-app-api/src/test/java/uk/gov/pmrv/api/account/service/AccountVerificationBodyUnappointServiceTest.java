package uk.gov.pmrv.api.account.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.Set;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.pmrv.api.account.domain.Account;
import uk.gov.pmrv.api.account.repository.AccountRepository;
import uk.gov.pmrv.api.common.domain.enumeration.EmissionTradingScheme;

@ExtendWith(MockitoExtension.class)
class AccountVerificationBodyUnappointServiceTest {

    @InjectMocks
    private AccountVerificationBodyUnappointService service;
    
    @Mock
    private AccountRepository accountRepository;
    
    @Mock
    private AccountVbSiteContactService accountVbSiteContactService;
    
    @Mock
    private AccountVerificationBodyNotificationService accountVerificationBodyNotificationService;
    
    @Test
    void unappointAccountsAppointedToVerificationBodyForEmissionTradingSchemes() {
        Long verificationBodyId = 1L; 
        Set<EmissionTradingScheme> notAvailableAccreditationEmissionTradingSchemes = Set.of(EmissionTradingScheme.UK_ETS_INSTALLATIONS, EmissionTradingScheme.CORSIA);
        
        Set<Account> accountsToBeUnappointed = 
                Set.of(Account.builder().id(1L).name("name1").verificationBodyId(verificationBodyId).build());
        
        when(accountRepository.findAllByVerificationBodyAndEmissionTradingSchemeIn(verificationBodyId, notAvailableAccreditationEmissionTradingSchemes))
            .thenReturn(accountsToBeUnappointed);
        
        //invoke
        service.unappointAccountsAppointedToVerificationBodyForEmissionTradingSchemes(verificationBodyId, notAvailableAccreditationEmissionTradingSchemes);
        
        verify(accountRepository, times(1)).findAllByVerificationBodyAndEmissionTradingSchemeIn(verificationBodyId, notAvailableAccreditationEmissionTradingSchemes);
        verify(accountVbSiteContactService, times(1)).removeVbSiteContactFromAccounts(accountsToBeUnappointed);
        verify(accountVerificationBodyNotificationService, times(1)).notifyUsersForVerificationBodyUnapppointment(accountsToBeUnappointed);
        ArgumentCaptor<Account> accountCaptor = ArgumentCaptor.forClass(Account.class);
        verify(accountRepository, times(1)).save(accountCaptor.capture());
        Account accountSaved = accountCaptor.getValue();
        assertThat(accountSaved.getVerificationBodyId()).isNull();
    }
    
    @Test
    void unappointAccountsAppointedToVerificationBodyForEmissionTradingSchemes_empty_ref_num_list() {
        Long verificationBodyId = 1L; 
        Set<EmissionTradingScheme> notAvailableAccreditationEmissionTradingSchemes = Set.of();
        
        //invoke
        service.unappointAccountsAppointedToVerificationBodyForEmissionTradingSchemes(verificationBodyId, notAvailableAccreditationEmissionTradingSchemes);
        
        verifyNoInteractions(accountRepository, accountVbSiteContactService, accountVerificationBodyNotificationService);
    }

    @Test
    void unappointAccountsAppointedToVerificationBody() {
        Long verificationBodyId = 1L;
        Set<Long> verificationBodyIds = Set.of(verificationBodyId);
        Set<Account> accountsToBeUnappointed = 
                Set.of(
                        Account.builder().id(1L).name("name1").verificationBodyId(verificationBodyId).build()
                        );

        // Mock
        when(accountRepository.findAllByVerificationBodyIn(verificationBodyIds)).thenReturn(accountsToBeUnappointed);

        // Invoke
        service.unappointAccountsAppointedToVerificationBody(verificationBodyIds);

        // Assert
        verify(accountRepository, times(1)).findAllByVerificationBodyIn(verificationBodyIds);
        verify(accountVbSiteContactService, times(1)).removeVbSiteContactFromAccounts(accountsToBeUnappointed);
        verify(accountVerificationBodyNotificationService, times(1)).notifyUsersForVerificationBodyUnapppointment(accountsToBeUnappointed);
        ArgumentCaptor<Account> accountCaptor = ArgumentCaptor.forClass(Account.class);
        verify(accountRepository, times(1)).save(accountCaptor.capture());
        Account accountSaved = accountCaptor.getValue();
        assertThat(accountSaved.getVerificationBodyId()).isNull();
    }

    @Test
    void unappointAccountsAppointedToVerificationBody_no_accounts() {
        Long verificationBodyId = 1L;
        Set<Long> verificationBodyIds = Set.of(verificationBodyId);
        Set<Account> accountsToBeUnappointed = Set.of();

        // Mock
        when(accountRepository.findAllByVerificationBodyIn(verificationBodyIds)).thenReturn(accountsToBeUnappointed);

        // Invoke
        service.unappointAccountsAppointedToVerificationBody(verificationBodyIds);

        // Assert
        verify(accountRepository, times(1)).findAllByVerificationBodyIn(verificationBodyIds);
        verify(accountRepository, never()).save(Mockito.any(Account.class));
        verify(accountVbSiteContactService, never()).removeVbSiteContactFromAccounts(anySet());
        verify(accountVerificationBodyNotificationService, never()).notifyUsersForVerificationBodyUnapppointment(anySet());
    }
}
