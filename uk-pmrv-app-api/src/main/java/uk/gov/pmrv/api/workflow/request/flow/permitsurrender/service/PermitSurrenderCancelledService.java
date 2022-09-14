package uk.gov.pmrv.api.workflow.request.flow.permitsurrender.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;

@Service
@RequiredArgsConstructor
public class PermitSurrenderCancelledService {

    private final RequestService requestService;
    
    public void cancel(final String requestId) {

        final Request request = requestService.findRequestById(requestId);
        final String operatorAssignee = request.getPayload().getOperatorAssignee();

        requestService.addActionToRequest(request,
            null,
            RequestActionType.PERMIT_SURRENDER_APPLICATION_CANCELLED,
            operatorAssignee);
    }
}
