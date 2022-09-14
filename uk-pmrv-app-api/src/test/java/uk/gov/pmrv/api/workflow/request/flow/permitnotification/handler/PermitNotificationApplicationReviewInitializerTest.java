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
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.flow.permitnotification.domain.NonSignificantChange;
import uk.gov.pmrv.api.workflow.request.flow.permitnotification.domain.NonSignificantChangeRelatedChangeType;
import uk.gov.pmrv.api.workflow.request.flow.permitnotification.domain.PermitNotification;
import uk.gov.pmrv.api.workflow.request.flow.permitnotification.domain.PermitNotificationApplicationReviewRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitnotification.domain.PermitNotificationRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitnotification.domain.PermitNotificationType;

@ExtendWith(MockitoExtension.class)
class PermitNotificationApplicationReviewInitializerTest {

    @InjectMocks
    private PermitNotificationApplicationReviewInitializer initializer;

    @Test
    void initializePayload() {
        UUID fileUuid = UUID.randomUUID();
        String fileName = "fileName";

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
        final Request request = Request.builder()
            .type(RequestType.PERMIT_NOTIFICATION)
            .payload(PermitNotificationRequestPayload.builder()
                .permitNotification(permitNotification)
                .permitNotificationAttachments(Map.of(fileUuid, fileName))
                .build())
            .build();

        PermitNotificationApplicationReviewRequestTaskPayload expected =
            PermitNotificationApplicationReviewRequestTaskPayload.builder()
                .payloadType(RequestTaskPayloadType.PERMIT_NOTIFICATION_APPLICATION_REVIEW_PAYLOAD)
                .permitNotification(permitNotification)
                .permitNotificationAttachments(Map.of(fileUuid, fileName))
                .build();

        // Invoke
        RequestTaskPayload actual = initializer.initializePayload(request);

        // Verify
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void getRequestTaskTypes() {
        assertThat(initializer.getRequestTaskTypes())
            .isEqualTo(Set.of(RequestTaskType.PERMIT_NOTIFICATION_APPLICATION_REVIEW));
    }
}
