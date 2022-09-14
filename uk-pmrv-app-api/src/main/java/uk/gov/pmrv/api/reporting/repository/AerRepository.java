package uk.gov.pmrv.api.reporting.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import uk.gov.pmrv.api.reporting.domain.AerEntity;

@Repository
public interface AerRepository extends JpaRepository<AerEntity, String> {
}
