package uk.gov.pmrv.api.workflow.request.flow.permitnotification.domain;

import com.fasterxml.jackson.annotation.JsonUnwrapped;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import uk.gov.pmrv.api.common.domain.dto.validation.SpELExpression;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@SpELExpression(expression = "{#reportingType != 'OTHER_ISSUE' || ((#startDateOfNonCompliance != null) == (#endDateOfNonCompliance != null))}",
        message = "permitNotification.endDateOfNonCompliance.startDateOfNonCompliance")
@SpELExpression(expression = "{{'EXCEEDED_THRESHOLD_STATED_GHGE_PERMIT','EXCEEDED_THRESHOLD_STATED_HSE_PERMIT'}.?[#this == #reportingType].empty || (#startDateOfNonCompliance != null && #endDateOfNonCompliance == null)}",
        message = "permitNotification.startDateOfNonCompliance.reportingType")
@SpELExpression(expression = "{#reportingType != 'RENOUNCE_FREE_ALLOCATIONS' || ((#startDateOfNonCompliance == null) && (#endDateOfNonCompliance == null))}",
        message = "permitNotification.startDateOfNonCompliance.reportingType")
public class OtherFactor extends PermitNotification {

    @NotNull
    private ReportingType reportingType;

    @JsonUnwrapped
    @Valid
    private DateOfNonCompliance dateOfNonCompliance;
}
