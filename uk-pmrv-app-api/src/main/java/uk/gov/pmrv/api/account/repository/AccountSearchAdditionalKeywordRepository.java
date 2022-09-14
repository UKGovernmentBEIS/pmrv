package uk.gov.pmrv.api.account.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.gov.pmrv.api.account.domain.AccountSearchAdditionalKeyword;

public interface AccountSearchAdditionalKeywordRepository extends JpaRepository<AccountSearchAdditionalKeyword, Long> {
}
