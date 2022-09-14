package uk.gov.pmrv.api.account.service;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import uk.gov.pmrv.api.account.domain.Account;
import uk.gov.pmrv.api.account.domain.LegalEntity;

@Service
@RequiredArgsConstructor
public class AccountRejectionService {

    private final AccountStatusService accountStatusService;
    private final LegalEntityService legalEntityService;
    private final AccountQueryService accountQueryService;

    public void rejectAccount(Long accountId) {
        Account account = accountQueryService.getAccountById(accountId);
        LegalEntity legalEntity = legalEntityService.getLegalEntityById(account.getLegalEntity().getId());

        // Change Account status to DENIED
        accountStatusService.handleInstallationAccountRejected(accountId);

        // Change LE status to DENIED
        legalEntityService.handleLegalEntityDenied(legalEntity);
    }
}
