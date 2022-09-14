package uk.gov.pmrv.api.permit.domain.monitoringapproaches.common;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonUnwrapped;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.pmrv.api.common.domain.dto.validation.SpELExpression;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@SpELExpression(expression = "{#deviationFromAppliedStandardExist == (#deviationFromAppliedStandardDetails != null)}", 
    message = "permit.monitoringapproach.common.appliedStandard.deviationFromAppliedStandardExist")
public class AppliedStandard {

    @NotBlank
    @Size(max = 250)
    private String parameter;

    @NotBlank
    @Size(max = 50)
    private String appliedStandard;

    private boolean deviationFromAppliedStandardExist;

    @Size(max = 50)
    private String deviationFromAppliedStandardDetails;
    
    @Valid
    @JsonUnwrapped
    private Laboratory laboratory;

}
