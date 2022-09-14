package uk.gov.pmrv.api.workflow.request.flow.permitnotification.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.common.domain.model.PmrvUser;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestActionPayload;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.permitnotification.domain.NonSignificantChange;
import uk.gov.pmrv.api.workflow.request.flow.permitnotification.domain.NonSignificantChangeRelatedChangeType;
import uk.gov.pmrv.api.workflow.request.flow.permitnotification.domain.PermitNotification;
import uk.gov.pmrv.api.workflow.request.flow.permitnotification.domain.PermitNotificationApplicationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitnotification.domain.PermitNotificationApplicationSubmittedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitnotification.domain.PermitNotificationContainer;
import uk.gov.pmrv.api.workflow.request.flow.permitnotification.domain.PermitNotificationRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitnotification.domain.PermitNotificationSaveApplicationRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitnotification.domain.PermitNotificationType;

@ExtendWith(MockitoExtension.class)
class RequestPermitNotificationServiceTest {

    @InjectMocks
    private RequestPermitNotificationService service;

    @Mock
    private RequestService requestService;

    @Mock
    private RequestTaskService requestTaskService;

    @Mock
    private PermitNotificationSubmitValidatorService permitNotificationSubmitValidatorService;

    @Test
    void applySavePayload_existing_task_payload() {
        PermitNotificationApplicationSubmitRequestTaskPayload taskPayload =
            PermitNotificationApplicationSubmitRequestTaskPayload.builder()
                .payloadType(RequestTaskPayloadType.PERMIT_NOTIFICATION_APPLICATION_SUBMIT_PAYLOAD)
                .permitNotification(
                    NonSignificantChange.builder().type(PermitNotificationType.NON_SIGNIFICANT_CHANGE).build())
                .sectionsCompleted(Map.of("SECTION_1", true))
                .build();

        PermitNotificationSaveApplicationRequestTaskActionPayload actionPayload =
            PermitNotificationSaveApplicationRequestTaskActionPayload.builder()
                .payloadType(RequestTaskActionPayloadType.PERMIT_NOTIFICATION_SAVE_APPLICATION_PAYLOAD)
                .permitNotification(NonSignificantChange.builder()
                    .type(PermitNotificationType.NON_SIGNIFICANT_CHANGE)
                    .description("description")
                    .relatedChanges(Arrays.asList(
                            NonSignificantChangeRelatedChangeType.MONITORING_METHODOLOGY_PLAN,
                            NonSignificantChangeRelatedChangeType.MONITORING_PLAN
                        )
                    )
                    .build()
                )
                .sectionsCompleted(Map.of())
                .build();

        RequestTask requestTask = RequestTask.builder().id(1L).payload(taskPayload)
            .type(RequestTaskType.PERMIT_NOTIFICATION_APPLICATION_SUBMIT).build();

        // Invoke
        service.applySavePayload(actionPayload, requestTask);

        // Verify
        assertThat(taskPayload.getPermitNotification()).isEqualTo(actionPayload.getPermitNotification());
        assertThat(taskPayload.getSectionsCompleted()).isEmpty();
        verify(requestTaskService, times(1)).saveRequestTask(requestTask);
    }

    @Test
    void applySavePayload_new_task_payload() {
        PermitNotificationApplicationSubmitRequestTaskPayload taskPayload = null;

        PermitNotificationSaveApplicationRequestTaskActionPayload actionPayload =
            PermitNotificationSaveApplicationRequestTaskActionPayload.builder()
                .payloadType(RequestTaskActionPayloadType.PERMIT_NOTIFICATION_SAVE_APPLICATION_PAYLOAD)
                .permitNotification(NonSignificantChange.builder()
                    .type(PermitNotificationType.NON_SIGNIFICANT_CHANGE)
                    .description("description")
                    .relatedChanges(Arrays.asList(
                            NonSignificantChangeRelatedChangeType.MONITORING_METHODOLOGY_PLAN,
                            NonSignificantChangeRelatedChangeType.MONITORING_PLAN
                        )
                    )
                    .build()
                )
                .sectionsCompleted(Map.of("SECTION_1", true))
                .build();

        RequestTask requestTask = RequestTask.builder().id(1L).payload(taskPayload)
            .type(RequestTaskType.PERMIT_NOTIFICATION_APPLICATION_SUBMIT).build();

        service.applySavePayload(actionPayload, requestTask);

        PermitNotificationApplicationSubmitRequestTaskPayload savedTaskPayload =
            (PermitNotificationApplicationSubmitRequestTaskPayload) requestTask.getPayload();

        assertThat(savedTaskPayload.getPermitNotification()).isEqualTo(actionPayload.getPermitNotification());
        assertThat(savedTaskPayload.getSectionsCompleted()).isEqualTo(actionPayload.getSectionsCompleted());
        verify(requestTaskService, times(1)).saveRequestTask(requestTask);
    }

    @Test
    void applySubmitPayload() {
        PmrvUser authUser = PmrvUser.builder().userId("user").build();

        PermitNotificationRequestPayload requestPayload = PermitNotificationRequestPayload.builder()
            .payloadType(RequestPayloadType.PERMIT_NOTIFICATION_REQUEST_PAYLOAD).build();
        Request request =
            Request.builder().id("1").payload(requestPayload).type(RequestType.PERMIT_NOTIFICATION).build();
        PermitNotification permitNotification = NonSignificantChange.builder()
            .relatedChanges(Arrays.asList(
                    NonSignificantChangeRelatedChangeType.MONITORING_METHODOLOGY_PLAN,
                    NonSignificantChangeRelatedChangeType.MONITORING_PLAN
                )
            )
            .documents(Set.of(UUID.randomUUID()))
            .build();

        PermitNotificationApplicationSubmitRequestTaskPayload taskPayload =
            PermitNotificationApplicationSubmitRequestTaskPayload.builder()
                .payloadType(RequestTaskPayloadType.PERMIT_NOTIFICATION_APPLICATION_SUBMIT_PAYLOAD)
                .permitNotification(permitNotification)
                .sectionsCompleted(Map.of("SECTION_1", true))
                .build();
        RequestTask requestTask = RequestTask.builder().id(1L)
            .payload(taskPayload)
            .type(RequestTaskType.PERMIT_NOTIFICATION_APPLICATION_SUBMIT)
            .request(request)
            .build();
        RequestActionPayload actionPayload = PermitNotificationApplicationSubmittedRequestActionPayload.builder()
            .payloadType(RequestActionPayloadType.PERMIT_NOTIFICATION_APPLICATION_SUBMITTED_PAYLOAD)
            .permitNotification(permitNotification)
            .build();

        // Invoke
        service.applySubmitPayload(requestTask, authUser);

        // Verify
        verify(permitNotificationSubmitValidatorService, times(1))
            .validatePermitNotification(
                PermitNotificationContainer.builder().permitNotification(taskPayload.getPermitNotification()).build());
        verify(requestService, times(1)).saveRequest(request);
        verify(requestService, times(1))
            .addActionToRequest(request, actionPayload, RequestActionType.PERMIT_NOTIFICATION_APPLICATION_SUBMITTED,
                "user");
    }
}
