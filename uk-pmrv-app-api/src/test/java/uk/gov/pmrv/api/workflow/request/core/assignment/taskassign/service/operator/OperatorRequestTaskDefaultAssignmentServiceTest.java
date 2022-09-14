package uk.gov.pmrv.api.workflow.request.core.assignment.taskassign.service.operator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.account.service.AccountContactQueryService;
import uk.gov.pmrv.api.authorization.core.service.UserRoleTypeService;
import uk.gov.pmrv.api.common.domain.enumeration.RoleType;
import uk.gov.pmrv.api.common.exception.BusinessCheckedException;
import uk.gov.pmrv.api.workflow.request.core.assignment.requestassign.RequestReleaseService;
import uk.gov.pmrv.api.workflow.request.core.assignment.taskassign.service.RequestTaskAssignmentService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestStatus;
import uk.gov.pmrv.api.workflow.request.flow.permitissuance.common.domain.PermitIssuanceRequestPayload;

@ExtendWith(MockitoExtension.class)
class OperatorRequestTaskDefaultAssignmentServiceTest {

    @InjectMocks
    private OperatorRequestTaskDefaultAssignmentService operatorRequestTaskDefaultAssignmentService;

    @Mock
    private RequestTaskAssignmentService requestTaskAssignmentService;

    @Mock
    private AccountContactQueryService accountContactQueryService;

    @Mock
    private UserRoleTypeService userRoleTypeService;

    @Mock
    private RequestReleaseService requestReleaseService;

    @Test
    void assignDefaultAssigneeToTask() throws BusinessCheckedException {
        String operatorAssignee = "operatorAssignee";
        Request request = Request.builder()
            .payload(PermitIssuanceRequestPayload.builder().operatorAssignee(operatorAssignee).build())
            .status(RequestStatus.IN_PROGRESS)
            .build();
        RequestTask requestTask = RequestTask.builder().request(request).build();

        when(userRoleTypeService.isUserOperator(operatorAssignee)).thenReturn(true);
        doNothing().when(requestTaskAssignmentService).assignToUser(requestTask, operatorAssignee);

        operatorRequestTaskDefaultAssignmentService.assignDefaultAssigneeToTask(requestTask);

        verify(userRoleTypeService, times(1)).isUserOperator(operatorAssignee);
        verify(requestTaskAssignmentService, times(1)).assignToUser(requestTask, operatorAssignee);
        verifyNoMoreInteractions(requestTaskAssignmentService);
        verifyNoInteractions(accountContactQueryService, requestReleaseService);
    }

    @Test
    void assignDefaultAssigneeToTask_null_request_assignee() throws BusinessCheckedException {
        String primaryContact = "primaryContact";
        Request request = Request.builder()
            .payload(PermitIssuanceRequestPayload.builder().build())
            .accountId(1L)
            .status(RequestStatus.IN_PROGRESS).build();
        RequestTask requestTask = RequestTask.builder().request(request).build();

        when(accountContactQueryService.findPrimaryContactByAccount(request.getAccountId())).thenReturn(primaryContact);
        doNothing().when(requestTaskAssignmentService).assignToUser(requestTask, primaryContact);

        operatorRequestTaskDefaultAssignmentService.assignDefaultAssigneeToTask(requestTask);

        verify(accountContactQueryService, times(1)).findPrimaryContactByAccount(request.getAccountId());
        verify(requestTaskAssignmentService, times(1)).assignToUser(requestTask, primaryContact);
        verifyNoMoreInteractions(requestTaskAssignmentService);
        verifyNoInteractions(userRoleTypeService, requestReleaseService);
    }

    @Test
    void assignDefaultAssigneeToTask_request_assignee_is_not_operator() throws BusinessCheckedException {
        String operatorAssignee = "operatorAssignee";
        String primaryContact = "primaryContact";
        Request request = Request.builder()
            .payload(PermitIssuanceRequestPayload.builder().operatorAssignee(operatorAssignee).build())
            .accountId(1L)
            .status(RequestStatus.IN_PROGRESS)
            .build();
        RequestTask requestTask = RequestTask.builder().request(request).build();

        when(userRoleTypeService.isUserOperator(operatorAssignee)).thenReturn(false);
        when(accountContactQueryService.findPrimaryContactByAccount(request.getAccountId())).thenReturn(primaryContact);
        doNothing().when(requestTaskAssignmentService).assignToUser(requestTask, primaryContact);

        operatorRequestTaskDefaultAssignmentService.assignDefaultAssigneeToTask(requestTask);

        verify(userRoleTypeService, times(1)).isUserOperator(operatorAssignee);
        verify(accountContactQueryService, times(1)).findPrimaryContactByAccount(request.getAccountId());
        verify(requestTaskAssignmentService, times(1)).assignToUser(requestTask, primaryContact);
        verifyNoMoreInteractions(requestTaskAssignmentService);
        verifyNoInteractions(requestReleaseService);
    }

    @Test
    void assignDefaultAssigneeToTask_assign_to_request_assignee_throws_business_exception() throws BusinessCheckedException {
        String operatorAssignee = "operatorAssignee";
        String primaryContact = "primaryContact";
        Request request = Request.builder()
            .payload(PermitIssuanceRequestPayload.builder().operatorAssignee(operatorAssignee).build())
            .accountId(1L)
            .status(RequestStatus.IN_PROGRESS)
            .build();
        RequestTask requestTask = RequestTask.builder().request(request).build();

        when(userRoleTypeService.isUserOperator(operatorAssignee)).thenReturn(true);
        doThrow(BusinessCheckedException.class).when(requestTaskAssignmentService).assignToUser(requestTask, operatorAssignee);
        when(accountContactQueryService.findPrimaryContactByAccount(request.getAccountId())).thenReturn(primaryContact);
        doNothing().when(requestTaskAssignmentService).assignToUser(requestTask, primaryContact);

        operatorRequestTaskDefaultAssignmentService.assignDefaultAssigneeToTask(requestTask);

        verify(userRoleTypeService, times(1)).isUserOperator(operatorAssignee);
        verify(requestTaskAssignmentService, times(1)).assignToUser(requestTask, operatorAssignee);
        verify(accountContactQueryService, times(1)).findPrimaryContactByAccount(request.getAccountId());
        verify(requestTaskAssignmentService, times(1)).assignToUser(requestTask, primaryContact);
        verifyNoInteractions(requestReleaseService);
    }

    @Test
    void assignDefaultAssigneeToTask_assign_to_primary_account_throws_business_exception() throws BusinessCheckedException {
        String operatorAssignee = "operatorAssignee";
        String primaryContact = "primaryContact";
        Request request = Request.builder()
            .payload(PermitIssuanceRequestPayload.builder().operatorAssignee(operatorAssignee).build())
            .accountId(1L)
            .status(RequestStatus.IN_PROGRESS)
            .build();
        RequestTask requestTask = RequestTask.builder().request(request).build();

        when(userRoleTypeService.isUserOperator(operatorAssignee)).thenReturn(true);
        doThrow(BusinessCheckedException.class).when(requestTaskAssignmentService).assignToUser(requestTask, operatorAssignee);
        when(accountContactQueryService.findPrimaryContactByAccount(request.getAccountId())).thenReturn(primaryContact);
        doThrow(BusinessCheckedException.class).when(requestTaskAssignmentService).assignToUser(requestTask, primaryContact);

        operatorRequestTaskDefaultAssignmentService.assignDefaultAssigneeToTask(requestTask);

        verify(userRoleTypeService, times(1)).isUserOperator(operatorAssignee);
        verify(requestTaskAssignmentService, times(1)).assignToUser(requestTask, operatorAssignee);
        verify(accountContactQueryService, times(1)).findPrimaryContactByAccount(request.getAccountId());
        verify(requestTaskAssignmentService, times(1)).assignToUser(requestTask, primaryContact);
        verify(requestReleaseService, times(1)).releaseRequest(requestTask);
    }

    @Test
    void getRoleType() {
        assertEquals(RoleType.OPERATOR, operatorRequestTaskDefaultAssignmentService.getRoleType());
    }
}