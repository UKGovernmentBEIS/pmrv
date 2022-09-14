package uk.gov.pmrv.api.permit.domain.monitoringapproaches.measurement;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.common.AppliedStandard;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.measurement.measuredemissions.MeasMeasuredEmissions;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MeasSourceStreamCategoryAppliedTier {

    @Valid
    @NotNull
    private MeasSourceStreamCategory sourceStreamCategory;

    @Valid
    @NotNull
    private MeasMeasuredEmissions measuredEmissions;

    @Valid
    @NotNull
    private AppliedStandard appliedStandard;
}
