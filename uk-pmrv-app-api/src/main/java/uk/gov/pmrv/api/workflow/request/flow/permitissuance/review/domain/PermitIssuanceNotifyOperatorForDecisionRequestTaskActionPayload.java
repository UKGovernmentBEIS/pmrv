package uk.gov.pmrv.api.workflow.request.flow.permitissuance.review.domain;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.DecisionNotification;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class PermitIssuanceNotifyOperatorForDecisionRequestTaskActionPayload extends RequestTaskActionPayload {

    @NotNull
    @Valid
    private DecisionNotification permitDecisionNotification;
}
