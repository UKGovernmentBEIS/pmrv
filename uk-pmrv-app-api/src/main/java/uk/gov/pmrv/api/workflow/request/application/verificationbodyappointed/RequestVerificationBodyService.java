package uk.gov.pmrv.api.workflow.request.application.verificationbodyappointed;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import uk.gov.pmrv.api.workflow.request.core.repository.RequestRepository;

@RequiredArgsConstructor
@Service
class RequestVerificationBodyService {
    
    private final RequestRepository requestRepository;

    @Transactional
    public void appointVerificationBodyToRequestsOfAccount(Long verificationBodyId, Long accountId) {
        requestRepository.updateVerificationBodyIdByAccountId(verificationBodyId, accountId);
    }
}
