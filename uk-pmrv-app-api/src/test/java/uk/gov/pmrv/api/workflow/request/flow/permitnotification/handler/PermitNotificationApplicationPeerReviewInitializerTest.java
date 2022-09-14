package uk.gov.pmrv.api.workflow.request.flow.permitnotification.handler;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestStatus;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.flow.permitnotification.domain.NonSignificantChange;
import uk.gov.pmrv.api.workflow.request.flow.permitnotification.domain.NonSignificantChangeRelatedChangeType;
import uk.gov.pmrv.api.workflow.request.flow.permitnotification.domain.PermitNotification;
import uk.gov.pmrv.api.workflow.request.flow.permitnotification.domain.PermitNotificationApplicationReviewRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitnotification.domain.PermitNotificationRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitnotification.domain.PermitNotificationReviewDecision;
import uk.gov.pmrv.api.workflow.request.flow.permitnotification.domain.PermitNotificationReviewDecisionType;
import uk.gov.pmrv.api.workflow.request.flow.permitnotification.domain.PermitNotificationType;

@ExtendWith(MockitoExtension.class)
class PermitNotificationApplicationPeerReviewInitializerTest {

    @InjectMocks
    private PermitNotificationApplicationPeerReviewInitializer initializer;

    @Test
    void initializePayload() {
        UUID fileUuid = UUID.randomUUID();
        String filename = "document";
        final PermitNotification permitNotification = NonSignificantChange.builder()
                .type(PermitNotificationType.NON_SIGNIFICANT_CHANGE)
                .description("description")
                .relatedChanges(Arrays.asList(
                        NonSignificantChangeRelatedChangeType.MONITORING_METHODOLOGY_PLAN,
                        NonSignificantChangeRelatedChangeType.MONITORING_PLAN
                    )
                )
                .documents(Set.of(fileUuid))
                .build();
        final PermitNotificationReviewDecision decision = PermitNotificationReviewDecision.builder()
                .type(PermitNotificationReviewDecisionType.ACCEPTED)
                .notes("notes")
                .officialNotice("officialNotice")
                .build();
        final Request request = Request.builder()
                .id("1")
                .type(RequestType.PERMIT_NOTIFICATION)
                .status(RequestStatus.IN_PROGRESS)
                .payload(PermitNotificationRequestPayload.builder()
                        .payloadType(RequestPayloadType.PERMIT_NOTIFICATION_REQUEST_PAYLOAD)
                        .permitNotification(permitNotification)
                        .permitNotificationAttachments(Map.of(fileUuid, filename))
                        .reviewDecision(decision)
                        .build())
                .build();

        final RequestTaskPayload requestTaskPayload = initializer.initializePayload(request);
        assertThat(requestTaskPayload.getPayloadType()).isEqualTo(
                RequestTaskPayloadType.PERMIT_NOTIFICATION_APPLICATION_PEER_REVIEW_PAYLOAD);
        assertThat(requestTaskPayload).isInstanceOf(PermitNotificationApplicationReviewRequestTaskPayload.class);
        assertThat(((PermitNotificationApplicationReviewRequestTaskPayload) requestTaskPayload).getPermitNotification())
                .isEqualTo(permitNotification);
        assertThat(((PermitNotificationApplicationReviewRequestTaskPayload) requestTaskPayload).getPermitNotificationAttachments())
                .isEqualTo(Map.of(fileUuid, filename));
        assertThat(((PermitNotificationApplicationReviewRequestTaskPayload) requestTaskPayload).getReviewDecision())
                .isEqualTo(decision);
    }

    @Test
    void getRequestTaskTypes() {
        assertThat(initializer.getRequestTaskTypes()).containsExactly(
                RequestTaskType.PERMIT_NOTIFICATION_APPLICATION_PEER_REVIEW);
    }
}
