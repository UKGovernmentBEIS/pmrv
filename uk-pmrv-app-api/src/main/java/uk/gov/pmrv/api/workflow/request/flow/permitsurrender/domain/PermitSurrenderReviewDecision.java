package uk.gov.pmrv.api.workflow.request.flow.permitsurrender.domain;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PermitSurrenderReviewDecision {

    @NotNull
    private PermitSurrenderReviewDecisionType type;

    @NotBlank
    @Size(max = 10000)
    private String notes;
    
}
