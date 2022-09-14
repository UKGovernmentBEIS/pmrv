package uk.gov.pmrv.api.workflow.request.flow.rfi.service;

import java.time.LocalDateTime;
import java.util.HashMap;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestMetadataRfiable;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestPayload;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.rfi.domain.RequestPayloadRfiable;
import uk.gov.pmrv.api.workflow.request.flow.rfi.domain.RfiResponseSubmittedRequestActionPayload;

@Service
@RequiredArgsConstructor
public class RfiRespondedService {

    private final RequestService requestService;

    public void respond(final String requestId) {

        final Request request = requestService.findRequestById(requestId);
        
        final RequestPayload requestPayload = request.getPayload();
        final RequestPayloadRfiable requestRfiablePayload = (RequestPayloadRfiable) request.getPayload();

        final String operatorAssignee = requestPayload.getOperatorAssignee();

        // Set RFI response date for permit PDF
        RequestMetadataRfiable rfiMetadata = (RequestMetadataRfiable) request.getMetadata();
        rfiMetadata.getRfiResponseDates().add(LocalDateTime.now());

        // write timeline action
        final RfiResponseSubmittedRequestActionPayload timelinePayload =
            RfiResponseSubmittedRequestActionPayload
                .builder()
                .payloadType(RequestActionPayloadType.RFI_RESPONSE_SUBMITTED_PAYLOAD)
                .rfiResponsePayload(requestRfiablePayload.getRfiData().getRfiResponsePayload())
                .rfiQuestionPayload(requestRfiablePayload.getRfiData().getRfiQuestionPayload())
                .rfiAttachments(new HashMap<>(requestRfiablePayload.getRfiData().getRfiAttachments()))
                .build();

        requestService.addActionToRequest(request,
            timelinePayload,
            RequestActionType.RFI_RESPONSE_SUBMITTED,
            operatorAssignee);
    }
}
