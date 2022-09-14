package uk.gov.pmrv.api.workflow.bpmn.handler.permitnotification;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.SubRequestType;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestExpirationVarsBuilder;
import uk.gov.pmrv.api.workflow.request.flow.permitnotification.service.PermitNotificationReviewSubmittedService;

import java.util.Date;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PermitNotificationGrantedHandler implements JavaDelegate {

    private final PermitNotificationReviewSubmittedService reviewSubmittedService;
    private final RequestExpirationVarsBuilder requestExpirationVarsBuilder;

    @Override
    public void execute(DelegateExecution execution) {

        final String requestId = (String) execution.getVariable(BpmnProcessConstants.REQUEST_ID);
        reviewSubmittedService.executeGrantedPostActions(requestId);
        
        final ImmutablePair<Boolean, Date> followUpInfo = reviewSubmittedService.getFollowUpInfo(requestId);
        final Boolean followUpRequired = followUpInfo.getLeft();
        execution.setVariable(BpmnProcessConstants.FOLLOW_UP_RESPONSE_NEEDED, followUpRequired);

        if (Boolean.TRUE.equals(followUpRequired)) {
            final Date expirationDate = followUpInfo.getRight();
            Map<String, Object> expirationVars = requestExpirationVarsBuilder
                    .buildExpirationVars(SubRequestType.FOLLOW_UP_RESPONSE, expirationDate);
            execution.setVariables(expirationVars);
        }
    }
}
