package uk.gov.pmrv.api.workflow.request.flow.notificationsystemmessage.mapper;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import uk.gov.pmrv.api.common.domain.enumeration.RoleType;
import uk.gov.pmrv.api.common.transform.MapperConfig;
import uk.gov.pmrv.api.notification.template.domain.NotificationContent;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestPayloadType;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestParams;
import uk.gov.pmrv.api.workflow.request.flow.notificationsystemmessage.domain.SystemMessageNotificationPayload;
import uk.gov.pmrv.api.workflow.request.flow.notificationsystemmessage.domain.SystemMessageNotificationRequestParams;
import uk.gov.pmrv.api.workflow.request.flow.notificationsystemmessage.domain.SystemMessageNotificationRequestPayload;

@Mapper(componentModel = "spring", config = MapperConfig.class)
public interface SystemMessageNotificationParamsMapper {

    @Mapping(target = "ca", source = "systemMessageNotificationRequestParams.competentAuthority")
    @Mapping(target = "type", expression = "java(uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType.SYSTEM_MESSAGE_NOTIFICATION)")
    RequestParams toRequestParams(SystemMessageNotificationRequestParams systemMessageNotificationRequestParams, RoleType recipientRoleType);

    @AfterMapping
    default void populatePayloadToRequestParams(SystemMessageNotificationRequestParams systemMessageNotificationRequestParams,
                                                RoleType recipientRoleType, @MappingTarget RequestParams requestParams) {
        NotificationContent requestNotificationContent = systemMessageNotificationRequestParams.getNotificationContent();
        SystemMessageNotificationRequestPayload requestPayload = SystemMessageNotificationRequestPayload.builder()
            .payloadType(RequestPayloadType.SYSTEM_MESSAGE_NOTIFICATION_REQUEST_PAYLOAD)
            .messagePayload(SystemMessageNotificationPayload.builder()
                .subject(requestNotificationContent.getSubject())
                .text(requestNotificationContent.getText())
                .build()
            )
            .build();

        switch (recipientRoleType) {
            case OPERATOR:
                requestPayload.setOperatorAssignee(systemMessageNotificationRequestParams.getNotificationMessageRecipient());
                break;
            case REGULATOR:
                requestPayload.setRegulatorAssignee(systemMessageNotificationRequestParams.getNotificationMessageRecipient());
                break;
            case VERIFIER:
                requestPayload.setVerifierAssignee(systemMessageNotificationRequestParams.getNotificationMessageRecipient());
                break;
            default:
                throw new UnsupportedOperationException(String.format("Can not assign request to user with role type %s", recipientRoleType));
        }

        requestParams.setRequestPayload(requestPayload);
    }
}
