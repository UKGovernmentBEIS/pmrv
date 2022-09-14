package uk.gov.pmrv.api.workflow.request.flow.permitvariation.handler;

import java.util.Map;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import uk.gov.pmrv.api.common.domain.enumeration.RoleType;
import uk.gov.pmrv.api.common.domain.model.PmrvUser;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.workflow.request.StartProcessRequestService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestCreateActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestMetadataType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.flow.common.actionhandler.RequestCreateActionHandler;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.RequestCreateActionEmptyPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestParams;
import uk.gov.pmrv.api.workflow.request.flow.permitvariation.domain.PermitVariationRequestMetadata;
import uk.gov.pmrv.api.workflow.request.flow.permitvariation.domain.PermitVariationRequestPayload;

@Component
@RequiredArgsConstructor
public class PermitVariationCreateActionHandler implements RequestCreateActionHandler<RequestCreateActionEmptyPayload>{
    
    private final StartProcessRequestService startProcessRequestService;

    @Override
    public String process(Long accountId, RequestCreateActionType type, RequestCreateActionEmptyPayload payload,
            PmrvUser pmrvUser) {
    	final RoleType currentUserRoleType = pmrvUser.getRoleType();
    	
    	final PermitVariationRequestPayload requestPayload = PermitVariationRequestPayload.builder()
	        .payloadType(RequestPayloadType.PERMIT_VARIATION_REQUEST_PAYLOAD)
	        .build();
    	
    	if(currentUserRoleType == RoleType.OPERATOR) {
    		requestPayload.setOperatorAssignee(pmrvUser.getUserId());
    	} else if (currentUserRoleType == RoleType.REGULATOR) {
    		requestPayload.setRegulatorAssignee(pmrvUser.getUserId());
    	} else {
    		throw new BusinessException(ErrorCode.REQUEST_CREATE_ACTION_NOT_ALLOWED, currentUserRoleType);
    	}
    	
        RequestParams requestParams = RequestParams.builder()
                .type(RequestType.PERMIT_VARIATION)
                .accountId(accountId)
                .requestPayload(requestPayload)
                .processVars(Map.of(
                		BpmnProcessConstants.REQUEST_INITIATOR_ROLE_TYPE, currentUserRoleType.name()
                		))
				.requestMetadata(PermitVariationRequestMetadata.builder()
						.type(RequestMetadataType.PERMIT_VARIATION)
						.build())
                .build();

        final Request request = startProcessRequestService.startProcess(requestParams);

        return request.getId();
    }

    @Override
    public RequestCreateActionType getType() {
        return RequestCreateActionType.PERMIT_VARIATION;
    }

}