package uk.gov.pmrv.api.workflow.request.flow.permitnotification.handler;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.Set;
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
import uk.gov.pmrv.api.workflow.request.flow.permitnotification.domain.PermitNotificationFollowUpRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitnotification.domain.PermitNotificationRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitnotification.domain.PermitNotificationReviewDecision;

@ExtendWith(MockitoExtension.class)
class PermitNotificationFollowUpInitializerTest {

    @InjectMocks
    private PermitNotificationFollowUpInitializer initializer;

    @Test
    void initializePayload() {

        final PermitNotificationReviewDecision reviewDecision = PermitNotificationReviewDecision.builder()
            .followUp(FollowUp.builder()
                .followUpRequest("the request")
                .followUpResponseExpirationDate(LocalDate.of(2023, 1, 1))
                .build())
            .build();

        final Request request = Request.builder()
            .type(RequestType.PERMIT_NOTIFICATION)
            .payload(PermitNotificationRequestPayload.builder()
                .reviewDecision(reviewDecision)
                .build())
            .build();

        final PermitNotificationFollowUpRequestTaskPayload expected =
            PermitNotificationFollowUpRequestTaskPayload.builder()
                .payloadType(RequestTaskPayloadType.PERMIT_NOTIFICATION_FOLLOW_UP_PAYLOAD)
                .followUpRequest("the request")
                .followUpResponseExpirationDate(LocalDate.of(2023, 1, 1))
                .build();

        RequestTaskPayload actual = initializer.initializePayload(request);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void getRequestTaskTypes() {
        assertThat(initializer.getRequestTaskTypes())
            .isEqualTo(Set.of(RequestTaskType.PERMIT_NOTIFICATION_FOLLOW_UP));
    }
}
