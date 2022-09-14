package uk.gov.pmrv.api.account.repository;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import uk.gov.pmrv.api.account.domain.dto.AccountSearchCriteria;
import uk.gov.pmrv.api.account.domain.dto.AccountSearchResults;
import uk.gov.pmrv.api.common.domain.enumeration.CompetentAuthority;

public interface AccountCustomRepository {

    @Transactional(readOnly = true)
    AccountSearchResults findByAccountIds(List<Long> accountIds, AccountSearchCriteria searchCriteria);
    
    @Transactional(readOnly = true)
    AccountSearchResults findByCompAuth(CompetentAuthority compAuth, AccountSearchCriteria searchCriteria);
    
}
