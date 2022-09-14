package uk.gov.pmrv.api.workflow.request.flow.permitnotification.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import javax.validation.constraints.Future;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.pmrv.api.common.domain.dto.validation.SpELExpression;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@SpELExpression(expression = "{(#type eq 'AMENDS_NEEDED') == (#changesRequired?.length() gt 0)}",
    message = "permitNotification.follow.up.review.decision.amend.missing.changes.required")
@SpELExpression(expression = "{(#type eq 'AMENDS_NEEDED') || (#files == null)}",
    message = "permitNotification.follow.up.review.decision.not.amend.with.files")
@SpELExpression(expression = "{(#type eq 'AMENDS_NEEDED') || (#dueDate == null)}",
    message = "permitNotification.follow.up.review.decision.not.amend.with.due.date")
public class PermitNotificationFollowUpReviewDecision {

    @NotNull
    private PermitNotificationFollowUpReviewDecisionType type;

    @Size(max = 10000)
    private String notes;

    @Size(max = 10000)
    private String changesRequired;

    @Builder.Default
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private Set<UUID> files = new HashSet<>();
    
    @Future
    private LocalDate dueDate;
}
