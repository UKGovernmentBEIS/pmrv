package uk.gov.pmrv.api.workflow.request.flow.permitnotification.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.common.domain.enumeration.RoleType;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestAction;
import uk.gov.pmrv.api.workflow.request.core.domain.dto.RequestActionDTO;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.flow.permitnotification.domain.PermitNotificationFollowUpReturnedForAmendsRequestActionPayload;

@ExtendWith(MockitoExtension.class)
class PermitNotificationFollowUpReturnedForAmendsOperatorMapperTest {

    @InjectMocks
    private PermitNotificationFollowUpReturnedForAmendsOperatorMapper mapper;

    @Test
    void toRequestActionDTO() {

        final RequestAction requestAction = RequestAction.builder()
            .type(RequestActionType.PERMIT_NOTIFICATION_FOLLOW_UP_RETURNED_FOR_AMENDS)
            .payload(PermitNotificationFollowUpReturnedForAmendsRequestActionPayload.builder()
                .payloadType(RequestActionPayloadType.PERMIT_NOTIFICATION_FOLLOW_UP_RETURNED_FOR_AMENDS_PAYLOAD)
                .changesRequired("the changes required")
                .notes("the notes")
                .build())
            .build();

        final RequestActionDTO result = mapper.toRequestActionDTO(requestAction);

        assertThat(result.getType()).isEqualTo(requestAction.getType());
        final PermitNotificationFollowUpReturnedForAmendsRequestActionPayload resultPayload =
            (PermitNotificationFollowUpReturnedForAmendsRequestActionPayload) result.getPayload();
        assertThat(resultPayload.getNotes()).isNull();
        assertThat(resultPayload.getChangesRequired()).isEqualTo("the changes required");
    }
    
    @Test
    void getRequestActionType() {
        assertThat(mapper.getRequestActionType()).isEqualTo(
            RequestActionType.PERMIT_NOTIFICATION_FOLLOW_UP_RETURNED_FOR_AMENDS);
    }

    @Test
    void getUserRoleType() {
        assertThat(mapper.getUserRoleType()).isEqualTo(RoleType.OPERATOR);
    }
}
