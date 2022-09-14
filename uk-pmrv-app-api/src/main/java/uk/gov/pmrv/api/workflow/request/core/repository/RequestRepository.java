package uk.gov.pmrv.api.workflow.request.core.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestStatus;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;

@Repository
public interface RequestRepository extends JpaRepository<Request, String> {

    @Override
    @EntityGraph(value = "fetchRequestActions", type = EntityGraph.EntityGraphType.FETCH)
    Optional<Request> findById(String id);

    @Query(name = Request.NAMED_QUERY_FIND_BY_ACCOUNT_ID_AND_STATUS_AND_TYPE_NOT_NOTIFICATION)
    List<Request> findByAccountIdAndStatusAndTypeNotNotification(Long accountId, RequestStatus status);

    @Modifying
    @Query(name = Request.NAMED_QUERY_UPDATE_VERIFICATION_BODY_BY_ACCOUNT_ID)
    void updateVerificationBodyIdByAccountId(Long verificationBodyId, Long accountId);

    @Transactional(readOnly = true)
    List<Request> findByAccountIdAndTypeInAndStatus(Long accountId, List<RequestType> types, RequestStatus status);

}
