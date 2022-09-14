package uk.gov.pmrv.api.workflow.request.flow.common.actionhandler;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.aspectj.lang.JoinPoint;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.common.domain.model.PmrvUser;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.accountinstallationopening.domain.InstallationAccountOpeningAmendApplicationRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.core.validation.RequestTaskActionValidatorService;

@ExtendWith(MockitoExtension.class)
class ProcessRequestTaskAspectTest {

    @InjectMocks
    private ProcessRequestTaskAspect processRequestTaskAspect;

    @Mock
    private RequestTaskService requestTaskService;

    @Mock
    private JoinPoint joinPoint;

    @Mock
    private RequestTaskActionValidatorService requestTaskActionValidatorService;

    @Test
    void validateProcessRequestTask() {
        final PmrvUser user = PmrvUser.builder().userId("userId").build();
        final Object[] arguments = new Object[]{1L,
                RequestTaskActionType.INSTALLATION_ACCOUNT_OPENING_AMEND_APPLICATION, user, new InstallationAccountOpeningAmendApplicationRequestTaskActionPayload()};
        final RequestTask requestTask = RequestTask.builder()
                .assignee("userId").type(RequestTaskType.INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW).build();

        // Mock
        when(joinPoint.getArgs()).thenReturn(arguments);
        when(requestTaskService.findTaskById(1L)).thenReturn(requestTask);
        doNothing().when(requestTaskActionValidatorService).validate(requestTask, 
            RequestTaskActionType.INSTALLATION_ACCOUNT_OPENING_AMEND_APPLICATION);

        // Invoke
        processRequestTaskAspect.validateProcessRequestTask(joinPoint);

        // Assert
        verify(requestTaskService, times(1)).findTaskById(1L);
        verify(requestTaskActionValidatorService, times(1)).validate(requestTask,
            RequestTaskActionType.INSTALLATION_ACCOUNT_OPENING_AMEND_APPLICATION);
    }

    @Test
    void validateProcessRequestTask_not_valid_assignee() {
        final PmrvUser user = PmrvUser.builder().userId("userId").build();
        final Object[] arguments = new Object[]{1L,
            RequestTaskActionType.INSTALLATION_ACCOUNT_OPENING_AMEND_APPLICATION, user, new InstallationAccountOpeningAmendApplicationRequestTaskActionPayload()};
        final RequestTask requestTask = RequestTask.builder()
                .assignee("userId2").type(RequestTaskType.INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW).build();

        // Mock
        when(joinPoint.getArgs()).thenReturn(arguments);
        when(requestTaskService.findTaskById(1L)).thenReturn(requestTask);

        // Invoke
        BusinessException businessException = assertThrows(BusinessException.class,
                () -> processRequestTaskAspect.validateProcessRequestTask(joinPoint));

        // Assert
        assertEquals(ErrorCode.REQUEST_TASK_ACTION_USER_NOT_THE_ASSIGNEE, businessException.getErrorCode());
        verify(requestTaskService, times(1)).findTaskById(1L);
    }

    @Test
    void validateProcessRequestTask_not_valid_action() {
        final PmrvUser user = PmrvUser.builder().userId("userId").build();
        final Object[] arguments = new Object[]{1L,
            RequestTaskActionType.SYSTEM_MESSAGE_DISMISS, user, new InstallationAccountOpeningAmendApplicationRequestTaskActionPayload()};
        final RequestTask requestTask = RequestTask.builder()
                .assignee("userId").type(RequestTaskType.INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW).build();

        // Mock
        when(joinPoint.getArgs()).thenReturn(arguments);
        when(requestTaskService.findTaskById(1L)).thenReturn(requestTask);

        // Invoke
        BusinessException businessException = assertThrows(BusinessException.class,
                () -> processRequestTaskAspect.validateProcessRequestTask(joinPoint));

        // Assert
        assertEquals(ErrorCode.REQUEST_TASK_ACTION_CANNOT_PROCEED, businessException.getErrorCode());
        verify(requestTaskService, times(1)).findTaskById(1L);
    }

    @Test
    void validateProcessRequestTask_no_task_exists() {
        final PmrvUser user = PmrvUser.builder().userId("userId").build();
        final Object[] arguments = new Object[]{1L,
            RequestTaskActionType.INSTALLATION_ACCOUNT_OPENING_AMEND_APPLICATION, user, new InstallationAccountOpeningAmendApplicationRequestTaskActionPayload()};

        // Mock
        when(joinPoint.getArgs()).thenReturn(arguments);
        when(requestTaskService.findTaskById(1L)).thenThrow(new BusinessException(ErrorCode.RESOURCE_NOT_FOUND));

        // Invoke
        BusinessException businessException = assertThrows(BusinessException.class,
                () -> processRequestTaskAspect.validateProcessRequestTask(joinPoint));

        // Assert
        assertEquals(ErrorCode.RESOURCE_NOT_FOUND, businessException.getErrorCode());
        verify(requestTaskService, times(1)).findTaskById(1L);
    }

    @Test
    void validateProcessRequestTask_whenCustomValidationFails_thenThrowException() {
        
        final PmrvUser user = PmrvUser.builder().userId("userId").build();
        final Object[] arguments = new Object[]{1L,
            RequestTaskActionType.INSTALLATION_ACCOUNT_OPENING_AMEND_APPLICATION, user, 
            new InstallationAccountOpeningAmendApplicationRequestTaskActionPayload()};
        final RequestTask requestTask = RequestTask.builder()
            .assignee("userId").type(RequestTaskType.INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW).build();

        // Mock
        when(joinPoint.getArgs()).thenReturn(arguments);
        when(requestTaskService.findTaskById(1L)).thenReturn(requestTask);
        doThrow(new BusinessException(ErrorCode.REQUEST_TASK_ACTION_CANNOT_PROCEED))
            .when(requestTaskActionValidatorService)
            .validate(requestTask, RequestTaskActionType.INSTALLATION_ACCOUNT_OPENING_AMEND_APPLICATION);
        
        // Invoke
        BusinessException businessException = assertThrows(BusinessException.class,
            () -> processRequestTaskAspect.validateProcessRequestTask(joinPoint));

        // Assert
        assertEquals(ErrorCode.REQUEST_TASK_ACTION_CANNOT_PROCEED, businessException.getErrorCode());
    }
}
