package uk.gov.pmrv.api.workflow.request.flow.permitnotification.handler;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.flow.permitnotification.domain.FollowUp;
import uk.gov.pmrv.api.workflow.request.flow.permitnotification.domain.PermitNotificationFollowUpReviewDecision;
import uk.gov.pmrv.api.workflow.request.flow.permitnotification.domain.PermitNotificationFollowUpReviewDecisionType;
import uk.gov.pmrv.api.workflow.request.flow.permitnotification.domain.PermitNotificationFollowUpWaitForAmendsRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitnotification.domain.PermitNotificationRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitnotification.domain.PermitNotificationReviewDecision;

@ExtendWith(MockitoExtension.class)
class PermitNotificationFollowUpWaitForAmendsInitializerTest {

    @InjectMocks
    private PermitNotificationFollowUpWaitForAmendsInitializer initializer;

    @Test
    void initializePayload() {

        final UUID amendFile = UUID.randomUUID();
        final UUID responseFile = UUID.randomUUID();
        final UUID nonRelevantFile = UUID.randomUUID();
        final PermitNotificationFollowUpReviewDecision reviewDecision = PermitNotificationFollowUpReviewDecision.builder()
            .type(PermitNotificationFollowUpReviewDecisionType.AMENDS_NEEDED)
            .notes("the notes")
            .files(Set.of(amendFile))
            .changesRequired("the changes required")
            .dueDate(LocalDate.of(2023, 1, 1))
            .build();

        final Map<UUID, String> files = Map.of(amendFile, "amendFile", responseFile, "responseFile", nonRelevantFile, "non-relevant-filename");
        final Request request = Request.builder()
            .type(RequestType.PERMIT_NOTIFICATION)
            .payload(PermitNotificationRequestPayload.builder()
                .reviewDecision(PermitNotificationReviewDecision.builder().followUp(FollowUp.builder()
                    .followUpRequest("follow up request").build())
                    .build())
                .followUpResponse("follow up response")
                .followUpResponseFiles(Set.of(responseFile))
                .followUpReviewDecision(reviewDecision)
                .followUpResponseAttachments(files)
                .build())
            .build();

        final PermitNotificationFollowUpWaitForAmendsRequestTaskPayload expected =
            PermitNotificationFollowUpWaitForAmendsRequestTaskPayload.builder()
                .payloadType(RequestTaskPayloadType.PERMIT_NOTIFICATION_FOLLOW_UP_WAIT_FOR_AMENDS_PAYLOAD)
                .followUpRequest("follow up request")
                .followUpResponse("follow up response")
                .followUpFiles(Set.of(responseFile))
                .reviewDecision(reviewDecision)
                .followUpResponseAttachments(Map.of(amendFile, "amendFile", responseFile, "responseFile"))
                .build();

        final RequestTaskPayload actual = initializer.initializePayload(request);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void getRequestTaskTypes() {
        assertThat(initializer.getRequestTaskTypes()).isEqualTo(Set.of(RequestTaskType.PERMIT_NOTIFICATION_FOLLOW_UP_WAIT_FOR_AMENDS));
    }
}
