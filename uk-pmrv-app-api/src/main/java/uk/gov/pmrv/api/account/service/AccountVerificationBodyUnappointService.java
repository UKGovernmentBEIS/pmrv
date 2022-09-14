package uk.gov.pmrv.api.account.service;

import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import uk.gov.pmrv.api.account.domain.Account;
import uk.gov.pmrv.api.account.repository.AccountRepository;
import uk.gov.pmrv.api.common.domain.enumeration.EmissionTradingScheme;

@RequiredArgsConstructor
@Service
public class AccountVerificationBodyUnappointService {
    
    private final AccountRepository accountRepository;
    private final AccountVbSiteContactService accountVbSiteContactService;
    private final AccountVerificationBodyNotificationService accountVerificationBodyNotificationService;

    @Transactional
    public void unappointAccountsAppointedToVerificationBodyForEmissionTradingSchemes(
            Long verificationBodyId, Set<EmissionTradingScheme> notAvailableAccreditationEmissionTradingSchemes) {
        if(notAvailableAccreditationEmissionTradingSchemes.isEmpty()) {
            return;
        }
        
        Set<Account> accountsToBeUnappointed = accountRepository.findAllByVerificationBodyAndEmissionTradingSchemeIn(
                verificationBodyId, notAvailableAccreditationEmissionTradingSchemes);

        unappointAccounts(accountsToBeUnappointed);
    }

    @Transactional
    public void unappointAccountsAppointedToVerificationBody(Set<Long> verificationBodyIds) {
        Set<Account> accountsToBeUnappointed = accountRepository.findAllByVerificationBodyIn(verificationBodyIds);
        unappointAccounts(accountsToBeUnappointed);
    }

    private void unappointAccounts(Set<Account> accountsToBeUnappointed) {
        if(accountsToBeUnappointed.isEmpty()) {
            return;
        }
        
        //clear verification body of accounts
        accountsToBeUnappointed.forEach(account -> {
            account.setVerificationBodyId(null);
            accountRepository.save(account);   
        });
        
        accountVbSiteContactService.removeVbSiteContactFromAccounts(accountsToBeUnappointed);

        // Notify users for unappointment
        accountVerificationBodyNotificationService.notifyUsersForVerificationBodyUnapppointment(accountsToBeUnappointed);
    }
    
}
