package uk.gov.pmrv.api.workflow.request.application.taskcompleted;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.repository.RequestTaskRepository;

@Service
@RequiredArgsConstructor
public class RequestTaskCompleteService {

    private final RequestTaskRepository requestTaskRepository;
    private final ApplicationEventPublisher eventPublisher;

    public void complete(String processTaskId) {
        RequestTask requestTask = requestTaskRepository.findByProcessTaskId(processTaskId);

        eventPublisher.publishEvent(RequestTaskCompletedEvent.builder()
                .requestTaskId(requestTask.getId()).build());

        requestTaskRepository.delete(requestTask);
    }
    
}
