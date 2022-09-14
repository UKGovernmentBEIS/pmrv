package uk.gov.pmrv.api.referencedata.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import uk.gov.pmrv.api.referencedata.domain.Country;

import java.util.Optional;

/**
 * Repository for {@link Country} objects.
 */
@Repository
public interface CountryRepository extends JpaRepository<Country, Long> {
    @Query("select c from Country c where LOWER(c.name) = LOWER(?1)")
    Optional<Country> findByName(String name);
}
