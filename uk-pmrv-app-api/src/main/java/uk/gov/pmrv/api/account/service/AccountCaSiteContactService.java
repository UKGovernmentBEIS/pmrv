package uk.gov.pmrv.api.account.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
import uk.gov.pmrv.api.common.domain.model.PmrvUser;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AccountCaSiteContactService {

    private final AccountRepository accountRepository;
    private final AccountContactQueryService accountContactQueryService;
    private final CompAuthAuthorizationResourceService compAuthAuthorizationResourceService;
    private final RegulatorAuthorityResourceService regulatorAuthorityResourceService;

    public Optional<String> findCASiteContactByAccount(Long accountId) {
       return accountContactQueryService.findContactByAccountAndContactType(accountId, AccountContactType.CA_SITE);
    }
    
    public AccountContactInfoResponse getAccountsAndCaSiteContacts(PmrvUser pmrvUser, Integer page, Integer pageSize) {
        CompetentAuthority ca = pmrvUser.getCompetentAuthority();

        Page<AccountContactInfoDTO> contacts = 
                accountRepository.findApprovedAccountContactsByCaAndContactType(PageRequest.of(page, pageSize), ca, AccountContactType.CA_SITE);

        // Check if user has the permission of editing account contacts assignees
        boolean isEditable = compAuthAuthorizationResourceService.hasUserScopeToCompAuth(pmrvUser, ca, Scope.EDIT_USER);

        // Transform properly
        return AccountContactInfoResponse.builder()
                    .contacts(contacts.get().collect(Collectors.toList()))
                    .totalItems(contacts.getTotalElements())
                    .editable(isEditable)
                    .build();
    }
    
    @Transactional
    public void removeUserFromCaSiteContact(String userId) {
        List<Account> accounts = accountRepository.findAccountsByContactTypeAndUserId(AccountContactType.CA_SITE, userId);
        accounts
            .forEach(ac -> {
                ac.getContacts().remove(AccountContactType.CA_SITE);
                accountRepository.save(ac);
            });
    }

    @Transactional
    public void updateCaSiteContacts(PmrvUser pmrvUser, List<AccountContactDTO> caSiteContacts) {
        CompetentAuthority ca = pmrvUser.getCompetentAuthority();

        // Validate accounts belonging to CA
        Set<Long> accountIds = 
                caSiteContacts.stream()
                        .map(AccountContactDTO::getAccountId)
                        .collect(Collectors.toSet());
        validateAccountsByCA(accountIds, ca);

        // Validate users belonging to CA
        Set<String> userIds = caSiteContacts.stream()
                .map(AccountContactDTO::getUserId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        validateUsersByCA(userIds, ca);

        // Update contacts in DB
        doUpdateCaSiteContacts(caSiteContacts);
    }

    private void doUpdateCaSiteContacts(List<AccountContactDTO> caSiteContactsUpdate) {
        List<Long> accountIdsUpdate = 
                caSiteContactsUpdate.stream()
                        .map(AccountContactDTO::getAccountId)
                        .collect(Collectors.toList());
        List<Account> accounts = accountRepository.findAllByIdIn(accountIdsUpdate);
        
        caSiteContactsUpdate
            .forEach(contact -> accounts.stream()
                .filter(ac -> ac.getId().equals(contact.getAccountId()))
                .findFirst()
                .ifPresent(ac -> {
                    ac.getContacts().put(AccountContactType.CA_SITE, contact.getUserId());
                    accountRepository.save(ac);
                }));
    }

    /** Validates that account exists and belongs to CA */
    private void validateAccountsByCA(Set<Long> accountIds, CompetentAuthority ca) {
        List<Long> accounts = accountRepository.findAllApprovedIdsByCA(ca);

        if(!accounts.containsAll(accountIds)){
            throw new BusinessException(ErrorCode.ACCOUNT_NOT_RELATED_TO_CA);
        }
    }

    /** Validates that user exists and belongs to CA */
    private void validateUsersByCA(Set<String> userIds, CompetentAuthority ca) {
        List<String> users = regulatorAuthorityResourceService.findUsersByCompetentAuthority(ca);

        if(!users.containsAll(userIds)){
            throw new BusinessException(ErrorCode.AUTHORITY_USER_NOT_RELATED_TO_CA);
        }
    }
}
