package uk.gov.pmrv.api.account.service;

import static uk.gov.pmrv.api.common.exception.ErrorCode.RESOURCE_NOT_FOUND;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.account.domain.Account;
import uk.gov.pmrv.api.account.domain.dto.AccountDTO;
import uk.gov.pmrv.api.account.domain.dto.AccountDetailsDTO;
import uk.gov.pmrv.api.account.domain.dto.AccountHeaderInfoDTO;
import uk.gov.pmrv.api.account.domain.dto.AccountSearchCriteria;
import uk.gov.pmrv.api.account.domain.dto.AccountSearchResults;
import uk.gov.pmrv.api.account.domain.enumeration.AccountStatus;
import uk.gov.pmrv.api.account.repository.AccountRepository;
import uk.gov.pmrv.api.account.transform.AccountMapper;
import uk.gov.pmrv.api.authorization.rules.services.authorityinfo.providers.AccountAuthorityInfoProvider;
import uk.gov.pmrv.api.common.domain.enumeration.CompetentAuthority;
import uk.gov.pmrv.api.common.domain.model.PmrvUser;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;

@Service
@RequiredArgsConstructor
public class AccountQueryService implements AccountAuthorityInfoProvider {

    private final AccountRepository accountRepository;

    private final AccountMapper accountMapper;

    public List<AccountDTO> getAccountsByStatus(AccountStatus status) {
        return accountRepository.findAllByStatusIs(status).stream()
                .map(accountMapper::toAccountDTO)
                .collect(Collectors.toList());
    }

    Account getAccountById(Long accountId) {
        return accountRepository.findById(accountId)
                .orElseThrow(() -> new BusinessException(RESOURCE_NOT_FOUND));
    }

    Account getAccountByIdForUpdate(final Long accountId) {
        return accountRepository.findByIdForUpdate(accountId)
                .orElseThrow(() -> new BusinessException(RESOURCE_NOT_FOUND));
    }

    @Override
    public CompetentAuthority getAccountCa(Long accountId) {
        return getAccountById(accountId).getCompetentAuthority();
    }

    public String getAccountInstallationName(Long accountId) {
        return getAccountById(accountId).getName();
    }

    public AccountStatus getAccountStatus(Long accountId) {
        return getAccountById(accountId).getStatus();
    }

    public void exclusiveLockAccount(final Long accountId) {
        this.getAccountByIdForUpdate(accountId);
    }

    @Override
    public Optional<Long> getAccountVerificationBodyId(Long accountId) {
        return Optional.ofNullable(getAccountById(accountId).getVerificationBodyId());
    }

    public AccountDetailsDTO getAccountDetailsDTOById(Long accountId) {
        return accountRepository.findAccountById(accountId)
                .map(accountMapper::toAccountDetailsDTO)
                .orElseThrow(() -> new BusinessException(RESOURCE_NOT_FOUND));
    }

    public Optional<Account> getApprovedAccountById(Long accountId) {
        return accountRepository.findByIdAndStatusNotIn(accountId,
                List.of(AccountStatus.UNAPPROVED, AccountStatus.DENIED));
    }

    public AccountDTO getAccountDTOById(Long accountId) {
        return accountMapper.toAccountDTO(getAccountById(accountId));
    }

    public List<AccountDTO> getAccountsByOperatorUser(PmrvUser user) {
        return accountRepository.findAllEagerLeByIdIn(List.copyOf(user.getAccounts()))
                .stream()
                .map(accountMapper::toAccountDTO)
                .collect(Collectors.toList());
    }

    public List<AccountDTO> getAccountsByCompetentAuthorityAndIds(CompetentAuthority competentAuthority, List<Long> accountIds) {
        return accountRepository.findByAccountIdsAndCompAuth(accountIds, competentAuthority)
                .stream()
                .map(accountMapper::toAccountDTO)
                .collect(Collectors.toList());
    }

    public List<AccountDTO> getAccountsByVerifierUserIdAndIds(PmrvUser verifierUser, List<Long> accountIds) {
        return accountRepository.findByAccountIdsAndVerBody(accountIds, verifierUser.getVerificationBodyId())
                .stream()
                .map(accountMapper::toAccountDTO)
                .collect(Collectors.toList());
    }

    public List<AccountDTO> getAccountsByCompetentAuthority(CompetentAuthority competentAuthority) {
        return accountRepository.findAllByCompetentAuthority(competentAuthority)
            .stream()
            .map(accountMapper::toAccountDTO)
            .collect(Collectors.toList());
    }

    public AccountSearchResults getAccountsByUserAndSearchCriteria(PmrvUser user,
                                                                   AccountSearchCriteria searchCriteria) {
        final AccountSearchResults accounts;
        switch (user.getRoleType()) {
            case OPERATOR:
                accounts = accountRepository.findByAccountIds(List.copyOf(user.getAccounts()), searchCriteria);
                break;
            case REGULATOR:
                accounts = accountRepository.findByCompAuth(user.getCompetentAuthority(), searchCriteria);
                break;
            default:
                throw new UnsupportedOperationException(
                        String.format("Fetching accounts for role type %s is not supported", user.getRoleType()));
        }
        return accounts;
    }

    public Optional<AccountHeaderInfoDTO> getAccountHeaderInfoById(Long accountId) {
        return getApprovedAccountById(accountId)
                .map(accountMapper::toAccountHeaderInfoDTO);
    }

    public boolean isExistingActiveAccountName(String accountName) {
        return false;
    }

    void validateAccountName(String accountName) {
        if (isExistingActiveAccountName(accountName)) {
            throw new BusinessException(ErrorCode.ACCOUNT_ALREADY_EXISTS);
        }
    }

    boolean isLegalEntityUnused(Long legalEntityId) {
        return accountRepository.countByLegalEntityId(legalEntityId) == 0;
    }
}
