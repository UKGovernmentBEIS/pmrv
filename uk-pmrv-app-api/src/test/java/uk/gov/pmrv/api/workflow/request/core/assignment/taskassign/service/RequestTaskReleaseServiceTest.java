package uk.gov.pmrv.api.workflow.request.core.assignment.taskassign.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.authorization.rules.domain.ResourceType;
import uk.gov.pmrv.api.authorization.rules.services.AuthorizationRulesQueryService;
import uk.gov.pmrv.api.common.domain.enumeration.RoleType;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.workflow.request.core.assignment.requestassign.RequestReleaseService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;

import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class RequestTaskReleaseServiceTest {

    @InjectMocks
    private RequestTaskReleaseService requestTaskReleaseService;

    @Mock
    private RequestTaskService requestTaskService;

    @Mock
    private RequestTaskAssignmentValidationService requestTaskAssignmentValidationService;

    @Mock
    private RequestReleaseService requestReleaseService;

    @Mock
    private AuthorizationRulesQueryService authorizationRulesQueryService;

    @Test
    void releaseTaskById() {
        Long requestTaskId = 1L;
        Request request = Request.builder().id("1").build();
        RoleType roleType = RoleType.REGULATOR;

        RequestTask requestTask = RequestTask.builder().id(requestTaskId).request(request)
                .type(RequestTaskType.PERMIT_ISSUANCE_APPLICATION_REVIEW).assignee("assignee").build();
        RequestTask requestTask1 = RequestTask.builder().id(2L).request(request)
                .type(RequestTaskType.PERMIT_ISSUANCE_APPLICATION_PEER_REVIEW).assignee("assignee2").build();
        RequestTask requestTask2 = RequestTask.builder().id(3L).request(request)
                .type(RequestTaskType.PERMIT_ISSUANCE_CONFIRM_PAYMENT).assignee("assignee").build();
        List<RequestTask> requestTasks = List.of(requestTask1, requestTask2);

        // Mock
        when(requestTaskService.findTaskById(requestTaskId)).thenReturn(requestTask);
        when(authorizationRulesQueryService.findRoleTypeByResourceTypeAndSubType(ResourceType.REQUEST_TASK, requestTask.getType().name()))
                .thenReturn(Optional.of(RoleType.REGULATOR));
        when(requestTaskService.findTasksByRequestIdAndRoleType(request.getId(), roleType))
                .thenReturn(requestTasks);

        // Invoke
        requestTaskReleaseService.releaseTaskById(requestTaskId);

        // Verify
        verify(requestTaskService, times(1))
                .findTaskById(requestTaskId);
        verify(authorizationRulesQueryService, times(1))
                .findRoleTypeByResourceTypeAndSubType(ResourceType.REQUEST_TASK, requestTask.getType().name());
        verify(requestTaskAssignmentValidationService, times(1))
                .validateTaskReleaseCapability(requestTask, roleType);
        verify(requestTaskService, times(1))
                .findTasksByRequestIdAndRoleType(request.getId(), roleType);
        verify(requestReleaseService, times(1)).releaseRequest(requestTask);

        assertNull(requestTask.getAssignee());
        assertNotNull(requestTask1.getAssignee());
        assertNull(requestTask2.getAssignee());
    }
    
    @Test
    void releaseTaskById_not_task_release_capability() {
        Long requestTaskId = 1L;
        Request request = Request.builder().id("1").build();
        RequestTask requestTask = RequestTask.builder().id(requestTaskId).request(request)
                .type(RequestTaskType.PERMIT_ISSUANCE_APPLICATION_SUBMIT).assignee("assignee").build();
        RoleType roleType = RoleType.OPERATOR;

        when(requestTaskService.findTaskById(requestTaskId)).thenReturn(requestTask);
        when(authorizationRulesQueryService.findRoleTypeByResourceTypeAndSubType(ResourceType.REQUEST_TASK, requestTask.getType().name()))
                .thenReturn(Optional.of(RoleType.OPERATOR));
        doThrow(new BusinessException(ErrorCode.ASSIGNMENT_NOT_ALLOWED))
                .when(requestTaskAssignmentValidationService).validateTaskReleaseCapability(requestTask, roleType);

        // Invoke
        BusinessException businessException = assertThrows(BusinessException.class, () ->
                requestTaskReleaseService.releaseTaskById(requestTaskId));

        // Verify
        assertEquals(ErrorCode.ASSIGNMENT_NOT_ALLOWED, businessException.getErrorCode());

        verify(requestTaskService, times(1))
                .findTaskById(requestTaskId);
        verify(authorizationRulesQueryService, times(1))
                .findRoleTypeByResourceTypeAndSubType(ResourceType.REQUEST_TASK, requestTask.getType().name());
        verify(requestTaskAssignmentValidationService, times(1))
                .validateTaskReleaseCapability(requestTask, roleType);
        verify(requestTaskService, never())
                .findTasksByRequestIdAndRoleType(anyString(), any());
        verify(requestReleaseService, never()).releaseRequest(any());

        assertNotNull(requestTask.getAssignee());
    }
    
    @Test
    void releaseTaskForced() {
        Long requestTaskId = 1L;
        Request request = Request.builder().id("1").build();
        RequestTask requestTask = RequestTask.builder().id(requestTaskId).request(request).assignee("assignee").build();

        // Invoke
        requestTaskReleaseService.releaseTaskForced(requestTask);

        // Verify
        ArgumentCaptor<RequestTask> requestTaskCaptor = ArgumentCaptor.forClass(RequestTask.class);
        verify(requestReleaseService, times(1)).releaseRequest(requestTaskCaptor.capture());
        assertNull(requestTaskCaptor.getValue().getAssignee());
        verifyNoInteractions(requestTaskAssignmentValidationService);
    }

}
