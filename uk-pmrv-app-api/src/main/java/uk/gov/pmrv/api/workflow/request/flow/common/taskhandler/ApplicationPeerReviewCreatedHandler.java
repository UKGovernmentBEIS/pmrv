package uk.gov.pmrv.api.workflow.request.flow.common.taskhandler;

import org.springframework.stereotype.Component;

import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskCreateService;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;

@Component
public class ApplicationPeerReviewCreatedHandler extends WorkflowDynamicExpirableUserTaskCreatedHandler {

    public ApplicationPeerReviewCreatedHandler(RequestTaskCreateService requestTaskCreateService) {
        super(requestTaskCreateService);
    }

    @Override
    public DynamicUserTaskDefinitionKey getTaskDefinition() {
        return DynamicUserTaskDefinitionKey.APPLICATION_PEER_REVIEW;
    }

    @Override
    public String getExpirationDateKey() {
        return BpmnProcessConstants.APPLICATION_REVIEW_EXPIRATION_DATE;
    }
}
