package uk.gov.pmrv.api.workflow.request.flow.rfi.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;

@Service
@RequiredArgsConstructor
public class RfiExpiredService {

    private final RequestService requestService;

    public void expire(final String requestId) {

        final Request request = requestService.findRequestById(requestId);

        requestService.addActionToRequest(request,
            null,
            RequestActionType.RFI_EXPIRED,
            null);
    }
}
