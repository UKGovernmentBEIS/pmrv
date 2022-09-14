package uk.gov.pmrv.api.workflow.request.flow.permitissuance.review.mapper;

import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.common.domain.enumeration.RoleType;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestAction;
import uk.gov.pmrv.api.workflow.request.core.domain.dto.RequestActionDTO;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.transform.RequestActionCustomMapper;
import uk.gov.pmrv.api.workflow.request.core.transform.RequestActionMapper;
import uk.gov.pmrv.api.workflow.request.flow.permitissuance.review.domain.PermitIssuanceApplicationDeemedWithdrawnRequestActionPayload;

@Service
public class PermitApplicationDeemedWithdrawnOperatorMapper implements RequestActionCustomMapper {

    private final RequestActionMapper requestActionMapper = Mappers.getMapper(RequestActionMapper.class);

    private final PermitIssuanceReviewRequestActionMapper permitIssuanceReviewRequestActionMapper = Mappers
            .getMapper(PermitIssuanceReviewRequestActionMapper.class);


    @Override
    public RequestActionDTO toRequestActionDTO(final RequestAction requestAction) {

        final PermitIssuanceApplicationDeemedWithdrawnRequestActionPayload entityPayload =
            (PermitIssuanceApplicationDeemedWithdrawnRequestActionPayload) requestAction.getPayload();

        final RequestActionDTO requestActionDTO = requestActionMapper.toRequestActionDTOIgnorePayload(requestAction);

        final PermitIssuanceApplicationDeemedWithdrawnRequestActionPayload dtoPayload =
                permitIssuanceReviewRequestActionMapper.cloneDeemedWithdrawnPayloadIgnoreReason(entityPayload);
        
        requestActionDTO.setPayload(dtoPayload);

        return requestActionDTO;
    }

    @Override
    public RequestActionType getRequestActionType() {
        return RequestActionType.PERMIT_ISSUANCE_APPLICATION_DEEMED_WITHDRAWN;
    }

    @Override
    public RoleType getUserRoleType() {
        return RoleType.OPERATOR;
    }
}
