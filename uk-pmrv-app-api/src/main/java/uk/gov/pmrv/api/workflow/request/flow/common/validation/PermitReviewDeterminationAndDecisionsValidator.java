package uk.gov.pmrv.api.workflow.request.flow.common.validation;

import javax.validation.constraints.NotNull;

import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.permit.DeterminationType;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.permit.PermitPayloadGroupDecidable;

public interface PermitReviewDeterminationAndDecisionsValidator<T extends PermitPayloadGroupDecidable<?>> {

    boolean isValid(T taskPayload);

    @NotNull
    DeterminationType getType();
    
    @NotNull
    RequestType getRequestType();
    
}
