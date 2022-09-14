package uk.gov.pmrv.api.workflow.request.application.verificationbodyappointed;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.pmrv.api.workflow.request.core.repository.RequestRepository;

@ExtendWith(MockitoExtension.class)
class RequestVerificationBodyServiceTest {
    
    @InjectMocks
    private RequestVerificationBodyService service;

    @Mock
    private RequestRepository requestRepository;
    
    @Test
    void appointVerificationBodyToRequestsOfAccount() {
        Long verificationBodyId = 1L; 
        Long accountId = 1L;
        
        service.appointVerificationBodyToRequestsOfAccount(verificationBodyId, accountId);
        
        verify(requestRepository, times(1)).updateVerificationBodyIdByAccountId(verificationBodyId, accountId);
    }


}
