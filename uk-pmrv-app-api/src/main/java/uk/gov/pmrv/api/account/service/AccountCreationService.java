package uk.gov.pmrv.api.account.service;

import lombok.RequiredArgsConstructor;

import javax.validation.Valid;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import uk.gov.pmrv.api.account.domain.Account;
import uk.gov.pmrv.api.account.domain.LegalEntity;
import uk.gov.pmrv.api.account.domain.dto.AccountDTO;
import uk.gov.pmrv.api.account.domain.enumeration.AccountStatus;
import uk.gov.pmrv.api.account.repository.AccountRepository;
import uk.gov.pmrv.api.account.transform.AccountMapper;
import uk.gov.pmrv.api.common.domain.model.PmrvUser;

@Validated
@Service
@RequiredArgsConstructor
public class AccountCreationService {

    private final AccountRepository accountRepository;
    private final AccountIdentifierService accountIdentifierService;
    private final LegalEntityService legalEntityService;
    private final AccountMapper accountMapper;
    private final AccountQueryService accountQueryService;

    @Transactional
    public AccountDTO createAccount(@Valid AccountDTO accountDTO, PmrvUser pmrvUser) {
        accountQueryService.validateAccountName(accountDTO.getName());

        final LegalEntity legalEntity = legalEntityService.resolveLegalEntity(accountDTO.getLegalEntity(), pmrvUser);
        final Long identifier = accountDTO.getId() != null ? accountDTO.getId()
                : accountIdentifierService.incrementAndGet();

        // Create account
        Account account = accountMapper.toAccount(accountDTO, identifier);
        account.setStatus(AccountStatus.UNAPPROVED);
        account.setLegalEntity(legalEntity);
        Account persistedAccount = accountRepository.save(account);

        return accountMapper.toAccountDTO(persistedAccount);
    }
    
}
