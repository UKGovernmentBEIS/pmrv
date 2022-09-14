package uk.gov.pmrv.api.workflow.request.core.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestSequence;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;

public interface RequestSequenceRepository extends JpaRepository<RequestSequence, Long> {
    @Query(value = "select nextval('request_seq')", nativeQuery = true)
    Long getNextSequenceValue();

    Optional<RequestSequence> findByAccountIdAndType(Long accountId, RequestType type);
}
