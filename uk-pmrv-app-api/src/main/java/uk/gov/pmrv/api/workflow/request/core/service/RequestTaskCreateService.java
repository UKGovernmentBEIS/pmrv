package uk.gov.pmrv.api.workflow.request.core.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.core.assignment.taskassign.service.RequestTaskDefaultAssignmentService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;

@Service
@RequiredArgsConstructor
public class RequestTaskCreateService {

    private final RequestService requestService;
    private final RequestTaskDefaultAssignmentService requestTaskDefaultAssignmentService;
    private final List<InitializeRequestTaskHandler> initializeRequestTaskHandlers;


    public void create(final String requestId,
                       final String processTaskId,
                       final RequestTaskType requestTaskType) {

        this.create(requestId, processTaskId, requestTaskType, null);
    }

    public void create(final String requestId,
                       final String processTaskId,
                       final RequestTaskType requestTaskType,
                       final String userToAssignTask) {

        this.create(requestId, processTaskId, requestTaskType, userToAssignTask, null);
    }
    
    public void create(final String requestId,
                       final String processTaskId,
                       final RequestTaskType requestTaskType,
                       final String userToAssignTask,
                       final LocalDate dueDate) {

        Request request = requestService.findRequestById(requestId);

        RequestTask requestTask =
            RequestTask.builder()
                .processTaskId(processTaskId)
                .type(requestTaskType)
                .payload(createRequestTaskPayload(requestTaskType, request))
                .startDate(LocalDateTime.now())
                .dueDate(dueDate)
                .build();
        
        request.addRequestTask(requestTask);

        assignToUserOrAssignToDefaultUser(requestTask, userToAssignTask);
    }

    private void assignToUserOrAssignToDefaultUser(RequestTask requestTask, String userToAssignTask) {
        if(userToAssignTask != null) {
            requestTask.setAssignee(userToAssignTask);
        } else {
            requestTaskDefaultAssignmentService.assignDefaultAssigneeToTask(requestTask);
        }
    }

    private RequestTaskPayload createRequestTaskPayload(RequestTaskType requestTaskType, Request request) {
        Optional<InitializeRequestTaskHandler> initializer = initializeRequestTaskHandlers.stream()
            .filter(handler -> handler.getRequestTaskTypes().contains(requestTaskType))
            .findFirst();

        return initializer.map(handler -> handler.initializePayload(request))
            .orElse(null);
    }
}
