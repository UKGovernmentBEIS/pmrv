package uk.gov.pmrv.api.account.service;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.pmrv.api.account.domain.Account;
import uk.gov.pmrv.api.account.domain.dto.AccountContactInfoDTO;
import uk.gov.pmrv.api.account.domain.enumeration.AccountContactType;
import uk.gov.pmrv.api.account.repository.AccountRepository;
import uk.gov.pmrv.api.common.exception.BusinessCheckedException;

@Service
@RequiredArgsConstructor
public class AccountContactQueryService {

    private final AccountRepository accountRepository;

    public String findPrimaryContactByAccount(Long accountId) throws BusinessCheckedException {
        Optional<String> userOpt = findContactByAccountAndContactType(accountId, AccountContactType.PRIMARY);
        if (userOpt.isEmpty()) {
            throw new BusinessCheckedException("Primary contact for account id: %d is missing" + accountId);
        } else {
            return userOpt.get();
        }
    }

    public Optional<String> findContactByAccountAndContactType(Long accountId, AccountContactType accountContactType) {
        Optional<Account> accountOpt = accountRepository.findById(accountId);
        return accountOpt
            .map(Account::getContacts)
            .map(contacts -> contacts.get(accountContactType));
    }

    public List<AccountContactInfoDTO> findContactsByAccountIdsAndContactType(Set<Long> accountIds, AccountContactType accountContactType) {
        return accountRepository
            .findAccountContactsByAccountIdsAndContactType(new ArrayList<>(accountIds), accountContactType);
    }

    @Transactional(readOnly = true)
    public Map<AccountContactType, String> findContactTypesByAccount(Long accountId) {
        Optional<Account> accountOpt = accountRepository.findById(accountId);
        if (accountOpt.isPresent() && !accountOpt.get().getContacts().isEmpty()) {
            return new EnumMap<>(accountOpt.get().getContacts());
        }
        return Map.of();
    }

    @Transactional(readOnly = true)
    public Map<Long, Map<AccountContactType, String>> findContactTypesByAccountIds(List<Long> accountIds) {
        return accountRepository
            .findAllByIdIn(accountIds)
            .stream()
            .collect(Collectors.toMap(Account::getId, Account::getContacts));
    }

    @Transactional(readOnly = true)
    public Map<AccountContactType, String> findOperatorContactTypesByAccount(Long accountId) {
        Map<AccountContactType, String> contactTypesByAccount = findContactTypesByAccount(accountId);
        return contactTypesByAccount.entrySet().stream()
            .filter(accountContactType -> AccountContactType.getOperatorAccountContactTypes().contains(accountContactType.getKey()))
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }
}
