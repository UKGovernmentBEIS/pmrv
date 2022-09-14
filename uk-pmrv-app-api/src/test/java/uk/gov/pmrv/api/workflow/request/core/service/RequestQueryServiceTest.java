package uk.gov.pmrv.api.workflow.request.core.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.dto.RequestDetailsDTO;
import uk.gov.pmrv.api.workflow.request.core.domain.dto.RequestDetailsSearchResults;
import uk.gov.pmrv.api.workflow.request.core.domain.dto.RequestSearchCriteria;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestStatus;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.core.repository.RequestRepository;
import uk.gov.pmrv.api.workflow.request.core.repository.RequestDetailsRepository;

@ExtendWith(MockitoExtension.class)
class RequestQueryServiceTest {

    @InjectMocks
    private RequestQueryService service;

    @Mock
    private RequestRepository requestRepository;

    @Mock
    private RequestDetailsRepository requestDetailsRepository;
    
    @Test
    void findOpenRequestsByAccount() {
        Long accountId = 1L;
        Request request = Request.builder().id("1").status(RequestStatus.IN_PROGRESS).build();

        when(requestRepository.findByAccountIdAndStatusAndTypeNotNotification(accountId, RequestStatus.IN_PROGRESS))
                .thenReturn(List.of(request));
        
        List<Request> result = service.findOpenRequestsByAccount(accountId);
        
        assertThat(result).containsExactlyInAnyOrder(request);
        verify(requestRepository, times(1)).findByAccountIdAndStatusAndTypeNotNotification(accountId, RequestStatus.IN_PROGRESS);
    }

    @Test
    void findRequestDetailsBySearchCriteria() {
        Long accountId = 1L;
        final String requestId = "1";
        RequestSearchCriteria criteria = RequestSearchCriteria.builder().accountId(accountId).page(0L).pageSize(30L).build();

        RequestDetailsDTO workflowResult1 = new RequestDetailsDTO(requestId, RequestType.INSTALLATION_ACCOUNT_OPENING, RequestStatus.IN_PROGRESS, LocalDateTime.now(), null);
        RequestDetailsDTO workflowResult2 = new RequestDetailsDTO(requestId, RequestType.PERMIT_ISSUANCE, RequestStatus.IN_PROGRESS, LocalDateTime.now(), null);

        RequestDetailsSearchResults expectedResults = RequestDetailsSearchResults.builder()
                .requestDetails(List.of(workflowResult1, workflowResult2))
                .total(10L)
                .build();

        when(requestDetailsRepository.findRequestDetailsBySearchCriteria(criteria)).thenReturn(expectedResults);

        RequestDetailsSearchResults actualResults = service.findRequestDetailsBySearchCriteria(criteria);

        assertThat(actualResults).isEqualTo(expectedResults);
        verify(requestDetailsRepository, times(1)).findRequestDetailsBySearchCriteria(criteria);
    }

    @Test
    void findRequestDetailsById() {
        final String requestId = "1";
        RequestDetailsDTO expected = new RequestDetailsDTO(requestId, RequestType.INSTALLATION_ACCOUNT_OPENING, RequestStatus.IN_PROGRESS, LocalDateTime.now(), null);

        when(requestDetailsRepository.findRequestDetailsById(requestId)).thenReturn(expected);

        RequestDetailsDTO actual = service.findRequestDetailsById(requestId);

        assertThat(actual).isEqualTo(expected);
        verify(requestDetailsRepository, times(1)).findRequestDetailsById(requestId);
    }
}
