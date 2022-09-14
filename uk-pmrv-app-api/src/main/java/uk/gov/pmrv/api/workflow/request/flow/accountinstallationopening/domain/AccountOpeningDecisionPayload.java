package uk.gov.pmrv.api.workflow.request.flow.accountinstallationopening.domain;

import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.pmrv.api.common.domain.dto.validation.SpELExpression;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.Decision;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@SpELExpression(expression = "{(#decision eq 'ACCEPTED') || (#reason != null)}", message = "accountOpeningDecision.decision.reason")
public class AccountOpeningDecisionPayload {

    @NotNull(message = "{accountOpeningDecision.decision.notEmpty}")
    private Decision decision;

    private String reason;
}
