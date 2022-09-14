package uk.gov.pmrv.api.workflow.payment.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.pmrv.api.common.domain.enumeration.CompetentAuthority;
import uk.gov.pmrv.api.workflow.payment.domain.BankAccountDetails;

@Repository
public interface BankAccountDetailsRepository extends JpaRepository<BankAccountDetails, Long> {

    @Transactional(readOnly = true)
    Optional<BankAccountDetails> findByCompetentAuthority(CompetentAuthority competentAuthority);
}
