package uk.gov.pmrv.api.account.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.pmrv.api.account.domain.Account;
import uk.gov.pmrv.api.account.domain.enumeration.AccountContactType;
import uk.gov.pmrv.api.account.repository.AccountRepository;
import uk.gov.pmrv.api.account.service.validator.AccountContactTypeUpdateValidator;
import uk.gov.pmrv.api.authorization.core.domain.AuthorityStatus;
import uk.gov.pmrv.api.authorization.core.service.AuthorityService;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;

import java.util.Collection;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AccountContactUpdateService {
    
    private final AccountRepository accountRepository;
    private final AuthorityService authorityService;
    private final List<AccountContactTypeUpdateValidator> contactTypeValidators;
    
    @Transactional
    public void assignUserAsDefaultAccountContactPoint(String user, Account account) {
        account.getContacts().put(AccountContactType.PRIMARY, user);
        account.getContacts().put(AccountContactType.SERVICE, user);
        account.getContacts().put(AccountContactType.FINANCIAL, user);
        
        accountRepository.save(account);
    }

    @Transactional
    public void updateAccountContacts(Map<AccountContactType, String> updatedContactTypes, Long accountId) {
        validateUpdatedContactTypeUsers(updatedContactTypes.values(), accountId);
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND));
        
        Map<AccountContactType, String> allContactTypes = constructAllContactTypes(updatedContactTypes, account);
        
        //validate
        contactTypeValidators.forEach(v -> v.validateUpdate(allContactTypes, accountId));
        
        //save
        account.setContacts(allContactTypes);
        accountRepository.save(account);
    }
    
    private void validateUpdatedContactTypeUsers(Collection<String> users, Long accountId) {
        users.forEach(u -> validateUpdatedContactTypeUser(u, accountId));
    }
    
    private void validateUpdatedContactTypeUser(String user, Long accountId) {
        if(user == null) {
            return;
        }
        
        if (!authorityService.existsByUserIdAndAccountId(user, accountId)) {
            throw new BusinessException(ErrorCode.AUTHORITY_USER_NOT_RELATED_TO_ACCOUNT, user);
        }
    }

    private Map<AccountContactType, String> constructAllContactTypes(Map<AccountContactType, String> updatedContactTypes,
            Account account) {
        Map<AccountContactType, String> currentContactTypes = account.getContacts();
        
        Map<AccountContactType, String> notUpdatedContactTypes = new EnumMap<>(AccountContactType.class);
        currentContactTypes.forEach((key, value) -> {
            if (!updatedContactTypes.containsKey(key)) {
                notUpdatedContactTypes.put(key, value);
            }
        });
        
        Map<AccountContactType, String> allContactTypes = new EnumMap<>(AccountContactType.class);
        allContactTypes.putAll(updatedContactTypes);
        allContactTypes.putAll(notUpdatedContactTypes);
        
        List<String> users = allContactTypes.values().stream().filter(Objects::nonNull).collect(Collectors.toList());
        Map<String, AuthorityStatus> userStatuses = authorityService.findStatusByUsersAndAccountId(users, account.getId());
        
        nullifyValuesForNonActiveUsers(allContactTypes, userStatuses);
        return allContactTypes;
    }
    
    private void nullifyValuesForNonActiveUsers(
            Map<AccountContactType, String> allContactTypes, Map<String, AuthorityStatus> userStatuses) {
        allContactTypes.entrySet().forEach(e -> {
            String user = e.getValue();
            if(user != null && 
                    userStatuses.get(user) != AuthorityStatus.ACTIVE) {
                e.setValue(null);
            }
        });
    }
}
