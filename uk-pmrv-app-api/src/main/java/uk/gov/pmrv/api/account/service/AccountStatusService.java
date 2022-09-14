package uk.gov.pmrv.api.account.service;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.pmrv.api.account.domain.Account;
import uk.gov.pmrv.api.account.domain.enumeration.AccountStatus;
import uk.gov.pmrv.api.account.repository.AccountRepository;
import uk.gov.pmrv.api.common.domain.AccountStatusChangedEvent;

@Service
@RequiredArgsConstructor
public class AccountStatusService {

    private final AccountQueryService accountQueryService;
    private final AccountRepository accountRepository;
    private final ApplicationEventPublisher publisher;

    @Transactional
    @uk.gov.pmrv.api.account.service.validator.AccountStatus(expression = "{#status == 'NEW'}")
    public void handlePermitGranted(final Long accountId) {
        updateAccountStatus(accountId, AccountStatus.LIVE);
    }

    @Transactional
    @uk.gov.pmrv.api.account.service.validator.AccountStatus(expression = "{#status == 'NEW'}")
    public void handlePermitRejected(final Long accountId) {
        updateAccountStatus(accountId, AccountStatus.PERMIT_REFUSED);
    }

    @Transactional
    @uk.gov.pmrv.api.account.service.validator.AccountStatus(expression = "{#status == 'NEW'}")
    public void handlePermitDeemedWithdrawn(final Long accountId) {
        updateAccountStatus(accountId, AccountStatus.DEEMED_WITHDRAWN);
    }

    @Transactional
    @uk.gov.pmrv.api.account.service.validator.AccountStatus(expression = "{#status == 'LIVE'}")
    public void handlePermitSurrenderGranted(final Long accountId) {
        updateAccountStatus(accountId, AccountStatus.AWAITING_SURRENDER);
    }

    @Transactional
    @uk.gov.pmrv.api.account.service.validator.AccountStatus(expression = "{#status == 'AWAITING_SURRENDER'}")
    public void handleSurrenderCessationCompleted(final Long accountId) {
        updateAccountStatus(accountId, AccountStatus.SURRENDERED);
    }
    
    @Transactional
    @uk.gov.pmrv.api.account.service.validator.AccountStatus(expression = "{#status == 'LIVE'}")
    public void handlePermitRevoked(final Long accountId) {
        updateAccountStatus(accountId, AccountStatus.AWAITING_REVOCATION);
    }

    @Transactional
    @uk.gov.pmrv.api.account.service.validator.AccountStatus(expression = "{#status == 'AWAITING_REVOCATION'}")
    public void handleRevocationCessationCompleted(final Long accountId) {
        updateAccountStatus(accountId, AccountStatus.REVOKED);
    }

    @Transactional
    @uk.gov.pmrv.api.account.service.validator.AccountStatus(expression = "{#status == 'UNAPPROVED'}")
    public void handleInstallationAccountAccepted(final Long accountId) {
        updateAccountStatus(accountId, AccountStatus.NEW);
    }

    @Transactional
    @uk.gov.pmrv.api.account.service.validator.AccountStatus(expression = "{#status == 'UNAPPROVED'}")
    public void handleInstallationAccountRejected(final Long accountId) {
        updateAccountStatus(accountId, AccountStatus.DENIED);
    }

    private void updateAccountStatus(final Long accountId, final AccountStatus newStatus) {
        Account account = accountQueryService.getAccountById(accountId);

        account.setStatus(newStatus);
        accountRepository.save(account);

        publisher.publishEvent(AccountStatusChangedEvent.builder()
            .accountId(accountId)
            .status(newStatus)
            .build());
    }
}
