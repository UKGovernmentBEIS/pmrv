package uk.gov.pmrv.api.workflow.request.flow.rfi.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.rfi.domain.RequestPayloadRfiable;

@Service
@RequiredArgsConstructor
public class RfiTerminatedService {

    private final RequestService requestService;

    public void terminate(final String requestId) {

        final Request request = requestService.findRequestById(requestId);
        final RequestPayloadRfiable requestPayload = (RequestPayloadRfiable) request.getPayload();
        requestPayload.cleanRfiData();
    }
}
