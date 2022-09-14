package uk.gov.pmrv.api.workflow.request.flow.rde.service;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestPayload;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;

@Service
@RequiredArgsConstructor
public class RdeCancelledService {

    private final RequestService requestService;

    public void cancel(final String requestId) {

        final Request request = requestService.findRequestById(requestId);
        final RequestPayload requestPayload = request.getPayload();
        final String regulatorAssignee = requestPayload.getRegulatorAssignee();

        requestService.addActionToRequest(request,
            null,
            RequestActionType.RDE_CANCELLED,
            regulatorAssignee);
    }
}
