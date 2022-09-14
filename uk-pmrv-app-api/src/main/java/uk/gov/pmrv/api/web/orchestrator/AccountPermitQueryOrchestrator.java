package uk.gov.pmrv.api.web.orchestrator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.account.domain.dto.AccountDetailsDTO;
import uk.gov.pmrv.api.account.domain.dto.AccountHeaderInfoDTO;
import uk.gov.pmrv.api.account.service.AccountQueryService;
import uk.gov.pmrv.api.permit.service.PermitQueryService;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AccountPermitQueryOrchestrator {

    private final AccountQueryService accountQueryService;
    private final PermitQueryService permitQueryService;

    public AccountDetailsDTO getAccountDetailsDtoWithPermit(Long accountId) {
        AccountDetailsDTO accountDetails = accountQueryService.getAccountDetailsDTOById(accountId);
        accountDetails.setPermitId(permitQueryService.getPermitIdByAccountId(accountId).orElse(null));
        return accountDetails;
    }

    public Optional<AccountHeaderInfoDTO> getAccountHeaderInfoWithPermitId(Long accountId) {
        Optional<AccountHeaderInfoDTO> accountHeaderInfo = accountQueryService.getAccountHeaderInfoById(accountId);
        accountHeaderInfo.ifPresent(headerInfo -> headerInfo.setPermitId(permitQueryService.getPermitIdByAccountId(accountId).orElse(null)));
        return accountHeaderInfo;
    }
}
