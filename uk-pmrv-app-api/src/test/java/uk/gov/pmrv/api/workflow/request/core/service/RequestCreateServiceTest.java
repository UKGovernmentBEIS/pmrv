package uk.gov.pmrv.api.workflow.request.core.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.account.service.AccountQueryService;
import uk.gov.pmrv.api.common.domain.enumeration.CompetentAuthority;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestStatus;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.core.repository.RequestRepository;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestParams;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RequestCreateServiceTest {

    @InjectMocks
    private RequestCreateService service;

    @Mock
    private RequestRepository requestRepository;
    
    @Mock
    private AccountQueryService accountQueryService;
    
    @Test
    void createRequest() {
    	final RequestType type = RequestType.INSTALLATION_ACCOUNT_OPENING;
        final RequestStatus status = RequestStatus.IN_PROGRESS;
        final CompetentAuthority ca = CompetentAuthority.ENGLAND;
        final Long accountId = 1L;
        final Long verificationBodyId = 1L;
    	
        RequestParams requestParams = createRequestParams(type, accountId, ca, verificationBodyId);
        //invoke
        service.createRequest(requestParams, status);
        
        //verify
        ArgumentCaptor<Request> requestCaptor = ArgumentCaptor.forClass(Request.class);
        verify(requestRepository, times(1)).save(requestCaptor.capture());
        Request request = requestCaptor.getValue();
        assertThat(request).isNotNull();
        assertThat(request.getType()).isEqualTo(type);
        assertThat(request.getStatus()).isEqualTo(status);
        assertThat(request.getCompetentAuthority()).isEqualTo(ca);
        assertThat(request.getVerificationBodyId()).isEqualTo(verificationBodyId);
        assertThat(request.getAccountId()).isEqualTo(accountId);
        
        verifyNoInteractions(accountQueryService);
    }

    @Test
    void createRequest_no_CA_and_VB_exist_in_request_params() {
        final RequestType type = RequestType.INSTALLATION_ACCOUNT_OPENING;
        final RequestStatus status = RequestStatus.IN_PROGRESS;
        final CompetentAuthority ca = CompetentAuthority.ENGLAND;
        final Long verificationBodyId = 1L;
        final Long accountId = 1L;

        when(accountQueryService.getAccountCa(accountId)).thenReturn(ca);
        when(accountQueryService.getAccountVerificationBodyId(accountId)).thenReturn(Optional.of(verificationBodyId));

        RequestParams requestParams = createRequestParams(type, accountId, null, null);
        
        //invoke
        service.createRequest(requestParams, status);

        //verify
        ArgumentCaptor<Request> requestCaptor = ArgumentCaptor.forClass(Request.class);
        verify(requestRepository, times(1)).save(requestCaptor.capture());
        Request request = requestCaptor.getValue();
        assertThat(request).isNotNull();
        assertThat(request.getType()).isEqualTo(type);
        assertThat(request.getStatus()).isEqualTo(status);
        assertThat(request.getCompetentAuthority()).isEqualTo(ca);
        assertThat(request.getAccountId()).isEqualTo(accountId);
        
        verify(accountQueryService, times(1)).getAccountCa(accountId);
        verify(accountQueryService, times(1)).getAccountVerificationBodyId(accountId);
    }

    private RequestParams createRequestParams(RequestType type, Long accountId, CompetentAuthority ca, Long verificationBodyId) {
        return RequestParams.builder()
            .requestId("1")
            .type(type)
            .ca(ca)
            .accountId(accountId)
            .verificationBodyId(verificationBodyId)
            .build();
    }
}
