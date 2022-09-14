package uk.gov.pmrv.api.account.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import uk.gov.pmrv.api.account.domain.Account;
import uk.gov.pmrv.api.account.domain.LegalEntity;
import uk.gov.pmrv.api.account.domain.dto.AccountDTO;
import uk.gov.pmrv.api.account.repository.AccountRepository;
import uk.gov.pmrv.api.authorization.operator.service.OperatorAuthorityService;

import javax.validation.Valid;
import java.time.LocalDateTime;

@Validated
@Service
@RequiredArgsConstructor
public class AccountActivationService {

    private final AccountRepository accountRepository;
    private final LegalEntityService legalEntityService;
    private final OperatorAuthorityService operatorauthorityService;
    private final AccountQueryService accountQueryService;
    private final AccountContactUpdateService accountContactUpdateService;
    private final AccountStatusService accountStatusService;

    @Transactional
    public void activateAccount(Long accountId, @Valid AccountDTO accountDTO, String user) {
        accountQueryService.validateAccountName(accountDTO.getName());
        Account account = accountQueryService.getAccountById(accountId);

        // Activate LE
        LegalEntity legalEntity = legalEntityService.activateLegalEntity(accountDTO.getLegalEntity());

        // Update account
        account.setLegalEntity(legalEntity);
        account.setAcceptedDate(LocalDateTime.now());
        accountRepository.save(account);

        // Activate account
        accountStatusService.handleInstallationAccountAccepted(accountId);

        // Create operator admin authorities for the created account
        operatorauthorityService.createOperatorAdminAuthority(account.getId(), user);
        
        accountContactUpdateService.assignUserAsDefaultAccountContactPoint(user, account);
    }
}
