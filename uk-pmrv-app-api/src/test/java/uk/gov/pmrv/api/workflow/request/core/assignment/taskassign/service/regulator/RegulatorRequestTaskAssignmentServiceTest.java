package uk.gov.pmrv.api.workflow.request.core.assignment.taskassign.service.regulator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.account.domain.enumeration.AccountContactType;
import uk.gov.pmrv.api.common.domain.enumeration.RoleType;
import uk.gov.pmrv.api.common.exception.BusinessCheckedException;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.workflow.request.core.assignment.taskassign.service.RequestTaskAssignmentService;
import uk.gov.pmrv.api.workflow.request.core.assignment.taskassign.service.SiteContactRequestTaskAssignmentService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.flow.permitissuance.common.domain.PermitIssuanceRequestPayload;

@ExtendWith(MockitoExtension.class)
class RegulatorRequestTaskAssignmentServiceTest {

    @InjectMocks
    private RegulatorRequestTaskAssignmentService regulatorRequestTaskAssignmentService;

    @Mock
    private SiteContactRequestTaskAssignmentService siteContactRequestTaskAssignmentService;

    @Mock
    private RequestTaskAssignmentService requestTaskAssignmentService;

    @Test
    void assignTasksOfDeletedRegulatorToCaSiteContactOrRelease() {
        String userId = "userId";
        regulatorRequestTaskAssignmentService.assignTasksOfDeletedRegulatorToCaSiteContactOrRelease(userId);
        verify(siteContactRequestTaskAssignmentService, times(1))
            .assignTasksOfDeletedUserToSiteContactOrRelease(userId, AccountContactType.CA_SITE);
    }

    @Test
    void assignTask_peer_review_task() throws BusinessCheckedException {
        String userId = "userId";
        String requestRegulatorReviewer = "requestRegulatorReviewer";
        Request request = Request.builder()
            .payload(PermitIssuanceRequestPayload.builder()
                .regulatorReviewer(requestRegulatorReviewer)
                .build())
            .build();
        RequestTask requestTask = RequestTask.builder()
            .request(request)
            .type(RequestTaskType.PERMIT_ISSUANCE_APPLICATION_PEER_REVIEW)
            .build();

        regulatorRequestTaskAssignmentService.assignTask(requestTask, userId);

        verify(requestTaskAssignmentService, times(1)).assignToUser(requestTask, userId);
        verifyNoMoreInteractions(requestTaskAssignmentService);
    }

    @Test
    void assignTask_peer_review_task_set_reviewer_as_assignee() {
        String userId = "userId";
        Request request = Request.builder()
            .payload(PermitIssuanceRequestPayload.builder()
                .regulatorReviewer(userId)
                .build())
            .build();
        RequestTask requestTask = RequestTask.builder()
            .request(request)
            .type(RequestTaskType.PERMIT_ISSUANCE_APPLICATION_PEER_REVIEW)
            .build();

        BusinessException be = assertThrows(BusinessException.class,
            () ->regulatorRequestTaskAssignmentService.assignTask(requestTask, userId));

        assertEquals(ErrorCode.ASSIGNMENT_NOT_ALLOWED, be.getErrorCode());

        verifyNoInteractions(requestTaskAssignmentService);
    }

    @Test
    void assignTask_non_peer_review_task() throws BusinessCheckedException {
        String userId = "userId";
        Request request = Request.builder().build();
        RequestTask requestTask = RequestTask.builder()
            .request(request)
            .type(RequestTaskType.PERMIT_ISSUANCE_APPLICATION_REVIEW)
            .build();

        regulatorRequestTaskAssignmentService.assignTask(requestTask, userId);

        verify(requestTaskAssignmentService, times(1)).assignToUser(requestTask, userId);
    }

    @Test
    void assignTask_non_peer_review_task_exception_on_assignment() throws BusinessCheckedException {
        String userId = "userId";
        Request request = Request.builder().build();
        RequestTask requestTask = RequestTask.builder()
            .request(request)
            .type(RequestTaskType.PERMIT_ISSUANCE_APPLICATION_REVIEW)
            .build();

        doThrow(BusinessCheckedException.class).when(requestTaskAssignmentService).assignToUser(requestTask, userId);

        BusinessException businessException = assertThrows(BusinessException.class,
            () -> regulatorRequestTaskAssignmentService.assignTask(requestTask, userId));

        assertEquals(ErrorCode.ASSIGNMENT_NOT_ALLOWED, businessException.getErrorCode());

        verify(requestTaskAssignmentService, times(1)).assignToUser(requestTask, userId);
        verifyNoMoreInteractions(requestTaskAssignmentService);
    }

    @Test
    void getRoleType() {
        assertEquals(RoleType.REGULATOR, regulatorRequestTaskAssignmentService.getRoleType());
    }
}