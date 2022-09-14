package uk.gov.pmrv.api.workflow.request.flow.rde.handler;

import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.common.taskhandler.DynamicUserTaskDefinitionKey;
import uk.gov.pmrv.api.workflow.request.flow.common.taskhandler.DynamicUserTaskDeletedHandler;
import uk.gov.pmrv.api.workflow.request.flow.rde.service.RdeCancelledService;

@Component
@RequiredArgsConstructor
public class RdeWaitForResponseDeletedHandler implements DynamicUserTaskDeletedHandler {

    private final RdeCancelledService rdeCancelledService;

    @Override
    public void process(final String requestId, final Map<String, Object> variables) {

        if (!variables.containsKey(BpmnProcessConstants.RDE_OUTCOME)) {
            rdeCancelledService.cancel(requestId);
        }
    }

    @Override
    public DynamicUserTaskDefinitionKey getTaskDefinition() {
        return DynamicUserTaskDefinitionKey.WAIT_FOR_RDE_RESPONSE;
    }
}
