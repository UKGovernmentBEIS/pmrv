package uk.gov.pmrv.api.workflow.request.flow.permitnotification.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import uk.gov.pmrv.api.common.domain.dto.validation.SpELExpression;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@SpELExpression(expression = "(#type eq 'ACCEPTED') == (#followUp != null)", message = "permitNotification.reviewDecision.followUp")
public class PermitNotificationReviewDecision {

    @NotNull
    private PermitNotificationReviewDecisionType type;

    @NotBlank
    @Size(max = 10000)
    private String officialNotice;

    @Valid
    private FollowUp followUp;

    @Size(max = 10000)
    private String notes;
}
