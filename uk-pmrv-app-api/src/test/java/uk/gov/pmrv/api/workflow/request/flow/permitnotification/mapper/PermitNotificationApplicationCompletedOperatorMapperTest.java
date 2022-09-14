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
import uk.gov.pmrv.api.workflow.request.flow.permitnotification.domain.PermitNotificationFollowUpApplicationReviewSubmittedDecisionRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitnotification.domain.PermitNotificationFollowUpReviewDecision;

@ExtendWith(MockitoExtension.class)
class PermitNotificationApplicationCompletedOperatorMapperTest {

    @InjectMocks
    private PermitNotificationApplicationCompletedOperatorMapper mapper;

    @Test
    void toRequestActionDTO() {

        final RequestAction requestAction = RequestAction.builder()
            .type(RequestActionType.PERMIT_NOTIFICATION_APPLICATION_COMPLETED)
            .payload(PermitNotificationFollowUpApplicationReviewSubmittedDecisionRequestActionPayload.builder()
                .payloadType(RequestActionPayloadType.PERMIT_NOTIFICATION_APPLICATION_COMPLETED_PAYLOAD)
                .reviewDecision(PermitNotificationFollowUpReviewDecision.builder()
                    .notes("the notes")
                    .changesRequired("the changes required")
                    .build())
                .build())
            .build();

        final RequestActionDTO result = mapper.toRequestActionDTO(requestAction);

        assertThat(result.getType()).isEqualTo(requestAction.getType());
        final PermitNotificationFollowUpApplicationReviewSubmittedDecisionRequestActionPayload resultPayload =
            (PermitNotificationFollowUpApplicationReviewSubmittedDecisionRequestActionPayload) result.getPayload();
        assertThat(resultPayload.getReviewDecision().getNotes()).isNull();
        assertThat(resultPayload.getReviewDecision().getChangesRequired()).isEqualTo("the changes required");
    }

    @Test
    void getRequestActionType() {
        assertThat(mapper.getRequestActionType()).isEqualTo(RequestActionType.PERMIT_NOTIFICATION_APPLICATION_COMPLETED);
    }

    @Test
    void getUserRoleType() {
        assertThat(mapper.getUserRoleType()).isEqualTo(RoleType.OPERATOR);
    }
}
