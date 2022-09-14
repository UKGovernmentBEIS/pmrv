package uk.gov.pmrv.api.workflow.request.flow.permitnotification.handler;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;

import uk.gov.pmrv.api.common.domain.model.PmrvUser;
import uk.gov.pmrv.api.workflow.request.StartProcessRequestService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestCreateActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestMetadataType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.flow.common.actionhandler.RequestCreateActionHandler;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.RequestCreateActionEmptyPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestParams;
import uk.gov.pmrv.api.workflow.request.flow.permitnotification.domain.PermitNotificationRequestMetadata;
import uk.gov.pmrv.api.workflow.request.flow.permitnotification.domain.PermitNotificationRequestPayload;

@Component
@RequiredArgsConstructor
public class PermitNotificationCreateActionHandler implements RequestCreateActionHandler<RequestCreateActionEmptyPayload> {
    
    private final StartProcessRequestService startProcessRequestService;

    @Override
    public String process(Long accountId, RequestCreateActionType type, RequestCreateActionEmptyPayload payload,
            PmrvUser pmrvUser) {
        RequestParams requestParams = RequestParams.builder()
                .type(RequestType.PERMIT_NOTIFICATION)
                .accountId(accountId)
                .requestPayload(PermitNotificationRequestPayload.builder()
                        .payloadType(RequestPayloadType.PERMIT_NOTIFICATION_REQUEST_PAYLOAD)
                        .operatorAssignee(pmrvUser.getUserId())
                        .build())
                .requestMetadata(PermitNotificationRequestMetadata.builder()
                        .type(RequestMetadataType.PERMIT_NOTIFICATION)
                        .build())
                .build();

        final Request request = startProcessRequestService.startProcess(requestParams);

        return request.getId();
    }

    @Override
    public RequestCreateActionType getType() {
        return RequestCreateActionType.PERMIT_NOTIFICATION;
    }
}
