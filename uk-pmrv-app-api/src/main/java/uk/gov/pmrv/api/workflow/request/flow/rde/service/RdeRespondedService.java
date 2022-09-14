package uk.gov.pmrv.api.workflow.request.flow.rde.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestActionPayload;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestPayload;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.rde.domain.RdeDecisionType;
import uk.gov.pmrv.api.workflow.request.flow.rde.domain.RdeRejectedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.rde.domain.RequestPayloadRdeable;

@Service
@RequiredArgsConstructor
public class RdeRespondedService {

    private final RequestService requestService;

    public void respond(final String requestId) {

        final Request request = requestService.findRequestById(requestId);
        final RequestPayload requestPayload = request.getPayload();
        final RequestPayloadRdeable requestPayloadRdeable = (RequestPayloadRdeable) requestPayload;
        final String reason = requestPayloadRdeable.getRdeData().getRdeDecisionPayload().getReason();

        final boolean isAccepted = requestPayloadRdeable.getRdeData().getRdeDecisionPayload().getDecision() == RdeDecisionType.ACCEPTED;
        final RequestActionType actionType = isAccepted ? RequestActionType.RDE_ACCEPTED : RequestActionType.RDE_REJECTED;
        final RequestActionPayload timelinePayload = isAccepted ? null :
            RdeRejectedRequestActionPayload
                .builder()
                .payloadType(RequestActionPayloadType.RDE_REJECTED_PAYLOAD)
                .reason(reason)
                .build();

        requestService.addActionToRequest(request,
            timelinePayload,
            actionType,
            requestPayload.getOperatorAssignee());
    }
}
