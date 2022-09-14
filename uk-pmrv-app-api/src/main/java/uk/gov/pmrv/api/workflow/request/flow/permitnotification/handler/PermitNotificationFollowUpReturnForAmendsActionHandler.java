package uk.gov.pmrv.api.workflow.request.flow.permitnotification.handler;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.pmrv.api.common.domain.model.PmrvUser;
import uk.gov.pmrv.api.workflow.request.WorkflowService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.SubRequestType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.common.actionhandler.RequestTaskActionHandler;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.RequestTaskActionEmptyPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.ReviewOutcome;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestExpirationVarsBuilder;
import uk.gov.pmrv.api.workflow.request.flow.permitnotification.domain.PermitNotificationFollowUpApplicationReviewRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitnotification.domain.PermitNotificationFollowUpReturnedForAmendsRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitnotification.domain.PermitNotificationFollowUpReviewDecision;
import uk.gov.pmrv.api.workflow.request.flow.permitnotification.domain.PermitNotificationRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitnotification.service.PermitNotificationValidatorService;

@Component
@RequiredArgsConstructor
public class PermitNotificationFollowUpReturnForAmendsActionHandler implements RequestTaskActionHandler<RequestTaskActionEmptyPayload> {

    private final RequestTaskService requestTaskService;
    private final PermitNotificationValidatorService validatorService;
    private final RequestService requestService;
    private final WorkflowService workflowService;
    private final RequestExpirationVarsBuilder varsBuilder;

    @Override
    @Transactional
    public void process(final Long requestTaskId, 
                        final RequestTaskActionType requestTaskActionType, 
                        final PmrvUser pmrvUser, 
                        final RequestTaskActionEmptyPayload payload) {
        
        final RequestTask requestTask = requestTaskService.findTaskById(requestTaskId);
        final PermitNotificationFollowUpApplicationReviewRequestTaskPayload taskPayload =
            (PermitNotificationFollowUpApplicationReviewRequestTaskPayload) requestTask.getPayload();
        final PermitNotificationFollowUpReviewDecision reviewDecision = taskPayload.getReviewDecision();
        
        // if the regulator has not filled in a new due date, set it with the initial expiration date value
        final LocalDate expirationDate;
        if (reviewDecision.getDueDate() == null) {
            expirationDate = taskPayload.getFollowUpResponseExpirationDate();
            reviewDecision.setDueDate(expirationDate);
        } else {
            expirationDate = reviewDecision.getDueDate();
        }
        
        validatorService.validateReturnForAmends(reviewDecision);

        // update request payload
        final PermitNotificationRequestPayload requestPayload = (PermitNotificationRequestPayload) requestTask.getRequest().getPayload();
        requestPayload.setFollowUpReviewDecision(reviewDecision);
        requestPayload.setFollowUpResponseAttachments(taskPayload.getFollowUpAttachments());
        requestPayload.setFollowUpReviewSectionsCompleted(taskPayload.getReviewSectionsCompleted());

        // create timeline action
        this.createRequestAction(requestTask.getRequest(), pmrvUser, taskPayload);

        // close task
        final Date dueDate = Date.from(expirationDate
            .atTime(LocalTime.MIN)
            .atZone(ZoneId.systemDefault())
            .toInstant());
        final Map<String, Object> expirationDates =
            varsBuilder.buildExpirationVars(SubRequestType.FOLLOW_UP_RESPONSE, dueDate);
        final Map<String, Object> variables = new HashMap<>(expirationDates);
        variables.put(BpmnProcessConstants.REVIEW_OUTCOME, ReviewOutcome.AMENDS_NEEDED);
        // APPLICATION_REVIEW_EXPIRATION_DATE needed by AmendsSubmitCreatedHandler and WaitForAmendsCreatedHandler
        variables.put(BpmnProcessConstants.APPLICATION_REVIEW_EXPIRATION_DATE, dueDate);
        workflowService.completeTask(requestTask.getProcessTaskId(), variables);
    }

    private void createRequestAction(final Request request, 
                                     final PmrvUser pmrvUser,
                                     final PermitNotificationFollowUpApplicationReviewRequestTaskPayload taskPayload) {

        final PermitNotificationFollowUpReviewDecision reviewDecision = taskPayload.getReviewDecision();
        final Set<UUID> files = reviewDecision.getFiles();
        final Map<UUID, String> amendAttachments = taskPayload.getFollowUpAttachments().entrySet().stream()
            .filter(f -> files.contains(f.getKey())).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        final PermitNotificationFollowUpReturnedForAmendsRequestActionPayload actionPayload =
            PermitNotificationFollowUpReturnedForAmendsRequestActionPayload.builder()
                .payloadType(RequestActionPayloadType.PERMIT_NOTIFICATION_FOLLOW_UP_RETURNED_FOR_AMENDS_PAYLOAD)
                .notes(reviewDecision.getNotes())
                .changesRequired(reviewDecision.getChangesRequired())
                .dueDate(reviewDecision.getDueDate())
                .amendFiles(files)
                .amendAttachments(amendAttachments)
                .build();

        requestService.addActionToRequest(request,
            actionPayload, 
            RequestActionType.PERMIT_NOTIFICATION_FOLLOW_UP_RETURNED_FOR_AMENDS,
            pmrvUser.getUserId());
    }

    @Override
    public List<RequestTaskActionType> getTypes() {
        return List.of(RequestTaskActionType.PERMIT_NOTIFICATION_FOLLOW_UP_RETURN_FOR_AMENDS);
    }
}
