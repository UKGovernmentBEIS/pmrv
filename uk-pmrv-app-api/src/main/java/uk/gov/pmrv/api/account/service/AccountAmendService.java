package uk.gov.pmrv.api.account.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import uk.gov.pmrv.api.account.domain.Account;
import uk.gov.pmrv.api.account.domain.LegalEntity;
import uk.gov.pmrv.api.account.domain.dto.AccountDTO;
import uk.gov.pmrv.api.account.repository.AccountRepository;
import uk.gov.pmrv.api.account.transform.AccountMapper;
import uk.gov.pmrv.api.common.domain.model.PmrvUser;
import uk.gov.pmrv.api.common.exception.BusinessException;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static uk.gov.pmrv.api.common.exception.ErrorCode.ACCOUNT_FIELD_NOT_AMENDABLE;

@Validated
@Service
@RequiredArgsConstructor
public class AccountAmendService {

    private final AccountRepository accountRepository;

    private final LegalEntityService legalEntityService;

    private final AccountMapper accountMapper;

    private final AccountQueryService accountQueryService;

    @Transactional
    public AccountDTO amendAccount(Long accountId, @Valid AccountDTO previousAccountDTO, @Valid AccountDTO newAccountDTO,
                             PmrvUser pmrvUser) {
        validateNonAmendableAccountFields(previousAccountDTO, newAccountDTO);
        validateAccountName(previousAccountDTO, newAccountDTO);

        Account account = accountQueryService.getAccountById(accountId);

        LegalEntity legalEntity = legalEntityService.getLegalEntityById(account.getLegalEntity().getId());

        final LegalEntity newLegalEntity = 
                legalEntityService.resolveAmendedLegalEntity(newAccountDTO.getLegalEntity(), legalEntity, pmrvUser);

        //update account
        Account newAccount = accountMapper.toAccount(newAccountDTO, account.getId());
        newAccount.setStatus(account.getStatus());
        newAccount.getLocation().setId(account.getLocation().getId());
        newAccount.setLegalEntity(newLegalEntity);
        Account persistedAccount = accountRepository.save(newAccount);

        //delete current LE if not used anymore by any account
        if (accountQueryService.isLegalEntityUnused(legalEntity.getId())) {
            legalEntityService.deleteLegalEntity(legalEntity);
        }
        
        return accountMapper.toAccountDTO(persistedAccount);
    }

    private void validateNonAmendableAccountFields(AccountDTO previousAccountDTO, AccountDTO newAccountDTO) {
        List<String> errors = new ArrayList<>();

        if (!previousAccountDTO.getCommencementDate().equals(newAccountDTO.getCommencementDate())) {
            errors.add("commencementDate");
        }

        if (!previousAccountDTO.getCompetentAuthority().equals(newAccountDTO.getCompetentAuthority())) {
            errors.add("competentAuthority");
        }

        if (!previousAccountDTO.getLocation().getType().equals(newAccountDTO.getLocation().getType())) {
            errors.add("location.type");
        }

        if (!errors.isEmpty()) {
            throw new BusinessException(ACCOUNT_FIELD_NOT_AMENDABLE, errors.toArray());
        }
    }

    private void validateAccountName(AccountDTO previousAccountDTO, AccountDTO newAccountDTO) {
        if (!Objects.equals(previousAccountDTO.getName(), newAccountDTO.getName())) {
            accountQueryService.validateAccountName(newAccountDTO.getName());
        }
    }
}
