package uk.gov.pmrv.api.workflow.request.flow.permitnotification.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import uk.gov.pmrv.api.common.transform.MapperConfig;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.flow.permitnotification.domain.PermitNotificationApplicationReviewRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitnotification.domain.PermitNotificationApplicationReviewSubmittedDecisionRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitnotification.domain.PermitNotificationApplicationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitnotification.domain.PermitNotificationApplicationSubmittedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitnotification.domain.PermitNotificationContainer;
import uk.gov.pmrv.api.workflow.request.flow.permitnotification.domain.PermitNotificationFollowUpApplicationReviewSubmittedDecisionRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitnotification.domain.PermitNotificationFollowUpReturnedForAmendsRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitnotification.domain.PermitNotificationFollowUpReviewDecision;
import uk.gov.pmrv.api.workflow.request.flow.permitnotification.domain.PermitNotificationRequestPayload;

@Mapper(componentModel = "spring", config = MapperConfig.class)
public interface PermitNotificationMapper {

    PermitNotificationContainer toPermitNotificationContainer(PermitNotificationApplicationSubmitRequestTaskPayload payload);

    @Mapping(target = "payloadType", expression = "java(uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionPayloadType.PERMIT_NOTIFICATION_APPLICATION_SUBMITTED_PAYLOAD)")
    @Mapping(target = "attachments", ignore = true)
    @Mapping(target = "permitNotificationAttachments", ignore = true)
    PermitNotificationApplicationSubmittedRequestActionPayload toApplicationSubmittedRequestActionPayload(PermitNotificationApplicationSubmitRequestTaskPayload payload);

    @Mapping(target = "payloadType", source = "payloadType")
    PermitNotificationApplicationReviewRequestTaskPayload toApplicationReviewRequestTaskPayload(
            PermitNotificationRequestPayload requestPayload, RequestTaskPayloadType payloadType);

    @Mapping(target = "payloadType", source = "payloadType")
    @Mapping(target = "reviewDecision.notes", ignore = true)
    PermitNotificationApplicationReviewSubmittedDecisionRequestActionPayload toPermitNotificationApplicationReviewSubmittedDecisionRequestActionPayload(
            PermitNotificationRequestPayload requestPayload, RequestActionPayloadType payloadType);

    @Mapping(target = "payloadType", source = "payloadType")
    @Mapping(target = "reviewDecision.notes", ignore = true)
    PermitNotificationFollowUpApplicationReviewSubmittedDecisionRequestActionPayload cloneCompletedPayloadIgnoreNotes(
        PermitNotificationFollowUpApplicationReviewSubmittedDecisionRequestActionPayload payload, RequestActionPayloadType payloadType);

    @Mapping(target = "payloadType", source = "payloadType")
    @Mapping(target = "notes", ignore = true)
    PermitNotificationFollowUpReturnedForAmendsRequestActionPayload cloneReturnedForAmendsIgnoreNotes(
        PermitNotificationFollowUpReturnedForAmendsRequestActionPayload payload, RequestActionPayloadType payloadType);

    @Mapping(target = "notes", ignore = true)
    PermitNotificationFollowUpReviewDecision cloneFollowUpReviewDecisionIgnoreNotes(
        PermitNotificationFollowUpReviewDecision decision);
}
