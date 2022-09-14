package uk.gov.pmrv.api.workflow.request.flow.permitvariation.domain;

import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.permit.PermitReviewGroup;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class PermitVariationSaveReviewGroupDecisionRequestTaskActionPayload
		extends PermitVariationSaveReviewGroupDecisionAbstractRequestTaskActionPayload {

    @NotNull
    private PermitReviewGroup group;

}
