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
import uk.gov.pmrv.api.workflow.request.flow.permitnotification.domain.PermitNotificationFollowUpApplicationReviewRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitnotification.domain.PermitNotificationFollowUpReviewDecision;
import uk.gov.pmrv.api.workflow.request.flow.permitnotification.domain.PermitNotificationFollowUpReviewDecisionType;
import uk.gov.pmrv.api.workflow.request.flow.permitnotification.domain.PermitNotificationRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitnotification.domain.PermitNotificationReviewDecision;
import uk.gov.pmrv.api.workflow.request.flow.permitnotification.domain.PermitNotificationType;
import uk.gov.pmrv.api.workflow.request.flow.permitnotification.domain.TemporaryChange;

@ExtendWith(MockitoExtension.class)
class PermitNotificationFollowUpReviewInitializerTest {

    @InjectMocks
    private PermitNotificationFollowUpReviewInitializer initializer;

    @Test
    void initializePayload() {

        final PermitNotificationReviewDecision reviewDecision = PermitNotificationReviewDecision.builder()
            .followUp(FollowUp.builder()
                .followUpRequest("the request")
                .followUpResponseExpirationDate(LocalDate.of(2023, 1, 1))
                .build())
            .build();

        final PermitNotificationFollowUpReviewDecision followUpReviewDecision = PermitNotificationFollowUpReviewDecision
            .builder()
            .type(PermitNotificationFollowUpReviewDecisionType.AMENDS_NEEDED)
            .changesRequired("the changes")
            .build();

        final UUID file = UUID.randomUUID();
        final Request request = Request.builder()
            .type(RequestType.PERMIT_NOTIFICATION)
            .payload(PermitNotificationRequestPayload.builder()
                .permitNotification(TemporaryChange.builder().type(PermitNotificationType.TEMPORARY_CHANGE).build())
                .followUpResponse("the response")
                .followUpResponseFiles(Set.of(file))
                .followUpResponseAttachments(Map.of(file, "filename"))
                .reviewDecision(reviewDecision)
                .followUpReviewDecision(followUpReviewDecision)
                .followUpReviewSectionsCompleted(Map.of("section", true))
                .followUpSectionsCompleted(Map.of("followUpSection", true))
                .build())
            .build();

        final PermitNotificationFollowUpApplicationReviewRequestTaskPayload expected =
            PermitNotificationFollowUpApplicationReviewRequestTaskPayload.builder()
                .payloadType(RequestTaskPayloadType.PERMIT_NOTIFICATION_FOLLOW_UP_APPLICATION_REVIEW_PAYLOAD)
                .permitNotificationType(PermitNotificationType.TEMPORARY_CHANGE)
                .submissionDate(LocalDate.now())
                .followUpRequest("the request")
                .followUpResponseExpirationDate(LocalDate.of(2023, 1, 1))
                .followUpResponse("the response")
                .followUpFiles(Set.of(file))
                .followUpAttachments(Map.of(file, "filename"))
                .reviewSectionsCompleted(Map.of("section", true))
                .followUpSectionsCompleted(Map.of("followUpSection", true))
                .reviewDecision(followUpReviewDecision)
                .build();

        final RequestTaskPayload actual = initializer.initializePayload(request);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void getRequestTaskTypes() {
        assertThat(initializer.getRequestTaskTypes())
            .isEqualTo(Set.of(RequestTaskType.PERMIT_NOTIFICATION_FOLLOW_UP_APPLICATION_REVIEW));
    }
}
