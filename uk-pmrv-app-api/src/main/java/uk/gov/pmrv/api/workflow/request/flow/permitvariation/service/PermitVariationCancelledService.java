package uk.gov.pmrv.api.workflow.request.flow.permitvariation.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.common.domain.enumeration.RoleType;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;

@Service
@RequiredArgsConstructor
public class PermitVariationCancelledService {

    private final RequestService requestService;

    public void cancel(final String requestId, final RoleType userRole) {

        final Request request = requestService.findRequestById(requestId);
        final String assignee = userRole == RoleType.OPERATOR ?
            request.getPayload().getOperatorAssignee() : request.getPayload().getRegulatorAssignee();

        requestService.addActionToRequest(request,
            null,
            RequestActionType.PERMIT_VARIATION_APPLICATION_CANCELLED,
            assignee);
    }
}
