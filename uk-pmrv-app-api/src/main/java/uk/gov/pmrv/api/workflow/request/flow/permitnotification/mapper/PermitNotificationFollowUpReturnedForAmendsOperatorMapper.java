package uk.gov.pmrv.api.workflow.request.flow.permitnotification.mapper;

import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.common.domain.enumeration.RoleType;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestAction;
import uk.gov.pmrv.api.workflow.request.core.domain.dto.RequestActionDTO;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.transform.RequestActionCustomMapper;
import uk.gov.pmrv.api.workflow.request.core.transform.RequestActionMapper;
import uk.gov.pmrv.api.workflow.request.flow.permitnotification.domain.PermitNotificationFollowUpReturnedForAmendsRequestActionPayload;

@Service
public class PermitNotificationFollowUpReturnedForAmendsOperatorMapper implements RequestActionCustomMapper {

    private final RequestActionMapper requestActionMapper = Mappers.getMapper(RequestActionMapper.class);

    private final PermitNotificationMapper requestActionPayloadMapper =
        Mappers.getMapper(PermitNotificationMapper.class);

    @Override
    public RequestActionDTO toRequestActionDTO(RequestAction requestAction) {

        final PermitNotificationFollowUpReturnedForAmendsRequestActionPayload actionPayload =
            (PermitNotificationFollowUpReturnedForAmendsRequestActionPayload) requestAction.getPayload();

        final RequestActionDTO requestActionDTO = requestActionMapper.toRequestActionDTOIgnorePayload(requestAction);

        final PermitNotificationFollowUpReturnedForAmendsRequestActionPayload dtoPayload =
            requestActionPayloadMapper.cloneReturnedForAmendsIgnoreNotes(actionPayload,
                RequestActionPayloadType.PERMIT_NOTIFICATION_FOLLOW_UP_RETURNED_FOR_AMENDS_PAYLOAD);

        requestActionDTO.setPayload(dtoPayload);

        return requestActionDTO;
    }

    @Override
    public RequestActionType getRequestActionType() {
        return RequestActionType.PERMIT_NOTIFICATION_FOLLOW_UP_RETURNED_FOR_AMENDS;
    }

    @Override
    public RoleType getUserRoleType() {
        return RoleType.OPERATOR;
    }
}
