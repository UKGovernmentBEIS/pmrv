package uk.gov.pmrv.api.workflow.request.flow.rde.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.rde.domain.RequestPayloadRdeable;

@Service
@RequiredArgsConstructor
public class RdeTerminatedService {

    private final RequestService requestService;

    public void terminate(final String requestId) {

        final Request request = requestService.findRequestById(requestId);
        final RequestPayloadRdeable requestPayload = (RequestPayloadRdeable) request.getPayload();
        requestPayload.cleanRdeData();
    }
}
