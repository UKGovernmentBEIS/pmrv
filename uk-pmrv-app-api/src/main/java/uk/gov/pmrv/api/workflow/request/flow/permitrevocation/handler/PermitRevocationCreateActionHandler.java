package uk.gov.pmrv.api.workflow.request.flow.permitrevocation.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.pmrv.api.common.domain.model.PmrvUser;
import uk.gov.pmrv.api.workflow.request.StartProcessRequestService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestCreateActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.flow.common.actionhandler.RequestCreateActionHandler;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.RequestCreateActionEmptyPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestParams;
import uk.gov.pmrv.api.workflow.request.flow.permitrevocation.domain.PermitRevocationRequestPayload;

@Component
@RequiredArgsConstructor
public class PermitRevocationCreateActionHandler implements RequestCreateActionHandler<RequestCreateActionEmptyPayload> {

    private final StartProcessRequestService startProcessRequestService;

    @Override
    public String process(final Long accountId, 
                          final RequestCreateActionType type, 
                          final RequestCreateActionEmptyPayload payload,
                          final PmrvUser pmrvUser) {
        
        final RequestParams requestParams = RequestParams.builder()
            .type(RequestType.PERMIT_REVOCATION)
            .accountId(accountId)
            .requestPayload(PermitRevocationRequestPayload.builder()
                .payloadType(RequestPayloadType.PERMIT_REVOCATION_REQUEST_PAYLOAD)
                .regulatorAssignee(pmrvUser.getUserId())
                .build())
            .build();

        final Request request = startProcessRequestService.startProcess(requestParams);

        return request.getId();
    }

    @Override
    public RequestCreateActionType getType() {
        return RequestCreateActionType.PERMIT_REVOCATION;
    }

}
