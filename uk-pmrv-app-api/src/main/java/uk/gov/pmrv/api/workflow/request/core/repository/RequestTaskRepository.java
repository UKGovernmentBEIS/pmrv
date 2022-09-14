package uk.gov.pmrv.api.workflow.request.core.repository;

import static uk.gov.pmrv.api.workflow.request.core.domain.RequestTask.NAMED_ENTITY_GRAPH_REQUEST_TASK_REQUEST;

import java.util.List;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestStatus;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;

@Repository
public interface RequestTaskRepository extends JpaRepository<RequestTask, Long> {

    @Transactional(readOnly = true)
    RequestTask findByProcessTaskId(String processTaskId);

    @Transactional(readOnly = true)
    List<RequestTask> findByRequestTypeAndAssignee(
            RequestType requestType, String assignee);
    
    @Transactional(readOnly = true)
    @EntityGraph(value = NAMED_ENTITY_GRAPH_REQUEST_TASK_REQUEST, type = EntityGraph.EntityGraphType.FETCH)
    List<RequestTask> findByAssigneeAndRequestStatus(
            String assignee, RequestStatus requestStatus);

    @Transactional(readOnly = true)
    List<RequestTask> findByRequestTypeAndAssigneeAndRequestAccountId(
        RequestType requestType, String assignee, Long accountId);

    @Transactional(readOnly = true)
    List<RequestTask> findByAssigneeAndRequestAccountIdAndRequestStatus(
        String assignee, Long accountId, RequestStatus requestStatus);

    @Transactional(readOnly = true)
    List<RequestTask> findByRequestId(String requestId);
}
