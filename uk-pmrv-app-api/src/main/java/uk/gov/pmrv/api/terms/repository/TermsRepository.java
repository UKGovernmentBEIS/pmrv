package uk.gov.pmrv.api.terms.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import uk.gov.pmrv.api.terms.domain.Terms;

/**
 * Data repository for terms and conditions.
 */
public interface TermsRepository extends JpaRepository<Terms, Long> {

    @Query("select t from Terms t where t.version = (SELECT MAX(tt.version) from Terms tt)")
    Terms findLatestTerms();

}
