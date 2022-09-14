package uk.gov.pmrv.api.workflow.request.application.taskdeleted;

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
class RequestTaskDeleteServiceTest {

    @InjectMocks
    private RequestTaskDeleteService service;

    @Mock
    private RequestTaskRepository requestTaskRepository;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @Test
    void delete() {
        
        final Long requestTaskId = 1L;
        final String processTaskId = "pr";
        final RequestTask requestTask = RequestTask.builder().id(requestTaskId).build();

        when(requestTaskRepository.findByProcessTaskId(processTaskId)).thenReturn(requestTask);

        service.delete(processTaskId);

        verify(requestTaskRepository, times(1)).findByProcessTaskId(processTaskId);
        verify(requestTaskRepository, times(1)).delete(requestTask);
        verify(eventPublisher, times(1)).publishEvent(RequestTaskDeletedEvent.builder()
            .requestTaskId(requestTask.getId()).build());
    }
}
