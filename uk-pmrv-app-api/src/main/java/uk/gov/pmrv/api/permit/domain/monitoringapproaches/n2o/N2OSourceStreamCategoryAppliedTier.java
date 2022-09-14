package uk.gov.pmrv.api.permit.domain.monitoringapproaches.n2o;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.common.AppliedStandard;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.n2o.measuredemissions.N2OMeasuredEmissions;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class N2OSourceStreamCategoryAppliedTier {

    @Valid
    @NotNull
    private N2OSourceStreamCategory sourceStreamCategory;

    @Valid
    @NotNull
    private N2OMeasuredEmissions measuredEmissions;

    @Valid
    @NotNull
    private AppliedStandard appliedStandard;
}
