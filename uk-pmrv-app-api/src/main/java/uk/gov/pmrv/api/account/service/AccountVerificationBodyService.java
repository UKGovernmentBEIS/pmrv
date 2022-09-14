package uk.gov.pmrv.api.account.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.account.domain.Account;
import uk.gov.pmrv.api.verificationbody.domain.dto.VerificationBodyNameInfoDTO;
import uk.gov.pmrv.api.verificationbody.service.VerificationBodyQueryService;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class AccountVerificationBodyService {

    private final AccountQueryService accountQueryService;
    private final VerificationBodyQueryService verificationBodyQueryService;
    
    public Optional<VerificationBodyNameInfoDTO> getVerificationBodyNameInfoByAccount(Long accountId) {
        return accountQueryService
                .getAccountVerificationBodyId(accountId)
                .map(verificationBodyQueryService::getVerificationBodyNameInfoById);
    }
    
    public List<VerificationBodyNameInfoDTO> getAllActiveVerificationBodiesAccreditedToAccountEmissionTradingScheme(Long accountId){
        Account account = accountQueryService.getAccountById(accountId);
        return verificationBodyQueryService.getAllActiveVerificationBodiesAccreditedToEmissionTradingScheme(account.getEmissionTradingScheme());
    }
    
}
