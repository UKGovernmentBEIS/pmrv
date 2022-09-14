package uk.gov.pmrv.api.permit.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.account.domain.Account;
import uk.gov.pmrv.api.account.service.AccountQueryService;
import uk.gov.pmrv.api.common.exception.BusinessException;

import static uk.gov.pmrv.api.common.exception.ErrorCode.RESOURCE_NOT_FOUND;

/**
 * Generates permit identifier according to specs:
 * <br/>
 * https://pmo.trasys.be/confluence/display/PMRV/Permit+Id
 */
@Service
@RequiredArgsConstructor
public class PermitIdentifierGenerator {

    private static final String UK = "UK";

    private final AccountQueryService accountQueryService;

    String generate(Long accountId) {
        Account account = accountQueryService.getApprovedAccountById(accountId)
            .orElseThrow(() -> new BusinessException(RESOURCE_NOT_FOUND));

        String countryCode = UK;
        String authorityCode = account.getCompetentAuthority().getOneLetterCode();
        String typeCode = account.getAccountType().getCode();
        String accountIdFormatted = String.format("%05d", account.getId());

        return String.format("%s-%s-%s-%s", countryCode, authorityCode, typeCode, accountIdFormatted);
    }
}
