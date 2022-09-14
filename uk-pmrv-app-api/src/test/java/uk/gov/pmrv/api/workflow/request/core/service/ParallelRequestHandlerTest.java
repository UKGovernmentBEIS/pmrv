package uk.gov.pmrv.api.workflow.request.core.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType.PERMIT_REVOCATION;
import static uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType.PERMIT_SURRENDER;
import static uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType.PERMIT_TRANSFER;
import static uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType.PERMIT_VARIATION;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.account.domain.enumeration.AccountStatus;
import uk.gov.pmrv.api.workflow.request.WorkflowService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestStatus;
import uk.gov.pmrv.api.workflow.request.core.repository.RequestRepository;

@ExtendWith(MockitoExtension.class)
class ParallelRequestHandlerTest {

    private static final String DELETE_REASON = "Workflow terminated by the system";
    private static final Long TEST_ACCOUNT_ID = 1L;
    private static final String TEST_PROCESS_INSTANCE_ID = "1";

    @Mock
    private RequestRepository requestRepository;
    @Mock
    private WorkflowService workflowService;
    @Mock
    private RequestService requestService;


    @InjectMocks
    ParallelRequestHandler cut;

    @Test
    void shouldClosePermitRevocationOnSurrender() {

        List<Request> requests = List.of(
            Request.builder()
                .processInstanceId(TEST_PROCESS_INSTANCE_ID)
                .status(RequestStatus.IN_PROGRESS)
                .build()
        );
        when(requestRepository.findByAccountIdAndTypeInAndStatus(TEST_ACCOUNT_ID,
            List.of(PERMIT_REVOCATION, PERMIT_TRANSFER, PERMIT_VARIATION), RequestStatus.IN_PROGRESS)).thenReturn(
            requests);

        cut.handleParallelRequests(TEST_ACCOUNT_ID, AccountStatus.AWAITING_SURRENDER);

        verify(workflowService, times(1)).deleteProcessInstance(TEST_PROCESS_INSTANCE_ID,
            DELETE_REASON);
        verify(requestService, times(1)).addActionToRequest(requests.get(0), null, RequestActionType.REQUEST_TERMINATED,
            null);
        assertThat(requests.get(0).getStatus()).isEqualTo(RequestStatus.CANCELLED);
    }
    
    @Test
    void shouldClosePermitSurrenderOnRevocation() {

        List<Request> requests = List.of(
            Request.builder()
                .processInstanceId(TEST_PROCESS_INSTANCE_ID)
                .status(RequestStatus.IN_PROGRESS)
                .build()
        );
        when(requestRepository.findByAccountIdAndTypeInAndStatus(TEST_ACCOUNT_ID,
            List.of(PERMIT_SURRENDER, PERMIT_TRANSFER, PERMIT_VARIATION), RequestStatus.IN_PROGRESS)).thenReturn(
            requests);

        cut.handleParallelRequests(TEST_ACCOUNT_ID, AccountStatus.AWAITING_REVOCATION);

        verify(workflowService, times(1)).deleteProcessInstance(TEST_PROCESS_INSTANCE_ID,
            DELETE_REASON);
        verify(requestService, times(1)).addActionToRequest(requests.get(0), null, RequestActionType.REQUEST_TERMINATED,
            null);
        assertThat(requests.get(0).getStatus()).isEqualTo(RequestStatus.CANCELLED);
    }
}
