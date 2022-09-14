package uk.gov.pmrv.api.workflow.request.flow.permitissuance.review.mapper;

import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.common.domain.enumeration.RoleType;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestAction;
import uk.gov.pmrv.api.workflow.request.core.domain.dto.RequestActionDTO;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.transform.RequestActionCustomMapper;
import uk.gov.pmrv.api.workflow.request.core.transform.RequestActionMapper;
import uk.gov.pmrv.api.workflow.request.flow.permitissuance.review.domain.PermitIssuanceApplicationGrantedRequestActionPayload;

@Service
public class PermitApplicationGrantedOperatorMapper implements RequestActionCustomMapper {
    
    private final RequestActionMapper requestActionMapper = Mappers.getMapper(RequestActionMapper.class);

    private final PermitIssuanceReviewRequestActionMapper permitIssuanceReviewRequestActionMapper = Mappers
            .getMapper(PermitIssuanceReviewRequestActionMapper.class);
    

    @Override
    public RequestActionDTO toRequestActionDTO(final RequestAction requestAction) {

        final PermitIssuanceApplicationGrantedRequestActionPayload entityPayload =
            (PermitIssuanceApplicationGrantedRequestActionPayload) requestAction.getPayload();

        final RequestActionDTO requestActionDTO = requestActionMapper.toRequestActionDTOIgnorePayload(requestAction);

        final PermitIssuanceApplicationGrantedRequestActionPayload dtoPayload = 
                permitIssuanceReviewRequestActionMapper.cloneGrantedPayloadIgnoreReasonAndDecisions(entityPayload);

        requestActionDTO.setPayload(dtoPayload);
        
        return requestActionDTO;
    }

    @Override
    public RequestActionType getRequestActionType() {
        return RequestActionType.PERMIT_ISSUANCE_APPLICATION_GRANTED;
    }

    @Override
    public RoleType getUserRoleType() {
        return RoleType.OPERATOR;
    }
}
