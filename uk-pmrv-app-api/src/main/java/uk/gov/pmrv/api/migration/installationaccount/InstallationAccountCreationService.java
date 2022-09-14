package uk.gov.pmrv.api.migration.installationaccount;

import java.util.List;
import java.util.Optional;

import lombok.AllArgsConstructor;

import org.springframework.boot.actuate.autoconfigure.endpoint.condition.ConditionalOnAvailableEndpoint;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uk.gov.pmrv.api.account.domain.Account;
import uk.gov.pmrv.api.account.domain.dto.AccountDTO;
import uk.gov.pmrv.api.account.domain.enumeration.AccountStatus;
import uk.gov.pmrv.api.account.repository.AccountRepository;
import uk.gov.pmrv.api.account.service.AccountCreationService;
import uk.gov.pmrv.api.common.domain.enumeration.CompetentAuthority;
import uk.gov.pmrv.api.common.domain.enumeration.RoleType;
import uk.gov.pmrv.api.common.domain.model.PmrvAuthority;
import uk.gov.pmrv.api.common.domain.model.PmrvUser;
import uk.gov.pmrv.api.migration.MigrationEndpoint;
import uk.gov.pmrv.api.migration.MigrationHelper;

@Service
@AllArgsConstructor
@ConditionalOnAvailableEndpoint(endpoint = MigrationEndpoint.class)
class InstallationAccountCreationService {

    private final AccountCreationService accountCreationService;
    private final AccountRepository accountRepository;

    @Transactional
    public Long createAccount(AccountDTO accountDTO, Emitter emitter) throws Exception {
        PmrvUser authUser = buildAuthUser(accountDTO.getCompetentAuthority());

        AccountDTO createdAccountDTO = accountCreationService.createAccount(accountDTO, authUser);
        
        Optional<Account> accountOpt = accountRepository.findById(createdAccountDTO.getId());
        if(accountOpt.isEmpty()) {
            throw new Exception(String.format("Account for emitter id %s failed to persist in PMRV", emitter.getId()));
        } 
        
        Account account = accountOpt.get();
        account.setMigratedAccountId(emitter.getId());
        account.setSopId(emitter.getSopId());
        populateStatus(account, emitter.getStatus());
        
        accountRepository.save(account);
        return account.getId();
    }

    private void populateStatus(Account account, String etsStatus) {
        //TODO: Correlate all ETSWAP Installation Account status values with the PMRV ones
        AccountStatus accountStatus = MigrationHelper.resolveAccountStatus(etsStatus);
        if(accountStatus != null) {
            account.setStatus(accountStatus);
        }
    }

    private PmrvUser buildAuthUser(CompetentAuthority ca) {
        PmrvUser authUser = new PmrvUser();
        authUser.setRoleType(RoleType.REGULATOR);
        authUser.setAuthorities(
            List.of(PmrvAuthority.builder().competentAuthority(ca).build()));
        return authUser;
    }

}
