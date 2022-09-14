package uk.gov.pmrv.api.workflow.request.core.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.pmrv.api.account.service.AccountQueryService;
import uk.gov.pmrv.api.common.domain.enumeration.CompetentAuthority;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestStatus;
import uk.gov.pmrv.api.workflow.request.core.repository.RequestRepository;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestParams;

@RequiredArgsConstructor
@Service
public class RequestCreateService {

    private final RequestRepository requestRepository;
    private final AccountQueryService accountQueryService;

    /**
     * Create and persist request.
     *
     * @param requestParams the {@link RequestParams}
     * @param status        the {@link RequestStatus}
     * @return the request created
     */
    @Transactional
    public Request createRequest(RequestParams requestParams, RequestStatus status) {
        Request request = new Request();
        request.setId(requestParams.getRequestId());
        request.setType(requestParams.getType());
        request.setStatus(status);
        request.setCompetentAuthority(populateRequestCompetentAuthority(requestParams));
        request.setVerificationBodyId(populateRequestVerificationBody(requestParams));
        request.setAccountId(requestParams.getAccountId());
        request.setPayload(requestParams.getRequestPayload());
        request.setMetadata(requestParams.getRequestMetadata());

        return requestRepository.save(request);
    }

    private CompetentAuthority populateRequestCompetentAuthority(RequestParams requestParams) {
        return requestParams.getCa() != null
            ? requestParams.getCa()
            : accountQueryService.getAccountCa(requestParams.getAccountId());
    }

    private Long populateRequestVerificationBody(RequestParams requestParams) {
        return requestParams.getVerificationBodyId() != null
            ? requestParams.getVerificationBodyId()
            : accountQueryService.getAccountVerificationBodyId(requestParams.getAccountId()).orElse(null);
    }
}
