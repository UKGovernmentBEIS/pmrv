package uk.gov.pmrv.api.workflow.request.core.assignment.taskassign.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.authorization.rules.domain.ResourceType;
import uk.gov.pmrv.api.authorization.rules.services.AuthorizationRulesQueryService;
import uk.gov.pmrv.api.common.domain.enumeration.RoleType;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.workflow.request.core.assignment.taskassign.dto.RequestTaskAssignmentDTO;
import uk.gov.pmrv.api.workflow.request.core.assignment.taskassign.service.operator.OperatorRequestTaskAssignmentService;
import uk.gov.pmrv.api.workflow.request.core.assignment.taskassign.service.regulator.RegulatorRequestTaskAssignmentService;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType.INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW;
import static uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType.INSTALLATION_ACCOUNT_OPENING_ARCHIVE;

@ExtendWith(MockitoExtension.class)
class UserRequestTaskAssignmentServiceTest {

    @InjectMocks
    private UserRequestTaskAssignmentService userRequestTaskAssignmentService;

    @Mock
    private RequestTaskService requestTaskService;

    @Mock
    private RequestTaskAssignmentValidationService requestTaskAssignmentValidationService;

    @Mock
    private AuthorizationRulesQueryService authorizationRulesQueryService;

    @Mock
    private OperatorRequestTaskAssignmentService operatorRequestTaskAssignmentService;

    @Mock
    private RegulatorRequestTaskAssignmentService regulatorRequestTaskAssignmentService;

    @BeforeEach
    void setup() {
        userRequestTaskAssignmentService = new UserRequestTaskAssignmentService(
                requestTaskService,
                requestTaskAssignmentValidationService,
                authorizationRulesQueryService,
                List.of(operatorRequestTaskAssignmentService, regulatorRequestTaskAssignmentService));
    }

    @Test
    void assignTask_operator() {
        Long requestTaskId = 1L;
        String userId = "userId";

        RequestTaskAssignmentDTO requestTaskAssignmentDTO = RequestTaskAssignmentDTO.builder()
                .taskId(requestTaskId)
                .userId(userId)
                .build();

        RequestTask requestTask = RequestTask.builder().id(requestTaskId).type(INSTALLATION_ACCOUNT_OPENING_ARCHIVE).build();

        when(requestTaskService.findTaskById(requestTaskId)).thenReturn(requestTask);
        when(authorizationRulesQueryService
                .findRoleTypeByResourceTypeAndSubType(ResourceType.REQUEST_TASK, requestTask.getType().name()))
                .thenReturn(Optional.of(RoleType.OPERATOR));
        when(operatorRequestTaskAssignmentService.getRoleType()).thenReturn(RoleType.OPERATOR);

        userRequestTaskAssignmentService.assignTask(requestTaskAssignmentDTO);

        verify(operatorRequestTaskAssignmentService, times(1)).assignTask(requestTask, userId);
        verify(requestTaskAssignmentValidationService, times(1)).validateTaskAssignmentCapability(requestTask);
        verify(regulatorRequestTaskAssignmentService, never()).assignTask(any(), anyString());
    }

    @Test
    void assignTask_regulator() {
        Long requestTaskId = 1L;
        String userId = "userId";

        RequestTaskAssignmentDTO requestTaskAssignmentDTO = RequestTaskAssignmentDTO.builder()
                .taskId(requestTaskId)
                .userId(userId)
                .build();

        RequestTask requestTask = RequestTask.builder().id(requestTaskId).type(INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW).build();

        when(requestTaskService.findTaskById(requestTaskId)).thenReturn(requestTask);
        when(authorizationRulesQueryService
                .findRoleTypeByResourceTypeAndSubType(ResourceType.REQUEST_TASK, requestTask.getType().name()))
                .thenReturn(Optional.of(RoleType.REGULATOR));
        when(operatorRequestTaskAssignmentService.getRoleType()).thenReturn(RoleType.OPERATOR);
        when(regulatorRequestTaskAssignmentService.getRoleType()).thenReturn(RoleType.REGULATOR);

        userRequestTaskAssignmentService.assignTask(requestTaskAssignmentDTO);

        verify(regulatorRequestTaskAssignmentService, times(1)).assignTask(requestTask, userId);
        verify(requestTaskAssignmentValidationService, times(1)).validateTaskAssignmentCapability(requestTask);
        verify(operatorRequestTaskAssignmentService, never()).assignTask(any(), anyString());
    }

    @Test
    void assignTask_no_task_found() {
        Long requestTaskId = 1L;
        String userId = "userId";

        RequestTaskAssignmentDTO requestTaskAssignmentDTO = RequestTaskAssignmentDTO.builder()
                .taskId(requestTaskId)
                .userId(userId)
                .build();

        when(requestTaskService.findTaskById(requestTaskId)).thenThrow(new BusinessException(ErrorCode.RESOURCE_NOT_FOUND));

        BusinessException businessException = assertThrows(BusinessException.class,
                () -> userRequestTaskAssignmentService.assignTask(requestTaskAssignmentDTO));

        assertEquals(ErrorCode.RESOURCE_NOT_FOUND, businessException.getErrorCode());

        verifyNoInteractions(authorizationRulesQueryService, operatorRequestTaskAssignmentService,
                regulatorRequestTaskAssignmentService, requestTaskAssignmentValidationService);
    }

}