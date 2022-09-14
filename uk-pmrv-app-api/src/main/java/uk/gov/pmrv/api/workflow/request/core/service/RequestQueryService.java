package uk.gov.pmrv.api.workflow.request.core.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.dto.RequestDetailsDTO;
import uk.gov.pmrv.api.workflow.request.core.domain.dto.RequestDetailsSearchResults;
import uk.gov.pmrv.api.workflow.request.core.domain.dto.RequestSearchCriteria;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestStatus;
import uk.gov.pmrv.api.workflow.request.core.repository.RequestRepository;
import uk.gov.pmrv.api.workflow.request.core.repository.RequestDetailsRepository;

@Service
@RequiredArgsConstructor
public class RequestQueryService {

    private final RequestRepository requestRepository;
    private final RequestDetailsRepository requestDetailsRepository;
    
    @Transactional(readOnly = true)
    public List<Request> findOpenRequestsByAccount(Long accountId){
        return requestRepository.findByAccountIdAndStatusAndTypeNotNotification(accountId, RequestStatus.IN_PROGRESS);
    }

    public RequestDetailsSearchResults findRequestDetailsBySearchCriteria(RequestSearchCriteria criteria) {
        return requestDetailsRepository.findRequestDetailsBySearchCriteria(criteria);
    }

    public RequestDetailsDTO findRequestDetailsById(String requestId) {
        return requestDetailsRepository.findRequestDetailsById(requestId);
    }
}
