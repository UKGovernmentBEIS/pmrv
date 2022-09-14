package uk.gov.pmrv.api.workflow.request.application.taskcompleted;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.repository.RequestTaskRepository;

@ExtendWith(MockitoExtension.class)
class RequestTaskCompleteServiceTest {

    @InjectMocks
    private RequestTaskCompleteService service;

    @Mock
    private RequestTaskRepository requestTaskRepository;
    
    @Mock
    private ApplicationEventPublisher eventPublisher;
    
    @Test
    void complete() {
        Long requestTaskId = 1L;
        String processTaskId = "pr";
        RequestTask requestTask = RequestTask.builder().id(requestTaskId).build();

        when(requestTaskRepository.findByProcessTaskId(processTaskId)).thenReturn(requestTask);
        
        service.complete(processTaskId);
        
        verify(requestTaskRepository, times(1)).findByProcessTaskId(processTaskId);
        verify(requestTaskRepository, times(1)).delete(requestTask);
        verify(eventPublisher, times(1)).publishEvent(RequestTaskCompletedEvent.builder()
                .requestTaskId(requestTask.getId()).build());
    }
}
