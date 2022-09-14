package uk.gov.pmrv.api.account.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.pmrv.api.account.domain.AccountSearchAdditionalKeyword;
import uk.gov.pmrv.api.account.repository.AccountSearchAdditionalKeywordRepository;

@Service
@RequiredArgsConstructor
public class AccountSearchAdditionalKeywordService {

    private final AccountSearchAdditionalKeywordRepository keywordRepository;

    @Transactional
    public void storeKeywordForAccount(String keywordValue, Long accountId) {
        AccountSearchAdditionalKeyword keyword = AccountSearchAdditionalKeyword.builder()
            .accountId(accountId)
            .value(keywordValue)
            .build();
        keywordRepository.save(keyword);
    }
}
