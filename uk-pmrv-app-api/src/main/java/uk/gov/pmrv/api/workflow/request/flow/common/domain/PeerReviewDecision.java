package uk.gov.pmrv.api.workflow.request.flow.common.domain;

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
public class PeerReviewDecision {

    @NotNull
    private PeerReviewDecisionType type;

    @NotBlank
    @Size(max = 10000)
    private String notes;
}
