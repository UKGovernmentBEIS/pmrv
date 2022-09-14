package uk.gov.pmrv.api.workflow.request.flow.permitsurrender.handler;

import java.util.Set;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.core.service.InitializeRequestTaskHandler;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.permit.cessation.PermitCessationContainer;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.permit.cessation.PermitCessationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitsurrender.domain.PermitSurrenderRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitsurrender.domain.PermitSurrenderReviewDeterminationGrant;

@Service
public class PermitSurrenderCessationSubmitInitializer implements InitializeRequestTaskHandler {

    @Override
    public RequestTaskPayload initializePayload(Request request) {
        PermitSurrenderRequestPayload permitSurrenderRequestPayload = (PermitSurrenderRequestPayload) request.getPayload();
        PermitSurrenderReviewDeterminationGrant permitSurrenderReviewDetermination =
            (PermitSurrenderReviewDeterminationGrant) permitSurrenderRequestPayload.getReviewDetermination();

        PermitCessationContainer cessationContainer = PermitCessationContainer.builder()
            .allowancesSurrenderRequired(permitSurrenderReviewDetermination.getAllowancesSurrenderRequired())
            .build();

        return PermitCessationSubmitRequestTaskPayload.builder()
            .payloadType(RequestTaskPayloadType.PERMIT_SURRENDER_CESSATION_SUBMIT_PAYLOAD)
            .cessationContainer(cessationContainer)
            .build();
    }

    @Override
    public Set<RequestTaskType> getRequestTaskTypes() {
        return Set.of(RequestTaskType.PERMIT_SURRENDER_CESSATION_SUBMIT);
    }
}
