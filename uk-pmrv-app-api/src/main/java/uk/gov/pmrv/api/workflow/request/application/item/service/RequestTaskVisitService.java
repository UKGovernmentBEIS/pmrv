package uk.gov.pmrv.api.workflow.request.application.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import uk.gov.pmrv.api.workflow.request.application.item.domain.RequestTaskVisit;
import uk.gov.pmrv.api.workflow.request.application.item.repository.RequestTaskVisitRepository;

@Service
@RequiredArgsConstructor
public class RequestTaskVisitService {

    private final RequestTaskVisitRepository requestTaskVisitRepository;

    public void create(Long taskId, String userId) {
        requestTaskVisitRepository.save(RequestTaskVisit.builder()
            .taskId(taskId)
            .userId(userId)
            .build());
    }

    public void deleteByTaskId(Long taskId) {
        requestTaskVisitRepository.deleteByTaskId(taskId);
    }
}
