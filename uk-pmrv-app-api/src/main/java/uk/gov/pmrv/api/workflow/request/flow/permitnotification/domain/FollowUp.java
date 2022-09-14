package uk.gov.pmrv.api.workflow.request.flow.permitnotification.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import uk.gov.pmrv.api.common.domain.dto.validation.SpELExpression;

import javax.validation.constraints.Future;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@SpELExpression(expression = "T(java.lang.Boolean).TRUE.equals(#followUpResponseRequired) == (#followUpRequest != null)",
        message = "permitNotification.reviewDecision.followUpRequest")
@SpELExpression(expression = "T(java.lang.Boolean).TRUE.equals(#followUpResponseRequired) == (#followUpResponseExpirationDate != null)",
        message = "permitNotification.reviewDecision.followUpResponseExpirationDate")
public class FollowUp {

    @NotNull
    private Boolean followUpResponseRequired;

    @Size(max = 10000)
    private String followUpRequest;

    @Future
    private LocalDate followUpResponseExpirationDate;
}
