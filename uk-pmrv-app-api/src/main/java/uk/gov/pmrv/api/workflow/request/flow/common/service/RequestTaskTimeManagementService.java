package uk.gov.pmrv.api.workflow.request.flow.common.service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.repository.RequestTaskRepository;
import uk.gov.pmrv.api.workflow.request.flow.common.taskhandler.WorkflowDynamicExpirableUserTaskCreatedHandlerMapper;

@Service
@RequiredArgsConstructor
public class RequestTaskTimeManagementService {

    private final RequestTaskRepository requestTaskRepository;
    private final WorkflowDynamicExpirableUserTaskCreatedHandlerMapper handlerMapper;

    public List<RequestTask> setDueDateToTasks(String requestId, String expirationDateKey, LocalDate dueDate) {
        List<RequestTask> expirableTasks = retrieveExpirableTasksByRequestAndExpirationKey(requestId, expirationDateKey);
        expirableTasks.forEach(requestTask -> requestTask.setDueDate(dueDate));
        return expirableTasks;
    }
    
    public void pauseTasks(String requestId, String expirationDateKey) {
        List<RequestTask> expirableTasks = retrieveExpirableTasksByRequestAndExpirationKey(requestId, expirationDateKey);
        expirableTasks.forEach(requestTask -> requestTask.setPauseDate(LocalDate.now()));
    }
    
    public void unpauseTasksAndUpdateDueDate(String requestId, String expirationDateKey, LocalDate dueDate) {
        List<RequestTask> expirableTasks = retrieveExpirableTasksByRequestAndExpirationKey(requestId, expirationDateKey);
        expirableTasks.forEach(requestTask -> {
            requestTask.setPauseDate(null);
            requestTask.setDueDate(dueDate);
        });
    }

    private List<RequestTask> retrieveExpirableTasksByRequestAndExpirationKey(String requestId, String expirationDateKey) {
        List<RequestTask> requestTasks = requestTaskRepository.findByRequestId(requestId);
        return requestTasks.stream()
            .filter(requestTask -> handlerMapper.handlerExists(requestTask.getType().name(), expirationDateKey))
            .collect(Collectors.toList());
    }
}
