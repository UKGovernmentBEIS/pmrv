package uk.gov.pmrv.api.workflow.request.flow.common.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.core.repository.RequestTaskRepository;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.common.taskhandler.WorkflowDynamicExpirableUserTaskCreatedHandlerMapper;

@ExtendWith(MockitoExtension.class)
class RequestTaskTimeManagementServiceTest {

    @InjectMocks
    private RequestTaskTimeManagementService requestTaskTimeManagementService;

    @Mock
    private RequestTaskRepository requestTaskRepository;

    @Mock
    private WorkflowDynamicExpirableUserTaskCreatedHandlerMapper handlerMapper;

    @Test
    void setDueDateToTasks() {
        String requestId = "1";
        LocalDate dueDate = LocalDate.now();
        RequestTask permitSubmitTask = RequestTask.builder()
            .type(RequestTaskType.PERMIT_ISSUANCE_APPLICATION_SUBMIT)
            .build();
        RequestTask permitReviewTask = RequestTask.builder()
            .type(RequestTaskType.PERMIT_ISSUANCE_APPLICATION_REVIEW)
            .build();

        when(requestTaskRepository.findByRequestId(requestId)).thenReturn(List.of(permitSubmitTask, permitReviewTask));
        when(handlerMapper.handlerExists(RequestTaskType.PERMIT_ISSUANCE_APPLICATION_SUBMIT.name(), BpmnProcessConstants.APPLICATION_REVIEW_EXPIRATION_DATE))
            .thenReturn(false);
        when(handlerMapper.handlerExists(RequestTaskType.PERMIT_ISSUANCE_APPLICATION_REVIEW.name(), BpmnProcessConstants.APPLICATION_REVIEW_EXPIRATION_DATE))
            .thenReturn(true);

        requestTaskTimeManagementService
            .setDueDateToTasks(requestId, BpmnProcessConstants.APPLICATION_REVIEW_EXPIRATION_DATE, dueDate);

        assertEquals(dueDate, permitReviewTask.getDueDate());
        assertNull(permitSubmitTask.getDueDate());
    }

    @Test
    void pauseTasks() {
        String requestId = "1";
        RequestTask permitSubmitTask = RequestTask.builder()
            .type(RequestTaskType.PERMIT_ISSUANCE_APPLICATION_SUBMIT)
            .build();
        RequestTask permitReviewTask = RequestTask.builder()
            .type(RequestTaskType.PERMIT_ISSUANCE_APPLICATION_REVIEW)
            .build();

        when(requestTaskRepository.findByRequestId(requestId)).thenReturn(List.of(permitSubmitTask, permitReviewTask));
        when(handlerMapper.handlerExists(RequestTaskType.PERMIT_ISSUANCE_APPLICATION_SUBMIT.name(), BpmnProcessConstants.APPLICATION_REVIEW_EXPIRATION_DATE))
            .thenReturn(false);
        when(handlerMapper.handlerExists(RequestTaskType.PERMIT_ISSUANCE_APPLICATION_REVIEW.name(), BpmnProcessConstants.APPLICATION_REVIEW_EXPIRATION_DATE))
            .thenReturn(true);

        requestTaskTimeManagementService
            .pauseTasks(requestId, BpmnProcessConstants.APPLICATION_REVIEW_EXPIRATION_DATE);

        assertNotNull(permitReviewTask.getPauseDate());
        assertNull(permitSubmitTask.getPauseDate());
    }

    @Test
    void unpauseTasksAndUpdateDueDate() {
        String requestId = "1";
        LocalDate dueDate = LocalDate.now();
        RequestTask permitSubmitTask = RequestTask.builder()
            .type(RequestTaskType.PERMIT_ISSUANCE_APPLICATION_SUBMIT)
            .pauseDate(LocalDate.now())
            .build();
        RequestTask permitReviewTask = RequestTask.builder()
            .type(RequestTaskType.PERMIT_ISSUANCE_APPLICATION_REVIEW)
            .pauseDate(LocalDate.now())
            .build();

        when(requestTaskRepository.findByRequestId(requestId)).thenReturn(List.of(permitSubmitTask, permitReviewTask));
        when(handlerMapper.handlerExists(RequestTaskType.PERMIT_ISSUANCE_APPLICATION_SUBMIT.name(),
                BpmnProcessConstants.APPLICATION_REVIEW_EXPIRATION_DATE))
            .thenReturn(false);
        when(handlerMapper.handlerExists(RequestTaskType.PERMIT_ISSUANCE_APPLICATION_REVIEW.name(),
                BpmnProcessConstants.APPLICATION_REVIEW_EXPIRATION_DATE))
            .thenReturn(true);

        requestTaskTimeManagementService
            .unpauseTasksAndUpdateDueDate(requestId, BpmnProcessConstants.APPLICATION_REVIEW_EXPIRATION_DATE, dueDate);

        assertNotNull(permitSubmitTask.getPauseDate());
        assertNull(permitSubmitTask.getDueDate());

        assertNull(permitReviewTask.getPauseDate());
        assertEquals(dueDate, permitReviewTask.getDueDate());
    }
}