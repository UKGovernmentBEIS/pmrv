package uk.gov.pmrv.api.workflow.request.core.service;

import static uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType.PERMIT_REVOCATION;
import static uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType.PERMIT_SURRENDER;
import static uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType.PERMIT_TRANSFER;
import static uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType.PERMIT_VARIATION;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.pmrv.api.account.domain.enumeration.AccountStatus;
import uk.gov.pmrv.api.workflow.request.WorkflowService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestStatus;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.core.repository.RequestRepository;

@Service
@RequiredArgsConstructor
public class ParallelRequestHandler {

    private static final String DELETE_REASON = "Workflow terminated by the system";

    private final RequestRepository requestRepository;
    private final WorkflowService workflowService;
    private final RequestService requestService;

    @Transactional
    public void handleParallelRequests(Long accountId, AccountStatus status) {
        switch (status) {
            case AWAITING_SURRENDER:
                handle(accountId, List.of(PERMIT_REVOCATION, PERMIT_TRANSFER, PERMIT_VARIATION));
                break;
            case AWAITING_REVOCATION:
                handle(accountId, List.of(PERMIT_SURRENDER, PERMIT_TRANSFER, PERMIT_VARIATION));
                break;
            default:
                break;
        }
    }

    private void handle(Long accountId, List<RequestType> types) {
        List<Request> requests =
            requestRepository.findByAccountIdAndTypeInAndStatus(
                accountId,
                types,
                RequestStatus.IN_PROGRESS
            );
        requests.forEach(this::closeRequestWithAction);
    }

    private void closeRequestWithAction(Request request) {
        workflowService.deleteProcessInstance(request.getProcessInstanceId(), DELETE_REASON);

        request.setStatus(RequestStatus.CANCELLED);

        requestService.addActionToRequest(
            request,
            null,
            RequestActionType.REQUEST_TERMINATED,
            null
        );
    }
}
