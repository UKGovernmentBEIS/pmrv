package uk.gov.pmrv.api.workflow.request.flow.notificationsystemmessage.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import uk.gov.pmrv.api.common.domain.enumeration.CompetentAuthority;
import uk.gov.pmrv.api.common.domain.enumeration.RoleType;
import uk.gov.pmrv.api.notification.template.domain.NotificationContent;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestParams;
import uk.gov.pmrv.api.workflow.request.flow.notificationsystemmessage.domain.SystemMessageNotificationPayload;
import uk.gov.pmrv.api.workflow.request.flow.notificationsystemmessage.domain.SystemMessageNotificationRequestParams;
import uk.gov.pmrv.api.workflow.request.flow.notificationsystemmessage.domain.SystemMessageNotificationRequestPayload;

class SystemMessageNotificationParamsMapperTest {

    private SystemMessageNotificationParamsMapper mapper;

    @BeforeEach
    void init() {
        mapper = Mappers.getMapper(SystemMessageNotificationParamsMapper.class);
    }

    @Test
    void toRequestParams_operator_assignee() {
        final String notificationSubject = "notification subject";
        final String notificationText = "notification text";
        final String notificationRecipient = "notification recipient";
        SystemMessageNotificationRequestParams messageRequestParams = SystemMessageNotificationRequestParams.builder()
            .accountId(1L)
            .competentAuthority(CompetentAuthority.WALES)
            .verificationBodyId(2L)
            .notificationMessageRecipient(notificationRecipient)
            .notificationContent(NotificationContent.builder()
                .subject(notificationSubject)
                .text(notificationText)
                .build()
            )
            .build();

        SystemMessageNotificationRequestPayload systemMessageNotificationRequestPayload =
            SystemMessageNotificationRequestPayload.builder()
                .payloadType(RequestPayloadType.SYSTEM_MESSAGE_NOTIFICATION_REQUEST_PAYLOAD)
                .messagePayload(SystemMessageNotificationPayload.builder()
                    .subject(notificationSubject)
                    .text(notificationText)
                    .build()
                )
                .operatorAssignee(notificationRecipient)
                .build();


        RequestParams requestParams = mapper.toRequestParams(messageRequestParams, RoleType.OPERATOR);

        assertEquals(messageRequestParams.getAccountId(), requestParams.getAccountId());
        assertEquals(messageRequestParams.getCompetentAuthority(), requestParams.getCa());
        assertEquals(messageRequestParams.getVerificationBodyId(), requestParams.getVerificationBodyId());
        assertEquals(RequestType.SYSTEM_MESSAGE_NOTIFICATION, requestParams.getType());
        assertEquals(systemMessageNotificationRequestPayload, requestParams.getRequestPayload());
        assertNull(requestParams.getRequestPayload().getRegulatorAssignee());
        assertNull(requestParams.getRequestPayload().getVerifierAssignee());
    }

    @Test
    void toRequestParams_regulator_assignee() {
        final String notificationSubject = "notification subject";
        final String notificationText = "notification text";
        final String notificationRecipient = "notification recipient";
        SystemMessageNotificationRequestParams messageRequestParams = SystemMessageNotificationRequestParams.builder()
            .accountId(1L)
            .competentAuthority(CompetentAuthority.WALES)
            .verificationBodyId(2L)
            .notificationMessageRecipient(notificationRecipient)
            .notificationContent(NotificationContent.builder()
                .subject(notificationSubject)
                .text(notificationText)
                .build()
            )
            .build();

        SystemMessageNotificationRequestPayload systemMessageNotificationRequestPayload =
            SystemMessageNotificationRequestPayload.builder()
                .payloadType(RequestPayloadType.SYSTEM_MESSAGE_NOTIFICATION_REQUEST_PAYLOAD)
                .messagePayload(SystemMessageNotificationPayload.builder()
                    .subject(notificationSubject)
                    .text(notificationText)
                    .build()
                )
                .regulatorAssignee(notificationRecipient)
                .build();


        RequestParams requestParams = mapper.toRequestParams(messageRequestParams, RoleType.REGULATOR);

        assertEquals(messageRequestParams.getAccountId(), requestParams.getAccountId());
        assertEquals(messageRequestParams.getCompetentAuthority(), requestParams.getCa());
        assertEquals(messageRequestParams.getVerificationBodyId(), requestParams.getVerificationBodyId());
        assertEquals(RequestType.SYSTEM_MESSAGE_NOTIFICATION, requestParams.getType());
        assertEquals(systemMessageNotificationRequestPayload, requestParams.getRequestPayload());
        assertNull(requestParams.getRequestPayload().getOperatorAssignee());
        assertNull(requestParams.getRequestPayload().getVerifierAssignee());
    }

    @Test
    void toRequestParams_verifier_assignee() {
        final String notificationSubject = "notification subject";
        final String notificationText = "notification text";
        final String notificationRecipient = "notification recipient";
        SystemMessageNotificationRequestParams messageRequestParams = SystemMessageNotificationRequestParams.builder()
            .accountId(1L)
            .competentAuthority(CompetentAuthority.WALES)
            .verificationBodyId(2L)
            .notificationMessageRecipient(notificationRecipient)
            .notificationContent(NotificationContent.builder()
                .subject(notificationSubject)
                .text(notificationText)
                .build()
            )
            .build();

        SystemMessageNotificationRequestPayload systemMessageNotificationRequestPayload =
            SystemMessageNotificationRequestPayload.builder()
                .payloadType(RequestPayloadType.SYSTEM_MESSAGE_NOTIFICATION_REQUEST_PAYLOAD)
                .messagePayload(SystemMessageNotificationPayload.builder()
                    .subject(notificationSubject)
                    .text(notificationText)
                    .build()
                )
                .verifierAssignee(notificationRecipient)
                .build();


        RequestParams requestParams = mapper.toRequestParams(messageRequestParams, RoleType.VERIFIER);

        assertEquals(messageRequestParams.getAccountId(), requestParams.getAccountId());
        assertEquals(messageRequestParams.getCompetentAuthority(), requestParams.getCa());
        assertEquals(messageRequestParams.getVerificationBodyId(), requestParams.getVerificationBodyId());
        assertEquals(RequestType.SYSTEM_MESSAGE_NOTIFICATION, requestParams.getType());
        assertEquals(systemMessageNotificationRequestPayload, requestParams.getRequestPayload());
        assertNull(requestParams.getRequestPayload().getOperatorAssignee());
        assertNull(requestParams.getRequestPayload().getRegulatorAssignee());
    }
}