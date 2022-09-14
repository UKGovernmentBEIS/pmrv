package uk.gov.pmrv.api.workflow.request.flow.permitnotification.domain;

import com.fasterxml.jackson.annotation.JsonUnwrapped;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import uk.gov.pmrv.api.common.domain.dto.validation.SpELExpression;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@SpELExpression(expression = "{#startDateOfNonCompliance != null && #endDateOfNonCompliance != null}", message = "permitNotification.dateOfNonCompliance.exist")
public class TemporaryChange extends PermitNotification {

    @NotBlank
    @Size(max=10000)
    private String justification;

    @JsonUnwrapped
    @Valid
    private DateOfNonCompliance dateOfNonCompliance;
}
