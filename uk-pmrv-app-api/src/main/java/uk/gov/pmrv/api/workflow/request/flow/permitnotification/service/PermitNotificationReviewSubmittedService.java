package uk.gov.pmrv.api.workflow.request.flow.permitnotification.service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Date;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.DecisionNotification;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestActionUserInfoResolver;
import uk.gov.pmrv.api.workflow.request.flow.permitnotification.domain.FollowUp;
import uk.gov.pmrv.api.workflow.request.flow.permitnotification.domain.PermitNotificationApplicationReviewSubmittedDecisionRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitnotification.domain.PermitNotificationFollowUpApplicationReviewSubmittedDecisionRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitnotification.domain.PermitNotificationRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitnotification.mapper.PermitNotificationMapper;

@Service
@RequiredArgsConstructor
public class PermitNotificationReviewSubmittedService {

    private final RequestService requestService;
    private final RequestActionUserInfoResolver requestActionUserInfoResolver;
    private final PermitNotificationOfficialNoticeService noticeService;
    
    private static final PermitNotificationMapper permitNotificationMapper = Mappers.getMapper(PermitNotificationMapper.class);

    public void executeGrantedPostActions(String requestId) {
        executePostActions(requestId, RequestActionPayloadType.PERMIT_NOTIFICATION_APPLICATION_GRANTED_PAYLOAD,
                RequestActionType.PERMIT_NOTIFICATION_APPLICATION_GRANTED);
    }

    public void executeRejectedPostActions(String requestId) {
        executePostActions(requestId, RequestActionPayloadType.PERMIT_NOTIFICATION_APPLICATION_REJECTED_PAYLOAD,
                RequestActionType.PERMIT_NOTIFICATION_APPLICATION_REJECTED);
    }

    private void executePostActions(String requestId, RequestActionPayloadType actionPayloadType, RequestActionType actionType) {
        Request request = requestService.findRequestById(requestId);
        PermitNotificationRequestPayload requestPayload = (PermitNotificationRequestPayload) request.getPayload();

        PermitNotificationApplicationReviewSubmittedDecisionRequestActionPayload actionPayload = permitNotificationMapper
                .toPermitNotificationApplicationReviewSubmittedDecisionRequestActionPayload(requestPayload, actionPayloadType);

        DecisionNotification reviewDecisionNotification = requestPayload.getReviewDecisionNotification();

        actionPayload.setUsersInfo(requestActionUserInfoResolver
            .getUsersInfo(reviewDecisionNotification.getOperators(), reviewDecisionNotification.getSignatory(), request)
        );

        // Add action
        requestService.addActionToRequest(request, actionPayload, actionType, requestPayload.getRegulatorReviewer());
        
        // send official notice 
        noticeService.sendOfficialNotice(request);
    }
    
    public ImmutablePair<Boolean, Date> getFollowUpInfo(final String requestId) {
        
        final Request request = requestService.findRequestById(requestId);
        final PermitNotificationRequestPayload requestPayload = (PermitNotificationRequestPayload) request.getPayload();
        final FollowUp followUp = requestPayload.getReviewDecision().getFollowUp();
        final Boolean followUpRequired = followUp.getFollowUpResponseRequired();
        final LocalDate expirationLocalDate = followUp.getFollowUpResponseExpirationDate();
        final Date expirationDate = expirationLocalDate != null ? 
            Date.from(expirationLocalDate.atTime(LocalTime.MIN).atZone(ZoneId.systemDefault()).toInstant())
            : null;

        return ImmutablePair.of(followUpRequired, expirationDate);
    }
    
    public void executeCompletedPostActions(final String requestId) {

        final Request request = requestService.findRequestById(requestId);
        final PermitNotificationRequestPayload requestPayload = (PermitNotificationRequestPayload) request.getPayload();
        final DecisionNotification followUpReviewDecisionNotification = requestPayload.getFollowUpReviewDecisionNotification();
        
        final PermitNotificationFollowUpApplicationReviewSubmittedDecisionRequestActionPayload actionPayload =
            PermitNotificationFollowUpApplicationReviewSubmittedDecisionRequestActionPayload.builder()
                .payloadType(RequestActionPayloadType.PERMIT_NOTIFICATION_APPLICATION_COMPLETED_PAYLOAD)
                .permitNotificationType(requestPayload.getPermitNotification().getType())
                .request(requestPayload.getReviewDecision().getFollowUp().getFollowUpRequest())
                .response(requestPayload.getFollowUpResponse())
                .responseFiles(requestPayload.getFollowUpResponseFiles())
                .responseAttachments(requestPayload.getFollowUpResponseAttachments())
                .responseExpirationDate(requestPayload.getReviewDecision().getFollowUp().getFollowUpResponseExpirationDate())
                .responseSubmissionDate(requestPayload.getFollowUpResponseSubmissionDate())
                .reviewDecision(requestPayload.getFollowUpReviewDecision())
                .reviewDecisionNotification(followUpReviewDecisionNotification)
                .build();


        actionPayload.setUsersInfo(requestActionUserInfoResolver
            .getUsersInfo(followUpReviewDecisionNotification.getOperators(), followUpReviewDecisionNotification.getSignatory(), request)
        );

        // add timeline action
        requestService.addActionToRequest(request,
            actionPayload,
            RequestActionType.PERMIT_NOTIFICATION_APPLICATION_COMPLETED,
            requestPayload.getRegulatorReviewer());
        
        // send official notice 
        noticeService.sendFollowUpOfficialNotice(request);
    }
}
