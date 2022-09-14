package uk.gov.pmrv.api.workflow.request.flow.permitsurrender.mapper;

import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;

import uk.gov.pmrv.api.common.domain.enumeration.RoleType;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestAction;
import uk.gov.pmrv.api.workflow.request.core.domain.dto.RequestActionDTO;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.transform.RequestActionCustomMapper;
import uk.gov.pmrv.api.workflow.request.core.transform.RequestActionMapper;
import uk.gov.pmrv.api.workflow.request.flow.permitsurrender.domain.PermitSurrenderApplicationGrantedRequestActionPayload;

@Service
public class PermitSurrenderApplicationGrantedOperatorMapper implements RequestActionCustomMapper {
    
    private final RequestActionMapper requestActionMapper = Mappers.getMapper(RequestActionMapper.class);

    private final PermitSurrenderRequestActionPayloadMapper requestActionPayloadMapper = Mappers
            .getMapper(PermitSurrenderRequestActionPayloadMapper.class);

    @Override
    public RequestActionDTO toRequestActionDTO(RequestAction requestAction) {
        PermitSurrenderApplicationGrantedRequestActionPayload actionPayload = (PermitSurrenderApplicationGrantedRequestActionPayload) requestAction
                .getPayload();

        RequestActionDTO requestActionDTO = requestActionMapper.toRequestActionDTOIgnorePayload(requestAction);

        PermitSurrenderApplicationGrantedRequestActionPayload dtoPayload = requestActionPayloadMapper
                .cloneGrantedPayloadIgnoreReason(actionPayload);

        requestActionDTO.setPayload(dtoPayload);
        return requestActionDTO;
    }

    @Override
    public RequestActionType getRequestActionType() {
        return RequestActionType.PERMIT_SURRENDER_APPLICATION_GRANTED;
    }

    @Override
    public RoleType getUserRoleType() {
        return RoleType.OPERATOR;
    }
}
